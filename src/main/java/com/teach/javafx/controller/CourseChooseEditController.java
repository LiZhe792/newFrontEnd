package com.teach.javafx.controller;

import com.teach.javafx.request.OptionItem;
import com.teach.javafx.util.CommonMethod;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseChooseEditController {
    @FXML
    private ComboBox<OptionItem> studentComboBox;
    private List<OptionItem> studentList;
    @FXML
    private ComboBox<OptionItem> courseComboBox;
    private List<OptionItem> courseList;

    @FXML
    private TextField teacherField;
    private CourseChooseTableController courseChooseTableController;

    private Integer id=null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id){
        this.id = id;
    }
    public ComboBox<OptionItem> getStudentComboBox() {
        return studentComboBox;
    }

    public void setStudentComboBox(ComboBox<OptionItem> studentComboBox) {
        this.studentComboBox = studentComboBox;
    }

    public List<OptionItem> getStudentList() {
        return studentList;
    }

    public void setStudentList(List<OptionItem> studentList) {
        this.studentList = studentList;
    }

    public ComboBox<OptionItem> getCourseComboBox() {
        return courseComboBox;
    }

    public void setCourseComboBox(ComboBox<OptionItem> courseComboBox) {
        this.courseComboBox = courseComboBox;
    }

    public List<OptionItem> getCourseList() {
        return courseList;
    }

    public void setCourseList(List<OptionItem> courseList) {
        this.courseList = courseList;
    }

    public CourseChooseTableController  getCourseChooseTableController() {
        return courseChooseTableController;
    }

    public void setCourseChooseTableController(CourseChooseTableController courseChooseTableController) {
        this.courseChooseTableController = courseChooseTableController;
    }

    @FXML
    private void initialize() {

    }
    @FXML
    public void showDialog(Map data) {
        if(data == null) {
            id = null;
            studentComboBox.getSelectionModel().select(-1);
            courseComboBox.getSelectionModel().select(-1);
            studentComboBox.setDisable(false);
            courseComboBox.setDisable(false);
        }else {
            id = CommonMethod.getInteger(data,"id");
            studentComboBox.getSelectionModel().select(CommonMethod.getOptionItemIndexByValue(studentList, CommonMethod.getString(data, "studentId")));
            courseComboBox.getSelectionModel().select(CommonMethod.getOptionItemIndexByValue(courseList, CommonMethod.getString(data, "courseId")));
            studentComboBox.setDisable(true);
            courseComboBox.setDisable(true);
            teacherField.setText(CommonMethod.getString(data,"teacher"));
        }
    }
    @FXML
    public void okButtonClick() {
        Map data = new HashMap();
        OptionItem op;
        op = studentComboBox.getSelectionModel().getSelectedItem();
        if(op != null) {
            data.put("studentId",Integer.parseInt(op.getValue()));
        }
        op = courseComboBox.getSelectionModel().getSelectedItem();
        if(op != null) {
            data.put("courseId", Integer.parseInt(op.getValue()));
        }
        data.put("id",id);
        data.put("teacher",teacherField.getText());
        courseChooseTableController.doClose("ok",data);
    }
    @FXML
    public void cancelButtonClick() {
        courseChooseTableController.doClose("cancel",null);
    }
    public void init(){
        studentList=courseChooseTableController.getStudentList();
        courseList=courseChooseTableController.getCourseList();
        studentComboBox.getItems().addAll(studentList);
        courseComboBox.getItems().addAll(courseList);
    }
}
