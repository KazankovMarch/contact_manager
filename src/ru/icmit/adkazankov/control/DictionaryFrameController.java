package ru.icmit.adkazankov.control;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import ru.icmit.adkazankov.Main;
import ru.icmit.adkazankov.dao.DictionaryTypeDAO;
import ru.icmit.adkazankov.dao.PhoneDAO;
import ru.icmit.adkazankov.domain.DictionaryType;
import ru.icmit.adkazankov.util.DbWork;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DictionaryFrameController<T extends DictionaryType> implements FrameClosable{

    private DictionaryTypeDAO<T> dao;
    private ObservableList<T> list;
    private List<T> objectsForDelete =  new ArrayList<>();

    @FXML
    private TableColumn<T, String> codeColumn;

    @FXML
    private TableColumn<T, String> fullnameColumn;

    @FXML
    private TableColumn<T, String> nameColumn;

    @FXML
    private TableColumn<T, String> idColumn;

    @FXML
    private TableView<T> table;

    @FXML
    void saveAct(ActionEvent event) {
        int nullCount = 0;
        for(T o : list){
            if(o.getCode() == null || o.getFullName() == null || o.getName() == null){
                nullCount++;
            }
            else {
                dao.update(o);
            }
        }
        for(T o : objectsForDelete){
            dao.delete(o);
        }
        if(nullCount==0 || Main.showOK("Rows with nulls have not saved. escape anyway?", "count of not saved rows: "+nullCount)){
            close();
        }
    }

    @FXML
    void addAct(ActionEvent event) {
        T o = dao.createNewDTEntity();
        //o.setId(DbWork.getInstance().generateId(dao.getIdSeqName()));
        o.setFullName("New element");
        list.add(o);
        table.refresh();
    }

    @FXML
    void deleteAct(ActionEvent event) {
        if(Main.showOK("Are you sure want try delete this element?","it may break foreign key constrait :(")){
            T o = list.get(table.getSelectionModel().getSelectedIndex());
            if(o.getId()!=null) {
                objectsForDelete.add(o);
            }
            list.remove(table.getSelectionModel().getSelectedIndex());
            table.refresh();
        }
    }

    @FXML
    void cancelAct(ActionEvent event) {
        close();
    }



    public void setDictionary(DictionaryTypeDAO<T> dao){
        this.dao = dao;
        init();
    }

    private void init() {
        table.setEditable(true);
        table.getSelectionModel().setCellSelectionEnabled(true);
        Callback<TableColumn<T, String>, TableCell<T, String>>
                cellFactoryForT = new Callback<TableColumn<T, String>,
                TableCell<T, String>>() {
            @Override
            public TableCell call(TableColumn p) {
                return new TextFieldTableCell(new StringConverter() {
                    @Override
                    public String toString(Object t) {
                        if(t==null)return null;
                        return t.toString();
                    }
                    @Override
                    public Object fromString(String string) {
                        return string;
                    }
                });
            }
        };
        nameColumn.setCellFactory(cellFactoryForT);
        nameColumn.setOnEditCommit(event -> {
            list.get(table.getSelectionModel().getSelectedIndex()).setName(event.getNewValue());
        });
        fullnameColumn.setCellFactory(cellFactoryForT);
        fullnameColumn.setOnEditCommit(event -> {
            list.get(table.getSelectionModel().getSelectedIndex()).setFullName(event.getNewValue());
        });
        codeColumn.setCellFactory(cellFactoryForT);
        codeColumn.setOnEditCommit(event -> {
            list.get(table.getSelectionModel().getSelectedIndex()).setCode(event.getNewValue());
        });

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        fullnameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        resetList();
    }
    private void resetList() {
        list = FXCollections.observableArrayList(dao.getAll());
        table.setItems(list);
    }

    @Override
    public void close() {
        Stage thisStage = (Stage) table.getScene().getWindow();
        thisStage.close();
    }
}
