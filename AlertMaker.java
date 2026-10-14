package org.example.csia;

import javafx.scene.control.Alert;

public class AlertMaker {
    public AlertMaker(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
