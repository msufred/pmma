package com.gemseeker.pmma.ui.components;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.SkinBase;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

/**
 * Created by felipe on 12/08/15.
 */
public class RippleSkinFactory {
	
    static public void getRippleEffect(SkinBase skinBase, Region node, Shape clip) {

        node.setClip(clip);
        Circle ripple = new Circle();
        ripple.radiusProperty().bind(node.widthProperty().divide(2));
        ripple.getStyleClass().add("ripple");

        skinBase.getChildren().add(ripple);
        skinBase.getChildren().addListener((ListChangeListener.Change c) -> {
            ObservableList observableList = c.getList();
            if (observableList.indexOf(ripple) == -1) {
                observableList.add(ripple);
            }
        });

        ripple.setScaleX(0);
        ripple.setScaleY(0);
        ripple.setOpacity(0.5);
        FadeTransition fade = new FadeTransition(new Duration(500), ripple);
        fade.setToValue(0);
        ScaleTransition scale = new ScaleTransition(new Duration(250), ripple);
        scale.setToX(1);
        scale.setToY(1);
        ParallelTransition rippleEffect = new ParallelTransition(fade, scale);
        rippleEffect.setInterpolator(Interpolator.EASE_OUT);
        rippleEffect.setOnFinished((ActionEvent event) -> {
            ripple.setOpacity(0.5);
            ripple.setScaleX(0);
            ripple.setScaleY(0);
        });

        node.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {

            ripple.setCenterX(event.getX());
            ripple.setCenterY(event.getY());
            rippleEffect.play();
        });

    }

    static public void getRippleEffect(SkinBase skinBase, Region node) {
        Rectangle rippleClip = new Rectangle();
        rippleClip.widthProperty().bind(node.widthProperty());
        rippleClip.heightProperty().bind(node.heightProperty());
        getRippleEffect(skinBase, node, rippleClip);

    }
    
    public static Circle createRipple(Region view, Duration rippleDuration, Duration shadowDuration, Color rippleColor) {
    	Rectangle rippleClip = new Rectangle();
        rippleClip.setArcWidth(16);
        rippleClip.setArcHeight(16);
        rippleClip.widthProperty().bind(view.widthProperty());
        rippleClip.heightProperty().bind(view.heightProperty());

        Circle circleRipple = new Circle(0.1, rippleColor);
        circleRipple.setClip(rippleClip);
        circleRipple.setOpacity(0.0);
        /*Fade Transition*/
        FadeTransition fadeTransition = new FadeTransition(rippleDuration, circleRipple);
        fadeTransition.setInterpolator(Interpolator.EASE_OUT);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        /*ScaleTransition*/
        final Timeline scaleRippleTimeline = new Timeline();
        DoubleBinding circleRippleRadius = new DoubleBinding() {
            {
                bind(view.heightProperty(), view.widthProperty());
            }

            @Override
            protected double computeValue() {
                return Math.max(view.heightProperty().get(), view.widthProperty().get() * 1.25);
            }
        };
        circleRippleRadius.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            KeyValue scaleValue = new KeyValue(circleRipple.radiusProperty(), newValue, Interpolator.EASE_OUT);
            KeyFrame scaleFrame = new KeyFrame(rippleDuration, scaleValue);
            scaleRippleTimeline.getKeyFrames().add(scaleFrame);
        });
        /*ShadowTransition*/
        Animation animation = new Transition() {
            {
                setCycleDuration(shadowDuration);
                setInterpolator(Interpolator.EASE_OUT);
            }

            @Override
            protected void interpolate(double frac) {
                view.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.rgb(0, 0, 0, 0.30), 5 + (10 * frac), 0.10 + ((3 * frac) / 10), 0, 2 + (4 * frac)));

            }
        };
        animation.setCycleCount(2);
        animation.setAutoReverse(true);

        final SequentialTransition rippleTransition = new SequentialTransition();
        rippleTransition.getChildren().addAll(
                scaleRippleTimeline,
                fadeTransition
        );

        final ParallelTransition parallelTransition = new ParallelTransition();

        view.getStyleClass().addListener((ListChangeListener.Change<? extends String> c) -> {
            if (c.getList().indexOf("flat") == -1 && c.getList().indexOf("toggle") == -1) {
                view.setMinWidth(88);
                view.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.rgb(0, 0, 0, 0.30), 5, 0.10, 0, 2));
                parallelTransition.getChildren().addAll(rippleTransition, animation);
            } else {

                parallelTransition.getChildren().addAll(rippleTransition);
                view.setMinWidth(Region.USE_COMPUTED_SIZE);
                view.setEffect(null);
            }
        });

        view.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            parallelTransition.stop();
            circleRipple.setOpacity(0.0);
            circleRipple.setRadius(0.1);
            circleRipple.setCenterX(event.getX());
            circleRipple.setCenterY(event.getY());
            parallelTransition.play();

        });
        
        return circleRipple;
    }
}
