# Study App

A JavaFX desktop application for creating, organising, and studying flashcards, with support for text and image-based cards, subject/topic categorisation, adaptive study sessions, and integrated timers.

## Features
- **Flashcard Management:** Create, edit, delete, and flip flashcards with text or images on either side.
- **Organisation:** Categorise cards by subject and topic, with difficulty levels (Easy, Medium, Hard).
- **Study Modes:** Sequential, Random, Subject, Topic, and Smart Study.
- **Smart Study:** Prioritises cards using difficulty and self-reported understanding levels.
- **Study Timers:** Free Study, Stopwatch, Countdown, and configurable Pomodoro sessions.
- **Progress Tracking:** Rate understanding from 1–5, with ratings saved for future sessions.
- **Search & Filtering:** Find cards by text, difficulty, subject, and topic.
- **Data Persistence:** Automatically saves flashcards, categories, understanding ratings, and images locally.

## Technologies
- **Java** — application logic and data management.
- **JavaFX & FXML** — graphical user interface.
- **Java Serialization** — persistent storage of flashcards and categories.

## Data Storage
The application creates the following local files and directory in its working directory:
- `flashcards.ser` — serialised flashcard deck.
- `categories.ser` — serialised subjects and topics.
- `flashcardCounter.txt` — flashcard counter.
- `images/` — saved flashcard images in PNG format.

## Running
Launch the application through `org.example.csia.Start` or the packaged JAR entry point, `org.example.csia.FakeMain`. JavaFX dependencies and the project's build configuration must be available.

**Note:** Keep the generated data files and `images/` directory together to preserve saved flashcards and their images.
