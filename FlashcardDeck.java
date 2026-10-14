package org.example.csia;

import java.io.*;
import java.util.*;

public class FlashcardDeck implements Serializable {
    private LinkedList<Flashcard> flashcards; // LinkedList to hold the flashcards
    private HashMap<String, List<String>> subjectTopics; // Map to store subjects and their associated topics
    private static FlashcardDeck instance; // Singleton instance for consistency across classes

    public FlashcardDeck() {
        flashcards = new LinkedList<>();
        subjectTopics = new HashMap<>();
    }

    /* Flashcard editor methods */
    public void add(Flashcard flashcard) {
        flashcards.add(flashcard);
    }

    public void remove(Flashcard flashcard) {
        flashcards.remove(flashcard);
    }

    public void replace(Flashcard editedFlashcard, int index) {
        if (index >= 0 && index < flashcards.size()) {
            Flashcard existingFlashcard = flashcards.get(index);
            editedFlashcard.setIndex(existingFlashcard.getIndex());
            flashcards.set(index, editedFlashcard);
        }
    }

    /* Subject/topic management */
    public void addSubject(String subject) {
        subjectTopics.putIfAbsent(subject, new ArrayList<>());
        saveCategories();
    }

    public void addTopic(String subject, String topic) {
        subjectTopics.computeIfPresent(subject, (k, v) -> {
            if (!v.contains(topic)) {
                v.add(topic);
            }
            return v;
        });
        saveCategories();
    }

    /* Saving and loading methods */
    public void loadAllImages() {
        for (Flashcard flashcard : flashcards) {
            try {
                flashcard.loadImages();
            } catch (IOException e) {
                System.err.println("Failed to load images for flashcard: " + flashcard.getIndex());
            }
        }
    }

    public void saveDeck() throws IOException {
        FileOutputStream fileOut = new FileOutputStream("flashcards.ser");
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(flashcards);
        out.close();
        fileOut.close();
    }

    @SuppressWarnings("unchecked")
    public void loadDeck() throws IOException, ClassNotFoundException {
        File file = new File("flashcards.ser");
        if (file.exists()) {
            FileInputStream fileIn = new FileInputStream(file);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            flashcards = (LinkedList<Flashcard>) in.readObject();
            loadAllImages();
        } else {
            flashcards = new LinkedList<>();
        }
    }

    public void saveCategories() {
        try {
            FileOutputStream fileOut = new FileOutputStream("categories.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(subjectTopics);
            out.close();
            fileOut.close();
        } catch (Exception e) {
            System.out.println("Error saving categories.");
        }
    }

    @SuppressWarnings("unchecked")
    public void loadCategories() {
        try {
            FileInputStream fileIn = new FileInputStream("categories.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            subjectTopics = (HashMap<String, List<String>>) in.readObject();
        } catch (Exception ignore) {
            System.out.println("None yet made.");
        }
    }

    /* Getters */
    public LinkedList<Flashcard> getFlashcards() { return flashcards; }

    public static FlashcardDeck getInstance() {
        if (instance == null) {
            instance = new FlashcardDeck();
        }
        return instance;
    }

    public int getDeckSize() { return flashcards.size(); }

    public List<String> getSubjects() {
        return new LinkedList<>(subjectTopics.keySet());
    }

    public List<String> getTopicsForSubject(String subject) {
        return subjectTopics.getOrDefault(subject, List.of());
    }

    public List<String> getAllTopics() {
        List<String> allTopics = new LinkedList<>();
        for (List<String> topics : subjectTopics.values()) {
            allTopics.addAll(topics);
        }
        return allTopics;
    }
}
