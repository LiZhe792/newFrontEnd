package com.teach.javafx.controller;


import com.teach.javafx.controller.base.LocalDateStringConverter;
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

/**
 * StudentPracticeController 学生社会实践管理控制器
 * 对应 student_practice_panel.fxml
 * 负责学生社会实践数据的展示、添加、修改和删除操作
 */
public class StudentPracticeController extends ToolController {
    @FXML
    private TableView<Map> dataTableView;  //社会实践信息表

    @FXML
    private TableColumn<Map, String> practiceNameColumn;   //实践名称列
    @FXML
    private TableColumn<Map, String> startTimeColumn;      //开始时间列
    @FXML
    private TableColumn<Map, String> endTimeColumn;        //结束时间列
    @FXML
    private TableColumn<Map, String> organizationColumn;   //组织单位列
    @FXML
    private TableColumn<Map, String> studentNumColumn;     //学生学号列
    @FXML
    private TableColumn<Map, String> studentNameColumn;    //学生姓名列
    @FXML
    private TableColumn<Map, String> studentDeptColumn;    //学生院系列

    @FXML
    private TextField practiceNameField;      //实践名称输入域
    @FXML
    private DatePicker startTimePicker;       //开始时间选择器
    @FXML
    private DatePicker endTimePicker;         //结束时间选择器
    @FXML
    private TextField organizationField;      //组织单位输入域
    @FXML
    private TextField practiceContentField;   //实践内容输入域
    @FXML
    private TextField practiceResultField;    //实践成果输入域
    @FXML
    private TextField practiceSummaryField;   //实践总结输入域
    @FXML
    private TextField practicePlaceField;     //实践地点输入域
    @FXML
    private TextField studentNumField;        //学生学号输入域
    @FXML
    private TextField studentNameField;       //学生姓名输入域

    @FXML
    private TextField keywordTextField;       //查询关键词输入域

    private Integer practiceId = null;        //当前编辑的社会实践ID

    private ArrayList<Map> practiceList = new ArrayList<>();  //社会实践列表数据
    private ObservableList<Map> observableList = FXCollections.observableArrayList();  //表格数据

    /**
     * 初始化表格数据
     */
    private void setTableViewData() {
        observableList.clear();
        observableList.addAll(practiceList);
        dataTableView.setItems(observableList);
    }

    /**
     * 页面初始化方法
     */
    @FXML
    public void initialize() {
        // 初始化表格列
        practiceNameColumn.setCellValueFactory(new MapValueFactory<>("practiceName"));
        startTimeColumn.setCellValueFactory(new MapValueFactory<>("startTime"));
        endTimeColumn.setCellValueFactory(new MapValueFactory<>("endTime"));
        organizationColumn.setCellValueFactory(new MapValueFactory<>("organization"));
        studentNumColumn.setCellValueFactory(new MapValueFactory<>("studentNum"));
        studentNameColumn.setCellValueFactory(new MapValueFactory<>("studentName"));
        studentDeptColumn.setCellValueFactory(new MapValueFactory<>("studentDept"));

        // 设置表格选择监听
        TableView.TableViewSelectionModel<Map> tsm = dataTableView.getSelectionModel();
        ObservableList<Integer> list = tsm.getSelectedIndices();
        list.addListener(this::onTableRowSelect);

        // 初始化日期转换器
        startTimePicker.setConverter(new LocalDateStringConverter("yyyy-MM-dd"));
        endTimePicker.setConverter(new LocalDateStringConverter("yyyy-MM-dd"));



        // 加载社会实践列表
        loadPracticeList("");
    }

    /**
     * 加载实践类型列表
     */


    /**
     * 加载社会实践列表
     */
    private void loadPracticeList(String keyword) {
        DataRequest req = new DataRequest();
        req.add("keyword", keyword);
        DataResponse res = HttpRequestUtil.request("/api/practice/list", req);

        if (res != null && res.getCode() == 0) {
            practiceList = (ArrayList<Map>) res.getData();
            setTableViewData();
        }
    }

    /**
     * 清除表单数据
     */
    public void clearPanel() {
        practiceId = null;
        practiceNameField.setText("");
        startTimePicker.getEditor().setText("");
        endTimePicker.getEditor().setText("");
        organizationField.setText("");
        practiceContentField.setText("");
        practiceResultField.setText("");
        practiceSummaryField.setText("");
        practicePlaceField.setText("");
        studentNumField.setText("");
        studentNameField.setText("");
    }

    /**
     * 表格行选择事件处理
     */
    public void onTableRowSelect(ListChangeListener.Change<? extends Integer> change) {
        Map<String, Object> selectedItem = dataTableView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            clearPanel();
            return;
        }

