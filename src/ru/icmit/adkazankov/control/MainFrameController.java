package ru.icmit.adkazankov.control;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import ru.icmit.adkazankov.Main;
import ru.icmit.adkazankov.dao.*;
import ru.icmit.adkazankov.domain.Contact;
import ru.icmit.adkazankov.control.ContactFrameController.FinalClick;
import ru.icmit.adkazankov.domain.DictionaryType;
import ru.icmit.adkazankov.domain.PhoneType;

import java.net.URL;
import java.util.ResourceBundle;

public class MainFrameController implements Initializable {

    @FXML
    private TableColumn<Contact, String> contactColumn;

    @FXML
    private TableView<Contact> contactTable;
    private ObservableList<Contact> list;


    public void close() {
        Stage stage = (Stage) contactTable.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        resetList();
        contactTable.setOnMouseClicked(event -> {
            if(event.getClickCount()==2) {
                Contact selected = contactTable.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    FinalClick click = Main.openContactFrame(selected);
                    if (click == FinalClick.save || click == FinalClick.delete) {
                        if (click == FinalClick.delete) {
                            selected.setFullName("");
                            list.remove(selected);
                            list.remove(contactTable.getSelectionModel().getSelectedIndex());
                        }
                        contactTable.refresh();
                    }
                }
            }
        });
    }

    private void resetList() {
        ContactDAO dao = ContactDAO.getInstance();
        list = FXCollections.observableArrayList(dao.getAll());
        contactTable.setItems(list);
    }

    @FXML
    private void showPhoneTypeAct(ActionEvent event) {
        Main.openDictionary(PhoneTypeDAO.getInstance(), new DictionaryFrameController<PhoneType>());
    }

    @FXML
    private void addAct(ActionEvent event) {
        Contact contact = new Contact();
        contact.setFullName("new Contact");
        if(Main.openContactFrame(contact)== FinalClick.save){
            list.add(contact);
            contactTable.refresh();
        }
    }


    @FXML
    void importAct(ActionEvent event) {
        MenuItem menu = (MenuItem) event.getSource();
        int count = 0;
        GenericDAOImpl dao = null;
        switch (menu.getText()){
            case "Contacts": dao = ContactDAO.getInstance(); break;
            case "Phones": dao = PhoneDAO.getInstance(); break;
            case "Phone types": dao = PhoneTypeDAO.getInstance(); break;
        }
        if(Main.openImportFrame(dao) > 0){
            resetList();
            contactTable.refresh();
        }
    }

    @FXML
    private void showAboutAct(ActionEvent event) {
        Main.showError("Sorry, not supported yet.", "Please, visit https://github.com/KazankovMarch/contact_manager");
    }
}
