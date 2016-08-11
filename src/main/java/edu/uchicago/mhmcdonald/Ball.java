package edu.uchicago.mhmcdonald;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

import java.util.concurrent.Callable;
import static java.lang.Math.sqrt;

/**
 * Created by markmcdonald on 8/8/16.
 */

public class Ball {
    private final DoubleProperty xVelocity;     //The velocity is determined in pixels per second
    private final DoubleProperty yVelocity;     //The velocity is determined in pixels per second
    private final ReadOnlyDoubleWrapper speed;
    private final double mass;
    private final double radius; // Sized in pixels

    private final Circle view;

    public Ball(double centerX, double centerY, double radius,
                double xVelocity, double yVelocity, double mass) {

        this.view = new Circle(centerX, centerY, radius);
        this.xVelocity = new SimpleDoubleProperty(this, "xVelocity", xVelocity);
        this.yVelocity = new SimpleDoubleProperty(this, "yVelocity", yVelocity);
        this.speed = new ReadOnlyDoubleWrapper(this, "speed");
        speed.bind(Bindings.createDoubleBinding(new Callable<Double>() {

            @Override
            public Double call() throws Exception {
                final double xVel = getXVelocity();
                final double yVel = getYVelocity();
                return sqrt(xVel * xVel + yVel * yVel);
            }
        }, this.xVelocity, this.yVelocity));
        this.mass = mass;
        this.radius = radius;
        view.setRadius(radius);
    }

    protected double getMass() {
        return mass;
    }

    protected double getRadius() {
        return radius;
    }

    protected final double getXVelocity() {
        return xVelocity.get();
    }

    protected final void setXVelocity(double xVelocity) {
        this.xVelocity.set(xVelocity);
    }

    protected final double getYVelocity() {
        return yVelocity.get();
    }

    protected final void setYVelocity(double yVelocity) {
        this.yVelocity.set(yVelocity);
    }

    protected final double getCenterX() {
        return view.getCenterX();
    }

    protected final void setCenterX(double centerX) {
        view.setCenterX(centerX);
    }

    protected final double getCenterY() {
        return view.getCenterY();
    }

    protected final void setCenterY(double centerY) {
        view.setCenterY(centerY);
    }

    protected Shape getView() {
        return view;
    }
}