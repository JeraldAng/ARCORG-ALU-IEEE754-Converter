import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    protected Stage stage;
    private HashMap<String, Object> data;
    private Controller controller;

    protected void setStage(Stage stage) {
        this.stage = stage;
    }
    protected void setData(HashMap<String, Object> data) {
        this.data = data;
    }
    private void setParentController(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                doneInit(data);
            }
        });
    }

    protected Controller getParentController(){
        return controller;
    }

    protected void onClose(){

    }

    protected void doneInit(HashMap<String, Object> data){

    }

    @FXML
    protected void onAction(ActionEvent event){

    }

    protected void onMouseAction(MouseEvent event){

    }

    public void close(){
        stage.close();
    }

    public Controller loadToPane(String fxml, Pane pane, Controller parent){
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        try {
            Parent root = loader.load();
            Controller c = loader.getController();
            c.setParentController(parent);

            pane.getChildren().clear();
            pane.getChildren().add(root);
            return c;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Controller open(String fxml, String title){
        return open(fxml, title, null, null);
    }

    public Controller open(String fxml, String title, Controller parent){
        return open(fxml, title, null, parent);
    }

    public Controller open(String fxml, String title, HashMap<String, Object> data, Controller parent){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Controller c = loader.getController();
            Stage stage = new Stage();
            c.setStage(stage);
            c.setParentController(parent);
            stage.setTitle(title);
            stage.setScene(new Scene(root));

            stage.sizeToScene();
            stage.show();
            return c;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void setScene(String fxml){
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        try {
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            Controller c = loader.getController();
            c.setStage(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Controller openModal(String fxml, String title){
        return openModal(fxml, title, null, null);
    }

    public Controller openModal(String fxml, String title, Controller parent){
        return openModal(fxml, title, null, parent);
    }

    public Controller openModal(String fxml, String title, HashMap<String, Object> data, Controller parent){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Controller c = loader.getController();
            Stage stage = new Stage();
            c.setStage(stage);
            c.setData(data);
            c.setParentController(parent);
            stage.setOnHidden(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    c.onClose();
                }
            });
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
            return c;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Stage getStage(){
        return stage;
    }

    public void setOnClose(EventHandler<WindowEvent> t){
        getStage().setOnHidden(t);
    }
}
