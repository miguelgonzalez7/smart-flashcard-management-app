package org.example.csia;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;

public class HomeScreen {
    @FXML private ChoiceBox<String> studyModes = new ChoiceBox<>();
    @FXML private ChoiceBox<String> studyTimers = new ChoiceBox<>();
    @FXML private AnchorPane helpScreen;

    @FXML
    public void initialize() {
        String[] modes = {"Sequential Study", "Random Study", "Subject Study", "Topic Study", "Smart Study"};
        String[] timers = {"Free Study", "Stopwatch", "Countdown Timer", "Pomodoro Timer"};

        studyModes.setItems(FXCollections.observableArrayList(modes));
        studyTimers.setItems(FXCollections.observableArrayList(timers));

        studyModes.setValue("Smart Study");
        studyTimers.setValue("Stopwatch");
    }

    @FXML
    public void startSession(ActionEvent event) throws IOException {
        if ((studyModes.getValue() == null) || (studyTimers.getValue() == null)) {
            new AlertMaker(Alert.AlertType.ERROR, "Error", null, "Please select a study mode and timer");
            return;
        }
        String selectedMode = studyModes.getValue();
        String selectedTimer = studyTimers.getValue();
        goToStudy(event, selectedMode, selectedTimer);
    }

    public void goToStudy(ActionEvent event, String selectedMode, String selectedTimer) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Study.fxml"));
        Parent root = loader.load();
        Study session = loader.getController();

        session.setStudyStats(selectedMode, selectedTimer);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void goToFlashcardEditor(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FlashcardEditor.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void goToFlashcardManager(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FlashcardManager.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void help() {
        if (helpScreen.isVisible()) {
            helpScreen.setVisible(false);
        } else {
            helpScreen.setVisible(true);
            Timeline hideHelpScreen = new Timeline(new KeyFrame(Duration.seconds(10), event1 -> helpScreen.setVisible(false)));
            hideHelpScreen.setCycleCount(1);
            hideHelpScreen.play();
        }
    }
}
