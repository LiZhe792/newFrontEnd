package com.teach.javafx.controller;

import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.OptionItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MarksController {
    @FXML
    private TableView<Map> dataTableView;
    @FXML
    private TableColumn<Map,String> studentNumColumn;
    @FXML
    private TableColumn<Map,String> studentNameColumn;
    @FXML
    private TableColumn<Map,String> courseNameColumn;
    @FXML
    private TableColumn<Map,String> creditColumn;
    @FXML
    private TableColumn<Map,String> teacherColumn;
    @FXML
    private TableColumn<Map, String> ranksColumn;
    @FXML
    private TableColumn<Map,String> marksColumn;
    @FXML
    private TableColumn<Map, Button> GPAColumn;
    @FXML
    private ComboBox<OptionItem> inputStudentComboBox;
    @FXML
    private ComboBox<OptionItem> inputCourseComboBox;
    private List<OptionItem> studentList;
    @FXML
    private ComboBox<OptionItem> courseComboBox;


    private ObservableList<Map> observableList= FXCollections.observableArrayList();

    private ArrayList<Map> marksList=new ArrayList<>();
    private List<OptionItem> courseList;
    private String num;
    private String userName;

    String userType;
    @FXML
    private Label avgMarks;
    @FXML
    private Label avgGPA;
    @FXML
    private Label totalRanks;

    private Stage stage = null;
    private Integer marksId=null;

    public List<OptionItem> getStudentList() {
        return studentList;
    }
    public List<OptionItem> getCourseList() {
        return courseList;
    }
    @FXML
    public void initialize() {
        DataResponse userResponse = HttpRequestUtil.request("/secure/user/getUser", new DataRequest());
        if(userResponse==null){
            MessageDialog.showDialog("连接失败，请重试");
        }
        Map data= (Map) userResponse.getData();
        userType = (String) data.get("typeId");
        num= (String) data.get("personNum");
        userName=(String) data.get("userName");

        if(userType==null){
            MessageDialog.showDialog("访问失败");
            return;
        }
        if(userType.equals("管理员")){
            studentNumColumn.setCellValueFactory(new MapValueFactory("studentNum"));  //设置列值工程属性
            studentNameColumn.setCellValueFactory(new MapValueFactory<>("studentName"));
            courseNameColumn.setCellValueFactory(new MapValueFactory<>("courseName"));
            creditColumn.setCellValueFactory(new MapValueFactory<>("credit"));
            teacherColumn.setCellValueFactory(new MapValueFactory<>("teacher"));
            marksColumn.setCellValueFactory(new MapValueFactory<>("marks"));
            ranksColumn.setCellValueFactory(new MapValueFactory<>("ranks"));
            GPAColumn.setCellValueFactory(new MapValueFactory<>("GPA"));
            OptionItem item = new OptionItem(null,"0","请选择");
            DataRequest req=new DataRequest();
            studentList=HttpRequestUtil.requestOptionItemList("/api/marks/getStudentItemOptionList",req);
            courseList=HttpRequestUtil.requestOptionItemList("/api/marks/getCourseItemOptionList",req);
            inputStudentComboBox.getItems().addAll(item);
            inputStudentComboBox.getItems().addAll(studentList);
            inputCourseComboBox.getItems().addAll(item);
            inputCourseComboBox.getItems().addAll(courseList);
            dataTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            onQueryButtonClick();
        }else {

            studentNumColumn.setCellValueFactory(new MapValueFactory("studentNum"));  //设置列值工程属性
            studentNameColumn.setCellValueFactory(new MapValueFactory<>("studentName"));
            courseNameColumn.setCellValueFactory(new MapValueFactory<>("courseName"));
            creditColumn.setCellValueFactory(new MapValueFactory<>("credit"));
            teacherColumn.setCellValueFactory(new MapValueFactory<>("teacher"));
            marksColumn.setCellValueFactory(new MapValueFactory<>("marks"));
            ranksColumn.setCellValueFactory(new MapValueFactory<>("ranks"));
            GPAColumn.setCellValueFactory(new MapValueFactory<>("GPA"));
            OptionItem item = new OptionItem(null,"0","请选择");
            DataRequest req=new DataRequest();
            //studentList=HttpRequestUtil.requestOptionItemList("/api/marks/getStudentItemOptionList",req);
            courseList=HttpRequestUtil.requestOptionItemList("/api/marks/getCourseItemOptionList",req);
            inputCourseComboBox.getItems().addAll(item);
            inputCourseComboBox.getItems().addAll(courseList);
            dataTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            onQueryButtonClick();
        }
    }
    @FXML
    private void onQueryButtonClick(){
        if(userType.equals("管理员")){
            Integer studentId = 0;
            Integer courseId = 0;
            OptionItem op;
            op = inputStudentComboBox.getSelectionModel().getSelectedItem();
            if(op != null)
                studentId = Integer.parseInt(op.getValue());
            op = inputCourseComboBox.getSelectionModel().getSelectedItem();
            if(op != null)
                courseId = Integer.parseInt(op.getValue());
            DataResponse res;
            DataRequest req =new DataRequest();
            req.add("studentId",studentId);
            req.add("courseId",courseId);
            res= HttpRequestUtil.request("/api/marks/getMarksList",req);
            if(res != null && res.getCode()== 0) {
                marksList = (ArrayList<Map>)res.getData();
            }
            setTableViewData();
        }
        else{
            Integer courseId=0;
            DataRequest req=new DataRequest();
            OptionItem op;
            op = inputCourseComboBox.getSelectionModel().getSelectedItem();
            if(op != null)
                courseId = Integer.parseInt(op.getValue());
            req.add("num",num);
            req.add("name",userName);
            req.add("courseId",courseId);
            DataResponse res=HttpRequestUtil.request("/api/marks/getMarksListByNumName",req);
            if(res != null && res.getCode()== 0) {
                marksList = (ArrayList<Map>)res.getData();
                //MessageDialog.showDialog("查找成功！");
            }else if(res!=null){
                MessageDialog.showDialog(res.getMsg());
            }else{
                MessageDialog.showDialog("连接错误，未正常返回请求！");
            }
            setTableViewData();
            DataRequest req1 = new DataRequest();
            DataResponse res1;

            res1 = HttpRequestUtil.request("/api/resume/getStudentIntroduceData",req1);
            if(res1.getCode() != 0)
                return;
            Map data =(Map)res1.getData();
            avgMarks.setText((String) data.get("marks"));
            avgGPA.setText((String) data.get("GPA"));
            totalRanks.setText((String) data.get("ranks"));


        }
    }
    private void setTableViewData(){
        observableList.clear();

        for (int j = 0; j < marksList.size(); j++) {

            observableList.addAll(FXCollections.observableArrayList(marksList.get(j)));
        }
        dataTableView.setItems(observableList);
    }
    @FXML
    private void onRefreshButtonClick(){
        DataRequest req=new DataRequest();
        DataResponse res=HttpRequestUtil.request("/api/marks/refresh",req);
        if(res!=null && res.getCode()==0){
            MessageDialog.showDialog("刷新成功");
        } else if (res!=null) {
            MessageDialog.showDialog(res.getMsg());

        }else{
            MessageDialog.showDialog("刷新失败");
        }

        onQueryButtonClick();
    }
}
