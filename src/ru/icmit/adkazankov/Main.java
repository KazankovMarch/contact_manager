package ru.icmit.adkazankov;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.icmit.adkazankov.control.ContactFrameController;
import ru.icmit.adkazankov.control.DictionaryFrameController;
import ru.icmit.adkazankov.control.ImportFrameController;
import ru.icmit.adkazankov.dao.ContactDAO;
import ru.icmit.adkazankov.dao.DictionaryTypeDAO;
import ru.icmit.adkazankov.dao.PhoneDAO;
import ru.icmit.adkazankov.dao.PhoneTypeDAO;
import ru.icmit.adkazankov.domain.Contact;
import ru.icmit.adkazankov.domain.Phone;
import ru.icmit.adkazankov.control.PhoneFrameController;
import ru.icmit.adkazankov.domain.PhoneType;

import java.io.IOException;

public class Main extends Application {

    private String mainFrameString = "view/MainFrame.fxml";

    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println(ContactDAO.getInstance().getAll());
        System.out.println("==========================================================");
        System.out.println(PhoneDAO.getInstance().getAll());
        System.out.println("==========================================================");
        System.out.println(PhoneTypeDAO.getInstance().getAll());
        System.out.println("==========================================================");
        launch(args);
    }

    public static boolean openPhoneFrame(Phone phone) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/PhoneFrame.fxml"));

            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("New phone");
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);

            PhoneFrameController controller = loader.getController();
            controller.setPhone(phone);

            stage.showAndWait();
            return controller.isAddClicked();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void openDictionary(DictionaryTypeDAO dao, DictionaryFrameController controller) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/DictionaryFrame.fxml"));

            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(dao.getTableName());
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            Class c = PhoneType.class;
            controller = loader.getController();
            controller.setDictionary(dao);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int openImportFrame(ContactDAO dao) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/ImportFrame.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Import to "+dao.getTableName());
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            Class c = PhoneType.class;
            ImportFrameController controller = loader.getController();
            controller.setDao(dao);
            stage.showAndWait();
            return controller.getUpdateCount();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(mainFrameString));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Contacts");
        stage.show();
    }
    public static ContactFrameController.FinalClick openContactFrame(Contact contact){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/ContactFrame.fxml"));

            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(contact.getFullName());
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);


            ContactFrameController controller = loader.getController();
            controller.setContact(contact);

            stage.showAndWait();
            return controller.getClick();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ContactFrameController.FinalClick.cancel;
    }
    public static boolean showOK(String main, String context){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("confirmation of action");
        alert.setHeaderText(main);
        alert.setContentText(context);
        alert.setResult(ButtonType.CANCEL);
        alert.setResult(ButtonType.OK);
        alert.showAndWait();
        return !alert.getResult().getButtonData().isCancelButton();
    }
    public static void showError(String main, String context){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error :(");
        alert.setHeaderText(main);
        alert.setContentText(context);
        alert.show();
    }
}
