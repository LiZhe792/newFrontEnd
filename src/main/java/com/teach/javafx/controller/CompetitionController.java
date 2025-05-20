package com.teach.javafx.controller;

import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.util.CommonMethod;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompetitionController {
    @FXML
    private TableView<Map> competitionTableView;
    @FXML
    private TableColumn<Map, String> idColumn;
    @FXML
    private TableColumn<Map, String> subjectColumn;
    @FXML
    private TableColumn<Map, String> nameColumn;
    @FXML
    private TableColumn<Map, String> levelColumn;

    @FXML
    private TextField idField;
    @FXML
    private TextField subjectField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField levelField;

    @FXML
    private TextField searchNameField;

    private Integer competitionId = null; // 当前编辑的竞赛主键
    private ArrayList<Map> competitionList = new ArrayList<>(); // 竞赛列表数据
    private ObservableList<Map> observableList = FXCollections.observableArrayList(); // 表格渲染数据

    @FXML
    public void initialize() {
        // 初始化表格列
        idColumn.setCellValueFactory(new MapValueFactory<>("competitionId"));
        subjectColumn.setCellValueFactory(new MapValueFactory<>("subject"));
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        levelColumn.setCellValueFactory(new MapValueFactory<>("level"));

        // 监听表格选择事件
        competitionTableView.getSelectionModel().getSelectedIndices().addListener(this::onTableRowSelect);

        // 加载初始数据
        loadCompetitionList();
    }

    private void loadCompetitionList() {
        DataRequest req = new DataRequest();
        req.add("name", searchNameField.getText()); // 带搜索条件
        DataResponse res = HttpRequestUtil.request("/api/competition/competitionList", req);

        if (res != null && res.getCode() == 0) {
            competitionList = (ArrayList<Map>) res.getData();
            observableList.setAll(competitionList);
            competitionTableView.setItems(observableList);
        } else {
            MessageDialog.showDialog("数据加载失败：" + (res != null ? res.getMsg() : "网络或服务器异常"));
        }
    }

    @FXML
    private void onQueryButtonClick() {
        loadCompetitionList(); // 重新加载数据（带搜索条件）
    }

    @FXML
    private void onAddButtonClick() {
        clearPanel(); // 清空表单
        competitionId = null; // 新增模式
    }

    @FXML
    private void onSaveButtonClick() {
        if (nameField.getText().isEmpty()) {
            MessageDialog.showDialog("名称不能为空");
            return;
        }

        Map<String, Object> form = new HashMap<>();
        form.put("competitionId", idField.getText());
        form.put("subject", subjectField.getText());
        form.put("name", nameField.getText());
        form.put("level", levelField.getText());

        DataRequest req = new DataRequest();
        req.add("competitionMap", form); // 对应后端的 competitionMap 参数
        DataResponse res;

        if (competitionId == null) {
            // 新增操作
            res = HttpRequestUtil.request("/api/competition/competitionAdd", req);
        } else {
            // 修改操作
            req.add("competitionId", competitionId);
            res = HttpRequestUtil.request("/api/competition/competitionUpdate", req);
        }

        if (res != null && res.getCode() == 0) {
            MessageDialog.showDialog("操作成功");
            loadCompetitionList(); // 刷新数据
            clearPanel(); // 清空表单（可选）
        } else {
            MessageDialog.showDialog("操作失败：" + (res != null ? res.getMsg() : "网络或服务器异常"));
        }
    }

    @FXML
    private void onDeleteButtonClick() {
        Map selectedItem = competitionTableView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            MessageDialog.showDialog("请选择要删除的竞赛");
            return;
        }

        int ret = MessageDialog.choiceDialog("确认要删除选中的竞赛吗？");
        if (ret == MessageDialog.CHOICE_YES) {
            Integer id = CommonMethod.getInteger(selectedItem, "competitionId");
            DataRequest req = new DataRequest();
            req.add("competitionId", id);
            DataResponse res = HttpRequestUtil.request("/api/competition/competitionDelete", req);

            if (res != null && res.getCode() == 0) {
                MessageDialog.showDialog("删除成功");
                loadCompetitionList();
            } else {
                MessageDialog.showDialog("删除失败：" + (res != null ? res.getMsg() : "网络或服务器异常"));
            }
        }
    }

    private void onTableRowSelect(ListChangeListener.Change<? extends Integer> change) {
        Map selectedItem = competitionTableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            competitionId = CommonMethod.getInteger(selectedItem, "competitionId");
            fillForm(selectedItem); // 填充表单
        } else {
            clearPanel(); // 未选中时清空表单
        }
    }

    private void fillForm(Map item) {
        idField.setText(CommonMethod.getString(item, "competitionId"));
        subjectField.setText(CommonMethod.getString(item, "subject"));
        nameField.setText(CommonMethod.getString(item, "name"));
        levelField.setText(CommonMethod.getString(item, "level"));
    }

    private void clearPanel() {
        idField.clear();
        nameField.clear();
        levelField.clear();
        subjectField.clear();
        competitionTableView.getSelectionModel().clearSelection();
    }
}
