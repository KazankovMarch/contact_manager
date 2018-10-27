package ru.icmit.adkazankov.control;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import ru.icmit.adkazankov.Main;
import ru.icmit.adkazankov.dao.ContactDAO;
import ru.icmit.adkazankov.dao.PhoneDAO;
import ru.icmit.adkazankov.domain.Contact;
import ru.icmit.adkazankov.domain.Phone;
import ru.icmit.adkazankov.domain.PhoneType;

import java.util.ArrayList;
import java.util.List;

public class ContactFrameController {

    public static enum FinalClick {
        save, delete, cancel;
    }

    @FXML
    private GridPane gridPane;

    private Contact contact;
    private TextField fullNameField;
    private TextField firstNameField;
    private TextField lastNameField;
    private CheckBox inBlackListButton;
    private List<Phone> phones;
    private List<TextField> phoneFields;
    private List<PhoneType> phoneTypes;

    private FinalClick click = FinalClick.cancel;
    public FinalClick getClick() { return click; }

    public void setContact(Contact contact) {
        this.contact = contact;
        fullNameField = new TextField(contact.getFullName());
        gridPane.addRow(0, new Label("fullname"), fullNameField);
        firstNameField = new TextField(contact.getFirstName());
        gridPane.addRow(1, new Label("firstName"), firstNameField);
        lastNameField = new TextField(contact.getLastName());
        gridPane.addRow(2, new Label("lastName"), lastNameField);
        inBlackListButton = new CheckBox();
        inBlackListButton.setSelected(contact.isInBlackList());
        gridPane.addRow(3, new Label("inBlackList"), inBlackListButton);
        phones = new ArrayList<>();
        phoneTypes = new ArrayList<>();
        phoneFields = new ArrayList<>();
        for(Phone phone : PhoneDAO.getInstance().getByContact(contact)){
            addPhone(phone);
        }
    }

    @FXML
    void saveAct(ActionEvent event) {
        click = FinalClick.save;
        contact.setInBlackList(inBlackListButton.isSelected());
        contact.setFirstName(firstNameField.getText());
        contact.setLastName(lastNameField.getText());
        contact.setFullName(fullNameField.getText());
        ContactDAO.getInstance().update(contact);
        PhoneDAO phoneDAO = PhoneDAO.getInstance();
        for(int i = 0; i < phones.size(); i++){
            Phone nextPhone = phones.get(i);
            nextPhone.setPhoneNumber(phoneFields.get(i).getText());
            //nextPhone.setPhoneType();
            phoneDAO.update(nextPhone);
        }
        ContactDAO.getInstance().update(contact);
        Stage thisStage = (Stage) gridPane.getScene().getWindow();
        thisStage.close();
    }

    @FXML
    void cancelAct(ActionEvent event) {
        click = FinalClick.cancel;
        Stage thisStage = (Stage) gridPane.getScene().getWindow();
        thisStage.close();
    }

    @FXML
    void deleteAct(ActionEvent event) {
        if(Main.showOK("Вы действительно хотите удалить этот контакт?", "Отменить это действие будет невозможно")){
            click = FinalClick.delete;
            ContactDAO.getInstance().delete(contact);
            Stage thisStage = (Stage) gridPane.getScene().getWindow();
            thisStage.close();
        }
    }

    @FXML
    void addPhoneAct(ActionEvent event) {
        Phone phone = new Phone();
        phone.setContact(contact);
        if(Main.openPhoneFrame(phone)){
            addPhone(phone);
        }
    }

    private void addPhone(Phone phone) {
        String typeString = phone.getPhoneType().getName();
        Label type = new Label(typeString);
        TextField number = new TextField(phone.getPhoneNumber());
        Button removeButton = new Button("Delete");
        int i = getRowCount(gridPane);
        removeButton.setOnMouseClicked(e -> {
            gridPane.getChildren().removeIf(node -> gridPane.getRowIndex(node) == i);
        });
        phoneTypes.add(phone.getPhoneType());
        phoneFields.add(number);
        phones.add(phone);
        gridPane.addRow(i,type,number,removeButton);
    }

    private int getRowCount(GridPane pane) {
        int numRows = pane.getRowConstraints().size();
        for (int i = 0; i < pane.getChildren().size(); i++) {
            Node child = pane.getChildren().get(i);
            if (child.isManaged()) {
                Integer rowIndex = GridPane.getRowIndex(child);
                if(rowIndex != null){
                    numRows = Math.max(numRows,rowIndex+1);
                }
            }
        }
        return numRows;
    }

}
