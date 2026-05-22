package com.ecommerce.utils;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * Animation Utilities - Smooth UI transitions
 */
public class AnimationUtils {

    /**
     * Fade in animation
     */
    public static void fadeIn(Node node, Duration duration) {
        FadeTransition fade = new FadeTransition(duration, node);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    /**
     * Fade out animation
     */
    public static void fadeOut(Node node, Duration duration) {
        FadeTransition fade = new FadeTransition(duration, node);
        fade.setFromValue(1);
        fade.setToValue(0);
        fade.play();
    }

    /**
     * Scale animation
     */
    public static void scale(Node node, Duration duration, double fromScale, double toScale) {
        ScaleTransition scale = new ScaleTransition(duration, node);
        scale.setFromX(fromScale);
        scale.setFromY(fromScale);
        scale.setToX(toScale);
        scale.setToY(toScale);
        scale.play();
    }

    /**
     * Slide animation
     */
    public static void slide(Node node, Duration duration, double fromX, double toX) {
        TranslateTransition translate = new TranslateTransition(duration, node);
        translate.setFromX(fromX);
        translate.setToX(toX);
        translate.play();
    }

    /**
     * Bounce in animation
     */
    public static void bounceIn(Node node) {
        ScaleTransition scale = new ScaleTransition(Duration.millis(500), node);
        scale.setFromX(0);
        scale.setFromY(0);
        scale.setToX(1);
        scale.setToY(1);
        scale.play();
    }

    /**
     * Pulse animation
     */
    public static void pulse(Node node) {
        ScaleTransition scale = new ScaleTransition(Duration.millis(600), node);
        scale.setFromX(1);
        scale.setFromY(1);
        scale.setToX(1.1);
        scale.setToY(1.1);
        scale.setCycleCount(2);
        scale.setAutoReverse(true);
        scale.play();
    }
}
