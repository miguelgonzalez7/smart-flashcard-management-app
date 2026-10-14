package org.example.csia;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.*;

public class Study {
    private LinkedList<Flashcard> flashcards;
    private String studyMode;
    private String studyTimer;

    private Stack<Flashcard> studyStack;
    private Stack<Flashcard> historyStack;

    private final FlashcardDeck deck = FlashcardDeck.getInstance();
    private final Timer timerClass = new Timer();

    @FXML private ToggleGroup understanding;
    @FXML private AnchorPane breakScreen;
    @FXML private Label flashcardText;
    @FXML private ImageView flashcardImage;
    @FXML private StackPane imageStackPane;
    @FXML private Label timerLabel;
    @FXML private Label breakTimer;
    @FXML private Button nextButton;
    @FXML private Button backButton;
    @FXML private Label breakLabel;

    public void initialize() {
        FlashcardDeck deck = FlashcardDeck.getInstance();
        flashcards = deck.getFlashcards();
        timerClass.initialize(timerLabel, breakScreen, breakTimer, breakLabel);
    }

    public void setStudyStats(String studyMode, String studyTimer) {
        this.studyMode = studyMode;
        this.studyTimer = studyTimer;
        startStudySession();
    }

    public void startStudySession() {
        studyStack = new Stack<>();
        historyStack = new Stack<>();

        switch (studyMode) {
            case "Random Study" -> random();
            case "Sequential Study" -> sequential();
            case "Smart Study" -> smart();
            case "Topic Study" -> topic();
            case "Subject Study" -> subject();
        }

        displayFirstCard();
        configureTimer(studyTimer);
    }

    @FXML
    public void endStudySession(ActionEvent event) throws IOException {
        timerClass.stop();
        if (!historyStack.isEmpty()) {
            Flashcard finalCard = historyStack.peek();
            if (!savedUnderstanding(finalCard)) {
                System.out.println("didnt save");
                return;
            }
        }
        goBack(event);
    }

    @FXML
    public void next() throws IOException {
        if (!studyStack.isEmpty()) {
            Flashcard currentCard = studyStack.peek();
            Flashcard previousCard = historyStack.isEmpty() ? null : historyStack.peek();

            if (!(previousCard != null && savedUnderstanding(previousCard))) {
                return;
            }

            studyStack.pop();
            historyStack.push(currentCard);

            nextButton.setVisible(!studyStack.isEmpty());
            backButton.setVisible(!historyStack.isEmpty());

            selectRadioButtonFromFlashcard(currentCard);
            displayFront(currentCard);
        }
    }

    @FXML
    public void previous() throws IOException {
        if (!historyStack.isEmpty()) {
            Flashcard previousCard = historyStack.pop();
            studyStack.push(previousCard);

            Flashcard currentCard = historyStack.isEmpty() ? null : historyStack.peek();

            if (!(previousCard != null && savedUnderstanding(previousCard))) {
                return;
            }

            nextButton.setVisible(!studyStack.isEmpty());

            Flashcard temp = historyStack.pop();
            if (historyStack.isEmpty()) {
                backButton.setVisible(false);
            }
            historyStack.push(temp);

            selectRadioButtonFromFlashcard(currentCard);
            displayFront(currentCard);
        }
    }

    private void random() {
        LinkedList<Flashcard> shuffledCards = new LinkedList<>(flashcards);
        Random random = new Random();
        while (!shuffledCards.isEmpty()) {
            int randomIndex = random.nextInt(shuffledCards.size());
            studyStack.push(shuffledCards.remove(randomIndex));
        }
    }

    private void sequential() {
        for (int i = flashcards.size() - 1; i >= 0; i--) {
            studyStack.push(flashcards.get(i));
        }
    }

    private void smart() {
        List<Flashcard> sortedFlashcards = new ArrayList<>();
        for (Flashcard flashcard : flashcards) {
            int difficultyToken = getDifficultyToken(flashcard.getDifficulty());
            int understandingToken = getUnderstandingToken(flashcard.getUnderstanding());
            int totalTokens = difficultyToken + understandingToken;

            flashcard.setTokens(totalTokens);
            sortedFlashcards.add(flashcard);
        }
        sortedFlashcards.sort(Comparator.comparingInt(Flashcard::getTokens));

        for (Flashcard flashcard : sortedFlashcards) {
            studyStack.push(flashcard);
        }
    }

    private void topic() {
        filterByCriteria("Topic Selection", "Select the topics you want to study.\n(Hold Command to select more than one)", deck.getAllTopics(), "topic");
    }

    private void subject() {
        filterByCriteria("Subject Selection", "Select the subjects you want to study.\n(Hold Command to select more than one)", deck.getSubjects(), "subject");
    }