        practiceId = CommonMethod.getInteger(selectedItem, "practiceId");
        loadPracticeInfo(practiceId);
    }

    /**
     * 加载社会实践详情
     */
    private void loadPracticeInfo(Integer practiceId) {
        DataRequest req = new DataRequest();
        req.add("practiceId", practiceId);
        DataResponse res = HttpRequestUtil.request("/api/practice/info", req);

        if (res != null && res.getCode() == 0) {
            Map<String, Object> practice = (Map<String, Object>) res.getData();
            fillFormData(practice);
        }
    }

    /**
     * 填充表单数据
     */
    private void fillFormData(Map<String, Object> practice) {
        practiceId = CommonMethod.getInteger(practice, "practiceId");
        practiceNameField.setText(CommonMethod.getString(practice, "practiceName"));
        startTimePicker.getEditor().setText(CommonMethod.getString(practice, "startTime"));
        endTimePicker.getEditor().setText(CommonMethod.getString(practice, "endTime"));
        organizationField.setText(CommonMethod.getString(practice, "organization"));
        practiceContentField.setText(CommonMethod.getString(practice, "practiceContent"));
        practiceResultField.setText(CommonMethod.getString(practice, "practiceResult"));
        practiceSummaryField.setText(CommonMethod.getString(practice, "practiceSummary"));
        practicePlaceField.setText(CommonMethod.getString(practice, "practicePlace"));
        studentNumField.setText(CommonMethod.getString(practice, "studentNum"));
        studentNameField.setText(CommonMethod.getString(practice, "studentName"));



    }

    /**
     * 查询按钮点击事件
     */
    @FXML
    protected void onQueryButtonClick() {
        String keyword = keywordTextField.getText();
        loadPracticeList(keyword);
    }

    /**
     * 添加按钮点击事件
     */
    @FXML
    protected void onAddButtonClick() {
        clearPanel();
    }

    /**
     * 删除按钮点击事件
     */
    @FXML
    protected void onDeleteButtonClick() {
        if (practiceId == null) {
            MessageDialog.showDialog("请选择要删除的社会实践记录");
            return;
        }

        int ret = MessageDialog.choiceDialog("确认要删除这条社会实践记录吗?");
        if (ret != MessageDialog.CHOICE_YES) {
            return;
        }

        DataRequest req = new DataRequest();
        req.add("practiceId", practiceId);
        DataResponse res = HttpRequestUtil.request("/api/practice/delete", req);

        if (res != null && res.getCode() == 0) {
            MessageDialog.showDialog("删除成功！");
            loadPracticeList(keywordTextField.getText());
            clearPanel();
        } else {
            MessageDialog.showDialog(res != null ? res.getMsg() : "删除失败！");
        }
    }

    /**
     * 保存按钮点击事件
     */
    @FXML
    protected void onSaveButtonClick() {
        if (practiceNameField.getText().isEmpty()) {
            MessageDialog.showDialog("实践名称不能为空");
            return;
        }

        if (studentNumField.getText().isEmpty()) {
            MessageDialog.showDialog("学生学号不能为空");
            return;
        }

        Map<String, Object> form = new HashMap<>();
        form.put("practiceName", practiceNameField.getText());
        form.put("startTime", startTimePicker.getEditor().getText());
        form.put("endTime", endTimePicker.getEditor().getText());
        form.put("organization", organizationField.getText());
        form.put("practiceContent", practiceContentField.getText());
        form.put("practiceResult", practiceResultField.getText());
        form.put("practiceSummary", practiceSummaryField.getText());
        form.put("practicePlace", practicePlaceField.getText());
        form.put("studentNum", studentNumField.getText()); // 实际应通过学号查询学生ID

        DataRequest req = new DataRequest();
        req.add("practiceId", practiceId);
        req.add("form", form);

        DataResponse res = HttpRequestUtil.request("/api/practice/editSave", req);

        if (res != null && res.getCode() == 0) {
            practiceId = CommonMethod.getIntegerFromObject(res.getData());
            MessageDialog.showDialog("保存成功！");
            loadPracticeList(keywordTextField.getText());
        } else {
            MessageDialog.showDialog(res != null ? res.getMsg() : "保存失败！");
        }
    }

    /**
     * 分页获取数据
     */
    @FXML


    /**
     * 新建操作
     */
    public void doNew() {
        clearPanel();
    }

    /**
     * 保存操作
     */
    public void doSave() {
        onSaveButtonClick();
    }

    /**
     * 删除操作
     */
    public void doDelete() {
        onDeleteButtonClick();
    }

    /**
     * 导出操作
     */
    public void doExport() {
        // 实现导出逻辑
    }
}