package com.teach.javafx.controller;
import com.teach.javafx.controller.ToolController;
import com.teach.javafx.request.*;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.util.CommonMethod;
import com.teach.javafx.controller.base.MessageDialog;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FamilyMemberController extends ToolController {
    @FXML
    private TableView<Map> dataTableView;  // 家庭成员信息表
    @FXML
    private TableColumn<Map, String> studentNameColumn;//家庭成员信息表 学生姓名列
    @FXML
    private TableColumn<Map, String> relationColumn;  // 家庭成员信息表 关系列
    @FXML
    private TableColumn<Map, String> nameColumn;  // 家庭成员信息表 姓名列
    @FXML
    private TableColumn<Map, String> genderColumn;  // 家庭成员信息表 性别列
    @FXML
    private TableColumn<Map, String> ageColumn;  // 家庭成员信息表 年龄列
    @FXML
    private TableColumn<Map, String> unitColumn;  // 家庭成员信息表 单位列

    @FXML
    private TextField studentPersonId1TextField;
    @FXML
    private TextField studentPersonIdTextField;  // 家庭成员信息 学生personId输入域
    @FXML
    private TextField nameField;  // 家庭成员信息  学生姓名输入域
    @FXML
    private TextField relationField;  // 家庭成员信息  关系输入域
    @FXML
    private TextField familyNameField;  // 家庭成员信息  姓名输入域

    @FXML
    private ComboBox<OptionItem> genderComboBox;  // 家庭成员信息  性别输入域
    @FXML
    private TextField ageField;  // 家庭成员信息  年龄输入域
    @FXML
    private TextField unitField;  // 家庭成员信息  单位输入域

    private Integer familyMemberId = null;  // 当前编辑修改的家庭成员的主键
    private ArrayList<Map> familyMemberList = new ArrayList<>();  // 家庭成员信息列表数据
    private List<OptionItem> genderList;  // 性别选择列表数据
    private ObservableList<Map> observableList = FXCollections.observableArrayList();  // TableView渲染列表

    /**
     * 将家庭成员数据集合设置到面板上显示
     */
    private void setTableViewData() {
        observableList.clear();
        for (Map member : familyMemberList) {
            observableList.add(member);
        }
        dataTableView.setItems(observableList);
    }

    /**
     * 页面加载对象创建完成初始化方法，页面中控件属性的设置，初始数据显示等初始操作都在这里完成，其他代码都事件处理方法里
     */
    @FXML
    public void initialize() {
        DataResponse res;
        DataRequest req = new DataRequest();
        req.add("numName", "");
        res = HttpRequestUtil.request("/api/student/getFamilyMemberList", req); // 从后台获取所有家庭成员信息列表集合
        if (res != null && res.getCode() == 0) {
            familyMemberList = (ArrayList<Map>) res.getData();
        }
        studentNameColumn.setCellValueFactory(new MapValueFactory<>("studentName"));
        relationColumn.setCellValueFactory(new MapValueFactory<>("relation"));  // 设置列值工程属性
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        genderColumn.setCellValueFactory(new MapValueFactory<>("gender"));
        ageColumn.setCellValueFactory(new MapValueFactory<>("age"));
        unitColumn.setCellValueFactory(new MapValueFactory<>("unit"));

        TableView.TableViewSelectionModel<Map> tsm = dataTableView.getSelectionModel();
        ObservableList<Integer> list = tsm.getSelectedIndices();
        list.addListener(this::onTableRowSelect);

        setTableViewData();

        genderList = HttpRequestUtil.getDictionaryOptionItemList("XBM");
        genderComboBox.getItems().addAll(genderList);

    }

    /**
     * 清除家庭成员表单中输入信息
     */
    public void clearPanel() {
        familyMemberId = null;
        studentPersonIdTextField.setText("");
        nameField.setText("");
        relationField.setText("");
        familyNameField.setText("");
        nameField.setText("");
        genderComboBox.getSelectionModel().select(-1);
        ageField.setText("");
        unitField.setText("");
    }

    /**
     * 根据选择的行更新家庭成员信息
     */
    protected void changeFamilyMemberInfo() {
        Map<String, Object> form = dataTableView.getSelectionModel().getSelectedItem();
        if (form == null) {
            clearPanel();
            return;
        }
        familyMemberId = CommonMethod.getInteger(form, "memberId");
        DataRequest req = new DataRequest();
        req.add("familyMemberId", familyMemberId);
        DataResponse res = HttpRequestUtil.request("/api/student/getFamilyInfo", req); // 假设存在该接口
        if (res.getCode() != 0) {
            MessageDialog.showDialog(res.getMsg());
            return;
        }
        form = (Map) res.getData();
        studentPersonIdTextField.setText(CommonMethod.getString(form, "studentNum"));
        nameField.setText(CommonMethod.getString(form, "studentName"));
        relationField.setText(CommonMethod.getString(form, "relation"));
        familyNameField.setText(CommonMethod.getString(form, "name"));
        genderComboBox.getSelectionModel().select(CommonMethod.getOptionItemIndexByValue(genderList, CommonMethod.getString(form, "gender")));
        ageField.setText(CommonMethod.getString(form, "age"));
        unitField.setText(CommonMethod.getString(form, "unit"));
    }

    /**
     * 点击家庭成员列表的某一行，根据familyMemberId ,从后台查询家庭成员的基本信息，切换家庭成员的编辑信息
     */
    public void onTableRowSelect(ListChangeListener.Change<? extends Integer> change) {
        changeFamilyMemberInfo();
    }

    /**
     * 点击查询按钮，从从后台根据输入的学生personId，查询匹配的家庭成员在家庭成员列表中显示
     */
    @FXML
    protected void onQueryButtonClick() {
        String studentPerson1Id = studentPersonId1TextField.getText();
        DataRequest req = new DataRequest();
        req.add("studentNum", studentPerson1Id);
        DataResponse res = HttpRequestUtil.request("/api/student/getFamilyMemberList", req);
        if (res != null && res.getCode() == 0) {
            familyMemberList = (ArrayList<Map>) res.getData();
            setTableViewData();
        }
    }

    /**
     * 添加新家庭成员， 清空输入信息， 输入相关信息，点击保存即可添加新的家庭成员
     */
    @FXML
    protected void onAddButtonClick() {
        clearPanel();
    }

    /**
     * 点击删除按钮 删除当前编辑的家庭成员的数据
     */
    @FXML
    protected void onDeleteButtonClick() {
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        if (form == null) {
            MessageDialog.showDialog("没有选择，不能删除");
            return;
        }
        int ret = MessageDialog.choiceDialog("确认要删除吗?");
        if (ret != MessageDialog.CHOICE_YES) {
            return;
        }
        familyMemberId = CommonMethod.getInteger(form, "memberId");
        DataRequest req = new DataRequest();
        req.add("memberId", familyMemberId);
        DataResponse res = HttpRequestUtil.request("/api/student/familyMemberDelete", req);
        if (res != null) {
            if (res.getCode() == 0) {
                MessageDialog.showDialog("删除成功！");
                onQueryButtonClick();
            } else {
                MessageDialog.showDialog(res.getMsg());
            }
        }
    }

    /**
     * 点击保存按钮，保存当前编辑的家庭成员信息，如果是新添加的家庭成员，后台添加家庭成员
     */
    @FXML

    protected void onSaveButtonClick() {
        if (familyNameField.getText().isEmpty()) {
            MessageDialog.showDialog("姓名为空，不能修改");
            return;
        }
        Map<String, Object> form = new HashMap<>();
        form.put("studentNum", studentPersonIdTextField.getText());
        form.put("name", familyNameField.getText());
        form.put("relation", relationField.getText());
        if (genderComboBox.getSelectionModel() != null && genderComboBox.getSelectionModel().getSelectedItem() != null) {
            form.put("gender", genderComboBox.getSelectionModel().getSelectedItem().getValue());
        }
        form.put("age", ageField.getText());
        form.put("unit", unitField.getText());
        DataRequest req = new DataRequest();


        req.add("memberId", familyMemberId);
        req.add("form", form);
        DataResponse res = HttpRequestUtil.request("/api/student/familyMemberSave", req);

        // 检查 res 是否为 null
        if (res == null) {
            MessageDialog.showDialog("保存请求失败，未收到有效响应，请检查网络或服务器状态。");
            return;
        }

        if (res.getCode() == 0) {
            familyMemberId = CommonMethod.getIntegerFromObject(res.getData());
            MessageDialog.showDialog("提交成功！");
            onQueryButtonClick();
        } else {
            MessageDialog.showDialog(res.getMsg());
        }
    }

    /**
     * doNew() doSave() doDelete() 重写 ToolController 中的方法， 实现选择 新建，保存，删除 对家庭成员的增，删，改操作
     */
    @Override
    public void doNew() {
        clearPanel();
    }

    @Override
    public void doSave() {
        onSaveButtonClick();
    }

    @Override
    public void doDelete() {
        onDeleteButtonClick();
    }
}