    private void configureTimer(String studyTimer) {
        switch (studyTimer) {
            case "Stopwatch" -> timerClass.startStopwatch();
            case "Countdown Timer" -> configureCountdownTimer();
            case "Pomodoro Timer" -> configurePomodoroTimer();
            case "Free Study" -> timerLabel.setVisible(false);
        }
    }

    private void configureCountdownTimer() {
        TextInputDialog workDialog = new TextInputDialog("30:00");
        workDialog.setTitle("Countdown Timer Setup");
        workDialog.setHeaderText("Enter Study Session Duration");
        workDialog.setContentText("Format: MM:SS (e.g., 30:00 for 30 minutes)");
        Optional<String> result = workDialog.showAndWait();

        int minutes = 30;
        int seconds = 0;

        if (result.isPresent()) {
            String[] workParts = result.get().split(":");
            try {
                minutes = Integer.parseInt(workParts[0].trim());
                if (minutes > 60 || minutes < 0) {
                    new AlertMaker(Alert.AlertType.ERROR, "Error", null, "Invalid minutes value. Using default 30 minutes.");
                    minutes = 30;
                }
                seconds = Integer.parseInt(workParts[1].trim());
                if (seconds > 59 || seconds < 0) {
                    new AlertMaker(Alert.AlertType.ERROR, "Error", null, "Invalid seconds value. Using default 0 seconds.");
                    seconds = 0;
                }
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                new AlertMaker(Alert.AlertType.ERROR, "Error", null, "Invalid format for work duration. Using default 30:00.");
            }
        }
        timerClass.startCountdown(minutes, seconds);
    }

    private void configurePomodoroTimer() {
        TextInputDialog workDialog = new TextInputDialog("25:00");
        workDialog.setTitle("Pomodoro Timer Setup");
        workDialog.setHeaderText("Enter Work Session Duration");
        workDialog.setContentText("Format: MM:SS (e.g., 25:00 for 25 minutes)");
        Optional<String> workResult = workDialog.showAndWait();

        int workMinutes = 25;
        int workSeconds = 0;

        if (workResult.isPresent()) {
            String[] workParts = workResult.get().split(":");
            try {
                workMinutes = Integer.parseInt(workParts[0].trim());
                if (workMinutes > 60 || workMinutes < 0) workMinutes = 25;
                workSeconds = Integer.parseInt(workParts[1].trim());
                if (workSeconds > 59 || workSeconds < 0) workSeconds = 0;
            } catch (Exception e) {
                workMinutes = 25;
                workSeconds = 0;
            }
        }

        TextInputDialog breakDialog = new TextInputDialog("05:00");
        breakDialog.setTitle("Pomodoro Timer Setup");
        breakDialog.setHeaderText("Enter Break Session Duration");
        breakDialog.setContentText("Format: MM:SS (e.g., 05:00 for 5 minutes)");
        Optional<String> breakResult = breakDialog.showAndWait();

        int breakMinutes = 5;
        int breakSeconds = 0;

        if (breakResult.isPresent()) {
            String[] breakParts = breakResult.get().split(":");
            try {
                breakMinutes = Integer.parseInt(breakParts[0].trim());
                if (breakMinutes > 60 || breakMinutes < 0) breakMinutes = 5;
                breakSeconds = Integer.parseInt(breakParts[1].trim());
                if (breakSeconds > 59 || breakSeconds < 0) breakSeconds = 0;
            } catch (Exception e) {
                breakMinutes = 5;
                breakSeconds = 0;
            }
        }

        TextInputDialog repetitionsDialog = new TextInputDialog("3");
        repetitionsDialog.setTitle("Pomodoro Timer Setup");
        repetitionsDialog.setHeaderText("Enter Cycle Repetitions");
        repetitionsDialog.setContentText("Format: Integer (Greater than 0)");
        Optional<String> repetitionsResult = repetitionsDialog.showAndWait();

        int repetitions = 2;
        if (repetitionsResult.isPresent()) {
            try {
                repetitions = Integer.parseInt(repetitionsResult.get().trim());
                if (repetitions <= 0) repetitions = 2;
            } catch (NumberFormatException e) {
                repetitions = 2;
            }
        }

        timerClass.startPomodoroCycle(workMinutes, workSeconds, breakMinutes, breakSeconds, repetitions);
    }

    public void displayFirstCard() {
        if (!studyStack.isEmpty()) {
            Flashcard firstCard = studyStack.pop();
            historyStack.push(firstCard);
            backButton.setVisible(false);
            if (studyStack.isEmpty()) nextButton.setVisible(false);
            selectRadioButtonFromFlashcard(firstCard);
            displayFront(firstCard);
        } else {
            flashcardText.setText("No cards available to study.");
        }
    }

