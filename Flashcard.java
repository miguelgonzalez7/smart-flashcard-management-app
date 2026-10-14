package org.example.csia;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// 'Serializable' allows the object to be serialized for persistence
public class Flashcard implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; // For serialization versioning
    private static final String IMAGE_DIRECTORY = "images"; // Directory for storing images
    private static int flashcardCounter = 0; // Counter for flashcards

    private transient Image frontSideImage; // Transient to prevent 'Image' serialization
    private transient Image backSideImage; // Transient to prevent 'Image' serialization

    // Serialized fields
    private String frontImageID; // Front image ID for storage reference
    private String backImageID; // Back image ID for storage reference
    private String frontSideText; // Front side text content
    private String backSideText; // Back side text content
    private String subject; // Subject of the flashcard
    private String topic; // Topic of the flashcard
    private int difficulty; // Difficulty level (1-3)
    private int understanding; // Understanding level (1-5)
    private int flashcardIndex; // Unique index for each flashcard
    private boolean isFlipped; // Indicates if the flashcard is flipped
    private int tokens; // Token count for smart studying

    /* Constructors (overloading for different parameters) */

    // Default constructor
    public Flashcard() {
        this.understanding = 0;
        this.isFlipped = false;
        this.flashcardIndex = FlashcardDeck.getInstance().getDeckSize();
    }

    // Constructor for text-only front and back sides
    public Flashcard(int difficulty, String subject, String topic, String frontSideText, String backSideText) {
        this.difficulty = difficulty;
        this.subject = subject;
        this.topic = topic;
        this.frontSideText = frontSideText;
        this.backSideText = backSideText;
        this.understanding = 0;
        this.isFlipped = false; // default to show frontside
        this.flashcardIndex = FlashcardDeck.getInstance().getDeckSize();
        this.tokens = 0;
    }

    // Constructor for image-only front and back sides
    public Flashcard(int difficulty, String subject, String topic, Image frontSideImage, Image backSideImage) {
        this.difficulty = difficulty;
        this.subject = subject;
        this.topic = topic;
        this.frontSideImage = frontSideImage;
        this.backSideImage = backSideImage;
        this.understanding = 0;
        this.isFlipped = false;
        this.flashcardIndex = FlashcardDeck.getInstance().getDeckSize();
        this.tokens = 0;
    }

    // Constructor for text front side and image back side
    public Flashcard(int difficulty, String subject, String topic, String frontSideText, Image backSideImage) {
        this.difficulty = difficulty;
        this.subject = subject;
        this.topic = topic;
        this.frontSideText = frontSideText;
        this.backSideImage = backSideImage;
        this.understanding = 0;
        this.isFlipped = false;
        this.flashcardIndex = FlashcardDeck.getInstance().getDeckSize();
        this.tokens = 0;
    }

    // Constructor for image front side and text back side
    public Flashcard(int difficulty, String subject, String topic, Image frontSideImage, String backSideText) {
        this.difficulty = difficulty;
        this.subject = subject;
        this.topic = topic;
        this.frontSideImage = frontSideImage;
        this.backSideText = backSideText;
        this.understanding = 0;
        this.isFlipped = false;
        this.flashcardIndex = FlashcardDeck.getInstance().getDeckSize();
        this.tokens = 0;
    }

    // Save image to the file system with the unique identifier
    public void saveImageToFile(Image image, int flashcardId, String side) throws IOException {
        // Convert Image to BufferedImage for saving
        BufferedImage bufferedImage = convertImageToBufferedImage(image);

        // Create the directory to save images if it doesn't exist
        Path imageDirectory = Paths.get("images");
        if (!Files.exists(imageDirectory)) {
            Files.createDirectory(imageDirectory);
        }

        // Generate a unique image name using the flashcard ID and side
        String imageID = generateImageID(flashcardId, "_" + side);
        if (side.equals("front")) {
            frontImageID = imageID;
        } else if (side.equals("back")) {
            backImageID = imageID;
        }

        // Save the image as PNG in the directory
        File file = new File(imageDirectory.toFile(), imageID + ".png");
        ImageIO.write(bufferedImage, "PNG", file);
    }

    // Delete old images if they exist (cleanup method)
    public static void deleteImages(int flashcardId) {
        String front_IDToDelete = generateImageID(flashcardId, "_front");
        String back_IDToDelete = generateImageID(flashcardId, "_back");

        File frontImageFile = new File(IMAGE_DIRECTORY, front_IDToDelete + ".png");
        File backImageFile = new File(IMAGE_DIRECTORY, back_IDToDelete + ".png");

        // Delete the image files if they exist
        if (frontImageFile.exists()) {
            frontImageFile.delete();
        }
        if (backImageFile.exists()) {
            backImageFile.delete();
        }
    }

    // Load images from the specified directory
    public void loadImages() throws IOException {
        // Load front image if available
        if (frontImageID != null) {
            File frontImageFile = new File(IMAGE_DIRECTORY, frontImageID + ".png");
            if (frontImageFile.exists()) {
                frontSideImage = new Image(frontImageFile.toURI().toString());
            }
        }
        // Load back image if available
        if (backImageID != null) {
            File backImageFile = new File(IMAGE_DIRECTORY, backImageID + ".png");
            if (backImageFile.exists()) {
                backSideImage = new Image(backImageFile.toURI().toString());
            }
        }
    }

    // Save flashcard counter
    public static void saveCounter() throws IOException {
        File file = new File("flashcardCounter.txt");
        if (!file.exists()) {
            file.createNewFile();
        }
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(String.valueOf(flashcardCounter));
        }
    }

    // Load flashcard counter
    public static void loadCounter() throws IOException {
        File file = new File("flashcardCounter.txt");
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                flashcardCounter = Integer.parseInt(reader.readLine());
            }
        } else {
            flashcardCounter = 0;
        }
    }

    /* Getters and Setters */
    public static void incrementFlashcardCounter() { flashcardCounter++; }
    public int getIndex() { return flashcardIndex; }
    public void setIndex(int flashcardIndex) { this.flashcardIndex = flashcardIndex; }
    public void setUnderstanding(int understanding) { this.understanding = understanding; }
    public int getUnderstanding() { return understanding; }
    public boolean isFlipped() { return isFlipped; }
    public void flip() { isFlipped = !isFlipped; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public int getDifficulty() { return difficulty; }
    public int getTokens() { return tokens; }
    public void setTokens(int tokens) { this.tokens = tokens; }
    public String getFrontSideText() { return frontSideText; }
    public String getBackSideText() { return backSideText; }
    public Image getFrontSideImage() { return frontSideImage; }
    public Image getBackSideImage() { return backSideImage; }

    /* Utility methods */
    public boolean hasTextFront() { return frontSideText != null; }
    public boolean hasImageFront() { return frontSideImage != null; }
    public boolean hasTextBack() { return backSideText != null; }
    public boolean hasImageBack() { return backSideImage != null; }

    // Convert JavaFX Image to BufferedImage (for saving)
    private static BufferedImage convertImageToBufferedImage(Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = image.getPixelReader().getColor(x, y);
                int alpha = (int) (color.getOpacity() * 255);
                int red = (int) (color.getRed() * 255);
                int green = (int) (color.getGreen() * 255);
                int blue = (int) (color.getBlue() * 255);

                int argb = (alpha << 24) | (red << 16) | (green << 8) | blue;
                bufferedImage.setRGB(x, y, argb);
            }
        }
        return bufferedImage;
    }

    private static String generateImageID(int flashcardNumber, String side) {
        return String.format("%04d%s", flashcardNumber, side);
    }
}
