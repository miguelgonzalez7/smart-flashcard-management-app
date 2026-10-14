package org.example.csia;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.IOException;

public class Start extends Application {
    private static final FlashcardDeck deck = FlashcardDeck.getInstance();

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException, ClassNotFoundException {
        // Load necessary data (flashcards, categories...) before initializing the scene
        loadData();

        // Navigate to the HomeScreen FXML file
        FXMLLoader fxmlLoader = new FXMLLoader(Start.class.getResource("HomeScreen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400, Color.CORNSILK);
        stage.setResizable(false); // Disable window resizing
        stage.setTitle("Study App");
        stage.setScene(scene); // Switch to the new scene
        stage.show(); // Display the stage
    }

    @Override
    public void stop() throws IOException {
        // Ensure data is saved upon program closing
        saveData();
    }

    private void loadData() throws IOException, ClassNotFoundException {
        // Load all necessary application data
        Flashcard.loadCounter();
        deck.loadDeck();
        deck.loadCategories();
    }

    private void saveData() throws IOException {
        // Save all necessary application data
        Flashcard.saveCounter();
        deck.saveDeck();
        deck.saveCategories();
    }
}
