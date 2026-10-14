package org.example.csia;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Optional;

public class FlashcardView {
    @FXML private Label flashcardText;
    @FXML private ImageView flashcardImage;
    @FXML private StackPane stackPane;
    @FXML private Label subject;
    @FXML private Label topic;
    @FXML private ProgressBar difficulty;

    private Flashcard selectedFlashcard;

    @FXML
    public void initialize(Flashcard flashcard) {
        if (flashcard.isFlipped()) {
            flashcard.flip();
        }
        selectedFlashcard = flashcard;
        subject.setText(flashcard.getSubject());
        topic.setText(flashcard.getTopic());

        double difficultyPercent = switch (flashcard.getDifficulty()) {
            case 1 -> 0.33;
            case 2 -> 0.66;
            case 3 -> 1.00;
            default -> 0;
        };
        difficulty.setProgress(difficultyPercent);

        if (!flashcard.isFlipped()) {
            displayFrontSide(flashcard);
        } else {
            displayBackSide(flashcard);
        }
    }

    @FXML
    private void editFlashcard(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FlashcardEditor.fxml"));
        Parent root = loader.load();
        FlashcardEditor editor = loader.getController();
        editor.setFlashcardData(selectedFlashcard);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void deleteFlashcard(ActionEvent event) throws IOException {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Are you sure you want to proceed?");
        confirm.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            FlashcardDeck.getInstance().remove(selectedFlashcard);
            Flashcard.deleteImages(selectedFlashcard.getIndex());
            FlashcardDeck.getInstance().saveDeck();
            new AlertMaker(Alert.AlertType.INFORMATION, "Flashcard Deleted", null, "This flashcard has been deleted.");
            goBack(event);
        } else {
            new AlertMaker(Alert.AlertType.INFORMATION, "Deletion Cancelled", null, "The deletion has been cancelled.");
        }
    }

    private void flipCard(Flashcard flashcard) {
        if (!flashcard.isFlipped()) {
            displayBackSide(flashcard);
        } else {
            displayFrontSide(flashcard);
        }
        flashcard.flip();
    }

    private void setupFlashcardStyle() {
        flashcardText.setStyle("-fx-background-color: yellow; -fx-border-color: black; -fx-border-width: 1;");
        stackPane.setStyle("-fx-background-color: yellow; -fx-border-color: black; -fx-border-width: 1;");
        flashcardText.setAlignment(Pos.CENTER);
        stackPane.setAlignment(Pos.CENTER);
    }

    private void displayFrontSide(Flashcard flashcard) {
        if (flashcard.hasTextFront()) {
            flashcardImage.setVisible(false);
            flashcardText.setVisible(true);
            flashcardText.setText(flashcard.getFrontSideText());
            setupFlashcardStyle();
            stackPane.setOnMouseClicked(event -> flipCard(flashcard));
        } else if (flashcard.hasImageFront()) {
            flashcardText.setVisible(false);
            flashcardImage.setVisible(true);
            flashcardImage.setImage(flashcard.getFrontSideImage());
            setupFlashcardStyle();
            stackPane.setOnMouseClicked(event -> flipCard(flashcard));
        }
    }

    private void displayBackSide(Flashcard flashcard) {
        if (flashcard.hasTextBack()) {
            flashcardImage.setVisible(false);
            flashcardText.setVisible(true);
            flashcardText.setText(flashcard.getBackSideText());
            setupFlashcardStyle();
            stackPane.setOnMouseClicked(event -> flipCard(flashcard));
        } else if (flashcard.hasImageBack()) {
            flashcardText.setVisible(false);
            flashcardImage.setVisible(true);
            flashcardImage.setImage(flashcard.getBackSideImage());
            setupFlashcardStyle();
            stackPane.setOnMouseClicked(event -> flipCard(flashcard));
        }
    }

    public void goBack(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("FlashcardManager.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
