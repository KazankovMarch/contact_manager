package ru.icmit.adkazankov.control;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.icmit.adkazankov.Main;
import ru.icmit.adkazankov.dao.PhoneTypeDAO;
import ru.icmit.adkazankov.domain.Phone;
import ru.icmit.adkazankov.domain.PhoneType;

import java.util.List;

public class PhoneFrameController {

    @FXML
    private TextField numberField;

    @FXML
    private ComboBox<String> typeBox;
    private List<PhoneType> types = PhoneTypeDAO.getInstance().getAll();
    private ObservableList<String> typeStrings = FXCollections.observableArrayList();
    private Phone phone;

    private boolean addClicked = false;
    public boolean isAddClicked() { return addClicked; }

    public void setPhone(Phone phone){ //init
        this.phone = phone;
        for(PhoneType type : types){
            typeStrings.add(type.getFullName());
        }
        typeBox.setItems(typeStrings);
    }



    @FXML
    void addAct(ActionEvent event) {
        if(isCorrectData()) {
            phone.setPhoneType(types.get(typeBox.getSelectionModel().getSelectedIndex()));
            phone.setPhoneNumber(numberField.getText());
            addClicked = true;
            Stage thisStage = (Stage) typeBox.getScene().getWindow();
            thisStage.close();
        }
    }

    private boolean isCorrectData() {
        if(typeBox.getSelectionModel().getSelectedItem()==null){
            Main.showError("Phonetype is not selected", "Please, choose type from a list");
            return false;
        }
        String number = numberField.getText();
        if(!isParsable(number)){
            Main.showError("incorrect number format", "Please, use only digits");
            return false;
        }
        return true;
    }

    @FXML
    void cancelAct(ActionEvent event) {
        Stage thisStage = (Stage) typeBox.getScene().getWindow();
        thisStage.close();
    }

    public static boolean isParsable(String input){
        boolean parsable = true;
        try{
            Long.parseLong(input);
        }catch(NumberFormatException e){
            parsable = false;
        }
        return parsable;
    }

    public void close() {
        Stage stage = (Stage)numberField.getScene().getWindow();
        stage.close();
    }
}
