package ru.icmit.adkazankov.control;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.icmit.adkazankov.Main;
import ru.icmit.adkazankov.dao.ContactDAO;
import ru.icmit.adkazankov.domain.Contact;

import java.net.URL;
import java.util.ResourceBundle;

public class MainFrameController implements Initializable {

    @FXML
    private TableColumn<Contact, String> contactColumn;

    @FXML
    private TableView<Contact> contactTable;
    private ObservableList<Contact> list;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        resetList();
        contactTable.setOnMouseClicked(event -> {
            if(event.getClickCount()==2) {
                Contact selected = contactTable.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    ContactFrameController.FinalClick click = Main.openContactFrame(selected);
                    if (click == ContactFrameController.FinalClick.save || click == ContactFrameController.FinalClick.delete) {
                        if (click == ContactFrameController.FinalClick.delete) {
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
    private void addAct(ActionEvent event) {
        Contact contact = new Contact();
        contact.setFullName("new Contact");
        if(Main.openContactFrame(contact)== ContactFrameController.FinalClick.save){
            list.add(contact);
            contactTable.refresh();
        }
    }

}