    private void filterByCriteria(String title, String header, List<String> options, String filterType) {
        Optional<List<String>> result = getSelectedCriteria(title, header, options);
        result.ifPresent(selectedItems -> {
            for (Flashcard flashcard : flashcards) {
                if ((filterType.equals("topic") && selectedItems.contains(flashcard.getTopic())) ||
                    (filterType.equals("subject") && selectedItems.contains(flashcard.getSubject()))) {
                    studyStack.push(flashcard);
                }
            }
        });
    }

    private Optional<List<String>> getSelectedCriteria(String title, String header, List<String> options) {
        Dialog<List<String>> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(header);

        ButtonType confirmButtonType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        ListView<String> listView = new ListView<>();
        listView.getItems().addAll(options);
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listView.setPrefHeight(200);

        dialog.getDialogPane().setContent(listView);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                return new ArrayList<>(listView.getSelectionModel().getSelectedItems());
            }
            return null;
        });

        return dialog.showAndWait();
    }

    public void displayFront(Flashcard flashcard) {
        if (flashcard.hasTextFront()) {
            flashcardImage.setVisible(false);
            flashcardText.setVisible(true);
            flashcardText.setText(flashcard.getFrontSideText());
            flashcardText.setPrefWidth(480);
            flashcardText.setPrefHeight(325);
            flashcardText.setAlignment(Pos.CENTER);
            flashcardText.setOnMouseClicked(event -> displayBack(flashcard));
        } else {
            Image frontImage = flashcard.getFrontSideImage();
            flashcardText.setVisible(false);
            flashcardImage.setVisible(true);
            flashcardImage.setImage(frontImage);
            flashcardImage.setFitWidth(480);
            flashcardImage.setFitHeight(325);
            flashcardImage.setPreserveRatio(true);
            imageStackPane.getChildren().clear();
            imageStackPane.getChildren().add(flashcardImage);
            imageStackPane.setAlignment(Pos.CENTER);
            flashcardImage.setOnMouseClicked(event -> displayBack(flashcard));
        }
    }

    public void displayBack(Flashcard flashcard) {
        if (flashcard.hasTextBack()) {
            flashcardImage.setVisible(false);
            flashcardText.setVisible(true);
            flashcardText.setText(flashcard.getBackSideText());
            flashcardText.setPrefWidth(480);
            flashcardText.setPrefHeight(325);
            flashcardText.setAlignment(Pos.CENTER);
            flashcardText.setOnMouseClicked(event -> displayFront(flashcard));
        } else {
            Image backImage = flashcard.getBackSideImage();
            flashcardText.setVisible(false);
            flashcardImage.setVisible(true);
            flashcardImage.setImage(backImage);
            flashcardImage.setFitWidth(480);
            flashcardImage.setFitHeight(325);
            flashcardImage.setPreserveRatio(true);
            imageStackPane.getChildren().clear();
            imageStackPane.getChildren().add(flashcardImage);
            imageStackPane.setAlignment(Pos.CENTER);
            flashcardImage.setOnMouseClicked(event -> displayFront(flashcard));
        }
    }

    public boolean savedUnderstanding(Flashcard flashcard) throws IOException {
        RadioButton selectedToggle = (RadioButton) understanding.getSelectedToggle();
        if (selectedToggle != null) {
            int thisUnderstanding = Integer.parseInt(selectedToggle.getText());
            flashcard.setUnderstanding(thisUnderstanding);
            understanding.selectToggle(null);
            deck.saveDeck();
            return true;
        } else {
            new AlertMaker(Alert.AlertType.ERROR, "No Understanding Selected", null, "\nNo understanding toggle selected. Please rate your understanding");
            return false;
        }
    }

    private int getDifficultyToken(int difficulty) {
        return switch (difficulty) {
            case 1 -> 1;
            case 2 -> 3;
            case 3 -> 5;
            default -> 0;
        };
    }

    private int getUnderstandingToken(int understanding) {
        return switch (understanding) {
            case 1 -> 10;
            case 2 -> 8;
            case 3 -> 5;
            case 4 -> 3;
            case 5 -> 1;
            case 0 -> 100;
            default -> 0;
        };
    }

    public void selectRadioButtonFromFlashcard(Flashcard flashcard) {
        understanding.selectToggle(null);
        for (Toggle toggle : understanding.getToggles()) {
            RadioButton radioButton = (RadioButton) toggle;
            if (Integer.parseInt(radioButton.getText()) == flashcard.getUnderstanding()) {
                understanding.selectToggle(radioButton);
                break;
            }
        }
    }

    @FXML
    public void goBack(ActionEvent event) throws IOException {
        timerClass.stop();
        Parent root = FXMLLoader.load(getClass().getResource("HomeScreen.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
