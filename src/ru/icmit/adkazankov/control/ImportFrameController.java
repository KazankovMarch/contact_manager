package ru.icmit.adkazankov.control;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.icmit.adkazankov.Main;
import ru.icmit.adkazankov.dao.GenericDAOImpl;
import ru.icmit.adkazankov.domain.Entity;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ImportFrameController {

    @FXML
    private TextField separatorField;
    private File file;
    private FileChooser fileChooser;
    private GenericDAOImpl dao;
    private int updateCount = 0;

    public void setDao(GenericDAOImpl dao){ //init
        this.dao = dao;
        fileChooser = new FileChooser();
    }
    public int getUpdateCount(){
        return updateCount;
    }

    @FXML
    private void browseAct(ActionEvent event) {
        file = fileChooser.showOpenDialog(separatorField.getScene().getWindow());
    }

    @FXML
    private void importAct(ActionEvent event) {
        if(file == null){ Main.showError("No file selected.", "Please, click \"Browse\" and choose file."); return ;}
        if(separatorField.getText().equals("")){Main.showError("Separator field is empty.", "Please, print separator.");}
        try {
            updateCount = dao.updateFromFile(file, separatorField.getText());
            close();
        } catch (IOException e) {
            Main.showError("Sorry, failed to import file.", "Something went wrong. Please, choose another file.");
        }
    }

    @FXML
    private void cancelAct(ActionEvent event) {
        close();
    }

    public void close() {
        Stage stage = (Stage) separatorField.getScene().getWindow();
        stage.close();
    }

}
