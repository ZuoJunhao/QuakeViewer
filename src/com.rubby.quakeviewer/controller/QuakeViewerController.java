package com.rubby.quakeviewer.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rubby.quakeviewer.entity.assets;
import com.rubby.quakeviewer.util.CreateConfigFile;
import com.rubby.quakeviewer.util.JsonRequest;
import com.rubby.quakeviewer.util.ReadFile;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.controlsfx.control.StatusBar;
import org.controlsfx.control.textfield.TextFields;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class QuakeViewerController {


    @FXML
    public TableColumn<assets,String> count;

    @FXML
    public Label status;

    @FXML
    public ContextMenu contextMenu;

    @FXML
    private TextField query;

    @FXML
    private TableView<assets> table;

    @FXML
    private TableColumn<assets,String> url;

    @FXML
    private TableColumn<assets,String> ip;

    @FXML
    private TableColumn<assets,String> port;

    @FXML
    private TableColumn<assets,String> domain;

    @FXML
    private TableColumn<assets,String> service;

    @FXML
    private TableColumn<assets,String> title;

    @FXML
    private TableColumn<assets,String> cms;

    @FXML
    private TableColumn<assets,String> icp;

    @FXML
    private TableColumn<assets,String> company;


    public String APIkey;
    public String MaxSize;
    @FXML
    public void initialize(){
//        ReadFile readFile = new ReadFile();
//        readFile.readFile("./api.config");
        File configFile = new File("./api.config");
        if (!configFile.exists()){
            CreateConfigFile createConfigFile=new CreateConfigFile();
            createConfigFile.createConfigFile(configFile);
        }
        ReadFile readFile = new ReadFile();
        readFile.readFile("./api.config");
        this.APIkey=readFile.getApikey();
        this.MaxSize=readFile.getMaxSize();


        count.prefWidthProperty().bind(table.widthProperty().multiply(0.024));
        url.prefWidthProperty().bind(table.widthProperty().multiply(0.23));
        ip.prefWidthProperty().bind(table.widthProperty().multiply(.096));
        port.prefWidthProperty().bind(table.widthProperty().multiply(.041));
        domain.prefWidthProperty().bind(table.widthProperty().multiply(.096));
        service.prefWidthProperty().bind(table.widthProperty().multiply(.053));
        title.prefWidthProperty().bind(table.widthProperty().multiply(.115));
        cms.prefWidthProperty().bind(table.widthProperty().multiply(.115));
        icp.prefWidthProperty().bind(table.widthProperty().multiply(.115));
        company.prefWidthProperty().bind(table.widthProperty().multiply(.115));

        //table.setContextMenu(contextMenu);
        // 设置鼠标按下和抬起事件监听器
        table.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                assets seletedAssets = table.getSelectionModel().getSelectedItem();
                String url = seletedAssets.getUrl();
                URI uri = URI.create(url);

                Desktop dp = Desktop.getDesktop();
                if (dp.isSupported(Desktop.Action.BROWSE)) {
                    try {
                        dp.browse(uri);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }
    private List<assets> assetsList;
    @FXML
    public void doSearch(){

        status.setVisible(true);
        status.setText("查询中请稍后...");

        String queryString = query.getText();

        // 在后台线程中执行查询
        Thread queryThread = new Thread(() -> {
            this.assetsList = JsonRequest.postRequest(queryString, APIkey, MaxSize);

            // 使用 Platform.runLater 更新 UI
            Platform.runLater(() -> {
                // 查询完成后更新 UI
                status.setText("查询成功！");
            });

            count.setCellValueFactory(new PropertyValueFactory<>("count"));
            url.setCellValueFactory(new PropertyValueFactory<>("url"));
            ip.setCellValueFactory(new PropertyValueFactory<>("ip"));
            port.setCellValueFactory(new PropertyValueFactory<>("port"));
            domain.setCellValueFactory(new PropertyValueFactory<>("domain"));
            service.setCellValueFactory(new PropertyValueFactory<>("service"));
            title.setCellValueFactory(new PropertyValueFactory<>("title"));
            cms.setCellValueFactory(new PropertyValueFactory<>("cms"));
            icp.setCellValueFactory(new PropertyValueFactory<>("icp"));
            company.setCellValueFactory(new PropertyValueFactory<>("company"));
            table.setItems(FXCollections.observableList(this.assetsList));

            //选中多行
            table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        });

        queryThread.start();



        //table.getSelectionModel().setCellSelectionEnabled(true);
    }
    @FXML
    public void onClikCopyURL(ActionEvent actionEvent) {

        List<assets> slectedAssets = table.getSelectionModel().getSelectedItems();
        String content=slectedAssets.stream()
                .map(assets::getUrl)
                .collect(Collectors.joining("\n"));

        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(content);
        Clipboard.getSystemClipboard().setContent(clipboardContent);

    }
    @FXML
    public void onClikCopyIP(ActionEvent actionEvent) {
        List<assets> slectedAssets = table.getSelectionModel().getSelectedItems();
        String content=slectedAssets.stream()
                .map(assets::getIp)
                .collect(Collectors.joining("\n"));

        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(content);
        Clipboard.getSystemClipboard().setContent(clipboardContent);
    }
    @FXML
    public void onClikCopyDomain(ActionEvent actionEvent) {
        List<assets> slectedAssets = table.getSelectionModel().getSelectedItems();
        String content=slectedAssets.stream()
                .map(assets::getDomain)
                .collect(Collectors.joining("\n"));

        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(content);
        Clipboard.getSystemClipboard().setContent(clipboardContent);
    }
    @FXML
    public void onClikCopyICP(ActionEvent actionEvent) {
        List<assets> slectedAssets = table.getSelectionModel().getSelectedItems();
        String content=slectedAssets.stream()
                .map(assets::getIcp)
                .collect(Collectors.joining("\n"));

        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(content);
        Clipboard.getSystemClipboard().setContent(clipboardContent);
    }
    @FXML
    public void onClikCopyCompany(ActionEvent actionEvent) {
        List<assets> slectedAssets = table.getSelectionModel().getSelectedItems();
        String content=slectedAssets.stream()
                .map(assets::getCompany)
                .collect(Collectors.joining("\n"));

        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(content);
        Clipboard.getSystemClipboard().setContent(clipboardContent);
    }
//    @FXML
//    public void jumpToURL(MouseEvent mouseEvent) {
//        assets seletedAssets = table.getSelectionModel().getSelectedItem();
//        String url = seletedAssets.getUrl();
//        URI uri = URI.create(url);
//
//        Desktop dp = Desktop.getDesktop();
//        if (dp.isSupported(Desktop.Action.BROWSE)) {
//            try {
//                dp.browse(uri);
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        }
//
//    }
}
