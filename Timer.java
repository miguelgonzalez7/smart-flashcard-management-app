package org.example.csia;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import java.text.DecimalFormat;

public class Timer {
    private Label counterLabel;
    private AnchorPane breakScreen;
    private Label breakTimer;
    private Label breakLabel;

    private int second;
    private int minute;
    private int repetitions;
    private final DecimalFormat format = new DecimalFormat("00");
    private Timeline timeline;

    public Timer() {
        second = 0;
        minute = 0;
    }

    public void initialize(Label timerLabel, AnchorPane breakScreen, Label breakTimer, Label breakLabel) {
        this.counterLabel = timerLabel;
        this.breakScreen = breakScreen;
        this.breakTimer = breakTimer;
        this.breakLabel = breakLabel;
        updateTimerDisplay();
    }

    public void startStopwatch() {
        second = 0;
        minute = 0;
        updateTimerDisplay();
        counterLabel.setText("00:00");
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            second++;
            if (second == 60) {
                second = 0;
                minute++;
            }
            updateTimerDisplay();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void startCountdown(int minutes, int seconds) {
        minute = minutes;
        second = seconds;
        updateTimerDisplay();
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if (minute == 0 && second == 0) {
                timeline.stop();
                breakLabel.setVisible(false);
                breakScreen.setVisible(true);
                breakTimer.setText("Session Over!");
            } else {
                if (second == 0) {
                    second = 59;
                    minute--;
                } else {
                    second--;
                }
                updateTimerDisplay();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void startPomodoroCycle(int workMinutes, int workSeconds, int breakMinutes, int breakSeconds, int repetitions) {
        this.repetitions = repetitions;
        startPomodoroWork(workMinutes, workSeconds, breakMinutes, breakSeconds);
    }

    private void startPomodoroWork(int workMinutes, int workSeconds, int breakMinutes, int breakSeconds) {
        this.minute = workMinutes;
        this.second = workSeconds;
        updateTimerDisplay();

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if (minute == 0 && second == 0) {
                timeline.stop();
                breakScreen.setVisible(true);
                repetitions--;
                if (repetitions > 0) {
                    startPomodoroBreak(breakMinutes, breakSeconds, workMinutes, workSeconds);
                } else {
                    breakLabel.setVisible(false);
                    breakTimer.setText("Session Over!");
                }
            } else {
                if (second == 0) {
                    second = 59;
                    minute--;
                } else {
                    second--;
                }
                updateTimerDisplay();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void startPomodoroBreak(int breakMinutes, int breakSeconds, int workMinutes, int workSeconds) {
        this.minute = breakMinutes;
        this.second = breakSeconds;
        updateTimerDisplay();
        breakTimer.setText(String.format("%02d:%02d", minute, second));

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if (minute == 0 && second == 0) {
                timeline.stop();
                breakScreen.setVisible(false);
                startPomodoroWork(workMinutes, workSeconds, breakMinutes, breakSeconds);
            } else {
                if (second == 0) {
                    second = 59;
                    minute--;
                } else {
                    second--;
                }
                breakTimer.setText(String.format("%02d:%02d", minute, second));
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateTimerDisplay() {
        String ddSecond = format.format(second);
        String ddMinute = format.format(minute);
        if (counterLabel != null) {
            counterLabel.setText(ddMinute + ":" + ddSecond);
        }
    }

    public void stop() {
        if (timeline != null) {
            timeline.stop();
        }
    }
}
