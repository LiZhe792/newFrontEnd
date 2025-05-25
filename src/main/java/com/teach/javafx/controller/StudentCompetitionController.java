package com.teach.javafx.controller;

import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.util.CommonMethod;
import com.teach.javafx.controller.base.MessageDialog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * StudentCompetitionController 学生学科竞赛管理控制类
 * 对应 student_competition_panel.fxml
 * 主要实现学科竞赛的增删改查，并与学生学号姓名关联
 */
public class StudentCompetitionController extends ToolController {
    @FXML
    private TableView<Map> competitionTableView;  //竞赛信息表
    @FXML
    private TableColumn<Map, String> numColumn; //学号列
    @FXML
    private TableColumn<Map, String> nameColumn; //姓名列
    @FXML
    private TableColumn<Map, String> competitionNameColumn; //竞赛名称列
    @FXML
    private TableColumn<Map, String> awardLevelColumn; //获奖等级列
    @FXML
    private TableColumn<Map, String> awardDateColumn; //获奖日期列

    @FXML
    private ComboBox<Map> studentComboBox; //学生下拉框
    @FXML
    private TextField competitionNameField; //竞赛名称输入域
    @FXML
    private TextField awardLevelField; //获奖等级输入域
    @FXML
    private TextField awardDateField; //获奖日期输入域

    @FXML
    private TextField numNameTextField; //查询 学号/姓名输入域

    private Integer selectedId = null; //当前选中竞赛记录id
    private ObservableList<Map> competitionList = FXCollections.observableArrayList();  // TableView渲染列表
    private ObservableList<Map> studentList = FXCollections.observableArrayList(); // 学生下拉框数据

    /**
     * 页面加载对象创建完成初始化方法
     */
    @FXML
    public void initialize() {
        numColumn.setCellValueFactory(new MapValueFactory<>("num"));
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        competitionNameColumn.setCellValueFactory(new MapValueFactory<>("competitionName"));
        awardLevelColumn.setCellValueFactory(new MapValueFactory<>("awardLevel"));
        awardDateColumn.setCellValueFactory(new MapValueFactory<>("awardDate"));
        competitionTableView.setItems(competitionList);

        // 学生下拉框显示学号+姓名
        studentComboBox.setItems(studentList);
        studentComboBox.setCellFactory(param -> new ListCell<Map>() {
            @Override
            protected void updateItem(Map item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.get("num") + " " + item.get("name"));
                }
            }
        });
        studentComboBox.setButtonCell(new ListCell<Map>() {
            @Override
            protected void updateItem(Map item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.get("num") + " " + item.get("name"));
                }
            }
        });

        competitionTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                selectedId = CommonMethod.getInteger(newSel, "id");
                // 选中行时，自动选中下拉框对应学生
                for (Map stu : studentList) {
                    if (stu.get("personId").equals(newSel.get("personId"))) {
                        studentComboBox.getSelectionModel().select(stu);
                        break;
                    }
                }
                competitionNameField.setText(CommonMethod.getString(newSel, "competitionName"));
                awardLevelField.setText(CommonMethod.getString(newSel, "awardLevel"));
                awardDateField.setText(CommonMethod.getString(newSel, "awardDate"));
            }
        });

        // 初始化加载学生列表
        loadStudentList("");
        // 初始化加载竞赛列表
        onQueryButtonClick();
    }

    /**
     * 加载学生下拉框数据
     */
    private void loadStudentList(String numName) {
        DataRequest req = new DataRequest();
        req.add("numName", numName);
        DataResponse res = HttpRequestUtil.request("/api/student/getStudentList", req);
        studentList.clear();
        if (res != null && res.getCode() == 0) {
            List<Map> list = (List<Map>) res.getData();
            studentList.addAll(list);
        }
    }

    /**
     * 清除竞赛表单中输入信息
     */
    public void clearPanel() {
        selectedId = null;
        studentComboBox.getSelectionModel().clearSelection();
        competitionNameField.setText("");
        awardLevelField.setText("");
        awardDateField.setText("");
        competitionTableView.getSelectionModel().clearSelection();
    }

    /**
     * 点击添加按钮，清空输入信息
     */
    @FXML
    protected void onAddButtonClick() {
        clearPanel();
    }

    /**
     * 点击保存按钮，保存当前编辑的竞赛信息
     */
    @FXML
    protected void onSaveButtonClick() {
        Map stu = studentComboBox.getSelectionModel().getSelectedItem();
        if (stu == null) {
            MessageDialog.showDialog("请选择学生！");
            return;
        }
        if (competitionNameField.getText().isEmpty()) {
            MessageDialog.showDialog("竞赛名称不能为空！");
            return;
        }
        Map<String, Object> form = new HashMap<>();
        form.put("id", selectedId);
        form.put("personId", stu.get("personId"));
        form.put("competitionName", competitionNameField.getText());
        form.put("awardLevel", awardLevelField.getText());
        form.put("awardDate", awardDateField.getText());
        DataRequest req = new DataRequest();
        req.add("form", form);
        DataResponse res = HttpRequestUtil.request("/api/studentCompetition/saveCompetition", req);
        if (res != null && res.getCode() == 0) {
            MessageDialog.showDialog("保存成功！");
            onQueryButtonClick();
            clearPanel();
        } else if (res != null) {
            MessageDialog.showDialog(res.getMsg());
        }
    }

    /**
     * 点击删除按钮，删除当前选中的竞赛记录
     */
    @FXML
    protected void onDeleteButtonClick() {
        Map form = competitionTableView.getSelectionModel().getSelectedItem();
        if (form == null) {
            MessageDialog.showDialog("没有选择，不能删除");
            return;
        }
        int ret = MessageDialog.choiceDialog("确认要删除吗?");
        if (ret != MessageDialog.CHOICE_YES) {
            return;
        }
        Integer id = CommonMethod.getInteger(form, "id");
        DataRequest req = new DataRequest();
        req.add("id", id);
        DataResponse res = HttpRequestUtil.request("/api/studentCompetition/deleteCompetition", req);
        if (res != null && res.getCode() == 0) {
            MessageDialog.showDialog("删除成功！");
            onQueryButtonClick();
            clearPanel();
        } else if (res != null) {
            MessageDialog.showDialog(res.getMsg());
        }
    }

    /**
     * 点击查询按钮，根据学号/姓名模糊查询
     */
    @FXML
    protected void onQueryButtonClick() {
        String numName = numNameTextField.getText();
        // 先刷新学生下拉框
        loadStudentList(numName);

        DataRequest req = new DataRequest();
        req.add("numName", numName);
        DataResponse res = HttpRequestUtil.request("/api/studentCompetition/getCompetitionList", req);
        competitionList.clear();
        if (res != null && res.getCode() == 0) {
            List<Map> list = (List<Map>) res.getData();
            competitionList.addAll(list);
        }
    }

    /**
     * doNew() doSave() doDelete() 重写 ToolController 中的方法，实现选择 新建，保存，删除
     */
    public void doNew() {
        clearPanel();
    }

    public void doSave() {
        onSaveButtonClick();
    }

    public void doDelete() {
        onDeleteButtonClick();
    }
}
