package edu.uchicago.mhmcdonald;


import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.lang.management.ManagementFactory;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.*;
import static javafx.scene.paint.Color.*;



/**
 * Created by markmcdonald on 7/26/16.
 */

public class BallController implements Initializable {

    private static final int mStartBalls = 4;
    private static final double minRadius = 10;
    private static final double maxRadius = 50;
    private static final double minSpeed = 50;
    private static final double maxSpeed = 175;
    private static final boolean randomness = true;
    private static Color mColor = Color.BLUE;
    private static  int numberofBalls = 4;
    private static double sChosenradius = 50.0;
    private static double sFrameinterval = 10.0;
    private static int mLapse = 10;
    private ObservableList<Ball> arrayOfBalls = FXCollections.observableArrayList();
    public static ExecutorService threadPool;
    private static final Color colorFill = BLUEVIOLET;
    private static final Color[] COLORS = new Color[]{RED, WHEAT, YELLOWGREEN,
            MAROON, BLUE, PINK, TOMATO};
    @FXML
    public Pane ballPane;
    @FXML
    private Button addBallButton;
    @FXML
    private Button timeButton;
    @FXML
    private ColorPicker cpkColor;
    @FXML
    private Slider ballSize;
    @FXML
    private Slider timeSize;
    @FXML
    private Label ballCount;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    //override the initialize method for the javaFx Controller class
        threadPool = Executors.newCachedThreadPool();

        //the following lines adjust some of the GUI features in the javaFX application
        cpkColor.setValue(BLUE);
        //ballSize is the slider which determines a new balls radius in pixels
        ballSize.setShowTickLabels(true);
        ballSize.setShowTickMarks(true);
        ballSize.setMajorTickUnit(50);
        ballSize.setMinorTickCount(5);
        ballSize.setBlockIncrement(10);
        //timeSize is the slider which determines the lag between frames
        timeSize.setShowTickLabels(true);
        timeSize.setShowTickMarks(true);
        timeSize.setMajorTickUnit(50);
        timeSize.setMinorTickCount(5);
        timeSize.setBlockIncrement(10);
        //ballCount is the label which displays the number of balls which currently appear on the panel
        ballCount.setText( String.valueOf(numberofBalls));
        ballCount.setVisible(true);

        /*the following print statments give some indication of the statistics about machine processing over this
        program*/
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("--Program Performance Summary--");
        System.out.println("This machine has " + cores + " cores available for processing");
        int threadCount = ManagementFactory.getThreadMXBean().getThreadCount();
        System.out.println("At the start of the proThreaded Program, there were " + threadCount + " threads in use." );


        //Add a listener to determine if user has changed the radius size for adding a new ball
        ballSize.valueProperty().addListener((arg0, arg1, arg2) -> {
            sChosenradius = ballSize.getValue();

        });
        //Add a listener to determine if user has changed the sleep time for threading
        timeSize.valueProperty().addListener((arg0, arg1, arg2) -> {
            sFrameinterval = timeSize.getValue();
        });

        //the javafx app must be responsive to changes in the size of the application's boundaries
        resizePane(ballPane);

        /*This lambda expression adds the listener for a double click on the JavaFX pane (ballPane). Upon a double
        click the pane will reset, clearing the arrayOfBalls and generating 4 new arrayOfBalls in the center of the pane.*/
        ballPane.addEventHandler(MouseEvent.MOUSE_CLICKED,
                event -> {
                    if (event.getClickCount() == 2) {
                        arrayOfBalls.clear();
                        createBalls(randomness, colorFill, mStartBalls, minRadius, maxRadius, minSpeed, maxSpeed, ballPane.getWidth() / 2, ballPane.getHeight() / 2);
                    }
                });

        /*This lambda expression adds the listener for a click on the Add Ball button. This creates a single new ball with
        * the color and size determined by the colorpicker and radius size set by the user.*/
        addBallButton.addEventHandler(MouseEvent.MOUSE_CLICKED,
                event -> {
                    if (event.getClickCount() >= 1) {

                        double sliderRadius = sChosenradius;
                        int singleBall = 1;
                        boolean notRandom = false;
                        //Increase the numberofBalls by one, which gets reflected in the label displayed on the toolbar
                        numberofBalls += 1;
                        mColor = cpkColor.getValue();
                        Color colorLook = mColor;
                        //spead is a function of the balls size(radius)
                        createBalls(notRandom, colorLook, singleBall, minRadius, sliderRadius, minSpeed, maxSpeed, ballPane.getWidth() / 2, ballPane.getHeight() / 2);
                    }
                });

        /*This lambda expression adds the listener for increasing the time between frames At the programs startBouncing, the default
        time is set to 10. Decreasing this does not seem to create noticable performance impacts. However increasing the time
        certainly causes negative performance impacts (as expected).*/
        timeButton.addEventHandler(MouseEvent.MOUSE_CLICKED,
                event -> {
                    if (event.getClickCount() >= 1) {
                        mLapse = (int)(sFrameinterval);
                        /*the mLapse variable is initially set to 10 nanoseconds. This seems to be the optimal time for
                        performance */
                    }
                });

        //This method adds the new ball to the javaFX pane as soon as the instantiated ball gets added to the arraylist
        arrayOfBalls.addListener(new ListChangeListener<Ball>() {
            @Override
            public void onChanged(Change<? extends Ball> change) {
                while (change.next()) {
                    change.getAddedSubList().stream().forEach((ball) -> {
                        ballPane.getChildren().add(ball.getView());
                    });
                    change.getRemoved().stream().forEach((ball) -> {
                        ballPane.getChildren().remove(ball.getView());
                    });
                }
            }
        });

        createBalls(randomness, colorFill, mStartBalls, minRadius, maxRadius, minSpeed, maxSpeed, 400, 300);
        startBouncing(ballPane);

    }
    //This method initiates the animation of the bouncing balls
    private void startBouncing(final Pane ballContainer) {
        final LongProperty lastFrameTime = new SimpleLongProperty(0);
        final AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long timestamp) {
                try
                {
                    /*this code determines the time lapse between animation. extending the duration
                    obviously results in a lag between animations, and thus creates a choppier appearance*/
                    Thread.sleep(mLapse);
                }
                catch(Exception event){}
                if (lastFrameTime.get() > 0) {
                    long elapsedTime = timestamp - lastFrameTime.get();

                    detectCollision(ballContainer.getWidth(), ballContainer.getHeight());
                    updateFrame(elapsedTime);
                }
                lastFrameTime.set(timestamp);
            }

        };
        timer.start();
    }

    private void updateFrame(long elapsedTime) {
        ballCount.setText( String.valueOf(numberofBalls));

        double elapsedSeconds = elapsedTime / 1_000_000_000.0;
        arrayOfBalls.stream().forEach((ball) -> {
            ball.setCenterX(ball.getCenterX() + elapsedSeconds * ball.getXVelocity());
            ball.setCenterY(ball.getCenterY() + elapsedSeconds * ball.getYVelocity());
        });
    }

    /*This is method presents the greates resource impact as we're performing an n^2 operation in terms of big-O notation.
    It's at this point that the multi-threading comes into play in the application.
    For each ball that gets added to the pane (and in doing so the observable arraylist) we added that ball to a thread
    contained in a threadpool.*/
    private void detectCollision(double maxX, double maxY){
        //detect collision begins by checking for collisions with the pane boundaries, this is what the parameters are used for
        //for loop is preferred to stream as we iterate through each ball in the arraylist, although a stream is possible too
        for (int thisBall = 0; thisBall < arrayOfBalls.size(); thisBall++) {
            //Use final variable to maintain thread safety
            final int ballNumber = thisBall;
            //Use ExecutorService for managing the pool of threads
            threadPool.execute(() -> {

                Ball ball1 = arrayOfBalls.get(ballNumber);

                //begin by checking balls position relative to the pane's boundaries
                wallCollision(ball1, maxX, maxY);

                //This nested for loop is where we get the n^2 time, but there is no alternate way that I can think of
                //We check each of the neighboring balls for collision with ball1
                //As above, a for loop is preferable here to a stream
                for (int nextBall = ballNumber + 1; nextBall < arrayOfBalls.size(); nextBall++ ) {

                    Ball ball2 = arrayOfBalls.get(nextBall);

                    //Get differences between centers of each of two balls, will be used to check for intersection

                    final double deltaX = ball2.getCenterX() - ball1.getCenterX();
                    final double deltaY = ball2.getCenterY() - ball1.getCenterY();

                    //Check for collision between ball1 and ball2
                    if (colliding(ball1, ball2, deltaX, deltaY)) {
                                bounce(ball1, ball2, deltaX, deltaY);
                            }
                }
            });
        }




    }

    //this method is called by detect collision method above to see if a ball has encountered the frame's boundaries
    private void wallCollision(Ball ball1, double maxX, double maxY){
        double xVel = ball1.getXVelocity();
                        double yVel = ball1.getYVelocity();
                        //check the sides
                        if ((ball1.getCenterX() - ball1.getRadius() <= 0 && xVel < 0)
                                || (ball1.getCenterX() + ball1.getRadius() >= maxX && xVel > 0)) {
                            ball1.setXVelocity(-xVel);
                        }
                        //check the top and bottom of the pane
                        if ((ball1.getCenterY() - ball1.getRadius() <= 0 && yVel < 0)
                                || (ball1.getCenterY() + ball1.getRadius() >= maxY && yVel > 0)) {
                            ball1.setYVelocity(-yVel);
                        }

    }


    public boolean colliding(final Ball b1, final Ball b2, final double deltaX, final double deltaY) {

        final double radiusSum = b1.getRadius() + b2.getRadius();
        if (deltaX * deltaX + deltaY * deltaY <= radiusSum * radiusSum) {
            if (deltaX * (b2.getXVelocity() - b1.getXVelocity())
                    + deltaY * (b2.getYVelocity() - b1.getYVelocity()) < 0) {
                return true;
            }
        }
        return false;
    }

    //Attribution: http://hyperphysics.phy-astr.gsu.edu/hbase/colsta.html
    private void bounce(final Ball ball1, final Ball ball2, final double deltaX, final double deltaY) {
        final double distance = sqrt(deltaX * deltaX + deltaY * deltaY);
        final double unitContactX = deltaX / distance;
        final double unitContactY = deltaY / distance;

        final double xVelocity1 = ball1.getXVelocity();
        final double yVelocity1 = ball1.getYVelocity();
        final double xVelocity2 = ball2.getXVelocity();
        final double yVelocity2 = ball2.getYVelocity();

        final double u1 = xVelocity1 * unitContactX + yVelocity1 * unitContactY;
        final double u2 = xVelocity2 * unitContactX + yVelocity2 * unitContactY;

        final double massSum = ball1.getMass() + ball2.getMass();
            //calculate the difference between ball masses
        final double massDiff = ball1.getMass() - ball2.getMass();

        //This is an implementation of the Elastic Headon Collision equation
        final double v1 = (2 * ball2.getMass() * u2 + u1 * massDiff) / massSum;
        final double v2 = (2 * ball1.getMass() * u1 - u2 * massDiff) / massSum;

        final double u1PerpX = xVelocity1 - u1 * unitContactX;
        final double u1PerpY = yVelocity1 - u1 * unitContactY;
        final double u2PerpX = xVelocity2 - u2 * unitContactX;
        final double u2PerpY = yVelocity2 - u2 * unitContactY;

        ball1.setXVelocity(v1 * unitContactX + u1PerpX);
        ball1.setYVelocity(v1 * unitContactY + u1PerpY);
        ball2.setXVelocity(v2 * unitContactX + u2PerpX);
        ball2.setYVelocity(v2 * unitContactY + u2PerpY);

    }

    /*This method uses the Ball class to add new arrayOfBalls to the arrayOfBalls array. The color, number of arrayOfBalls, size (radius),
    speed, and position are all parameters here. */
    private void createBalls(boolean multipleBalls, Color colorAppearance, int numBalls, double minRadius, double maxRadius, double minSpeed, double maxSpeed, double initialX, double initialY) {
        /*The if statement checks the first boolean parameter. If it's true, it will result in 4 arrayOfBalls being added.
         The else statement below is called when adding a single ball where color, and size are inputs provided by the
         user via the JavaFX GUI.*/
        if (multipleBalls == true) {
            final Random range = new Random();

            for (int thisBall = 0; thisBall < numBalls; thisBall++) {
                double radius = minRadius + (maxRadius - minRadius) * range.nextDouble();
                double mass = Math.pow((radius / 40), 3);

                final double speed = minSpeed + (maxSpeed - minSpeed) * range.nextDouble();
                final double angle = 2 * PI * range.nextDouble();
                Ball ball = new Ball(initialX, initialY, radius, speed * cos(angle),
                        speed * sin(angle), mass);
                ball.getView().setFill(COLORS[thisBall % COLORS.length]);
                arrayOfBalls.add(ball);
            }
        } else {
            //this portion of code is called when a single ball gets added by the user
            final Random range = new Random();
            double radius = maxRadius;
            double mass = Math.pow((radius / 40), 3);

            final double speed = minSpeed + (maxSpeed - minSpeed);
            //angle is still random
            final double angle = 2 * PI * range.nextDouble();
            Ball ball = new Ball(initialX, initialY, radius, speed * cos(angle),
                    speed * sin(angle), mass);
            ball.getView().setFill(colorAppearance);
            arrayOfBalls.add(ball);
        }
    }

    /*This method is called when the user re-sizes the Pane. The wall collision is then updated.*/
    private void resizePane(final Pane javaFxPane) {
        javaFxPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() < oldValue.doubleValue()) {
                arrayOfBalls.stream().forEach((ball) -> {
                    double max = newValue.doubleValue() - ball.getRadius();
                    if (ball.getCenterX() > max) {
                        ball.setCenterX(max);
                    }
                });
            }
        });

        javaFxPane.heightProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() < oldValue.doubleValue()) {
                arrayOfBalls.stream().forEach((ball) -> {
                    double max = newValue.doubleValue() - ball.getRadius();
                    if (ball.getCenterY() > max) {
                        ball.setCenterY(max);
                    }

                });
            }
        });
    }

    //Attribution: http://stackoverflow.com/questions/1250643/how-to-wait-for-all-threads-to-finish-using-executorservice
    public static void shutdownThreadsAndWaitforRunnabletoStop(ExecutorService threadPool) {
        int threadCount = ManagementFactory.getThreadMXBean().getThreadCount();
        System.out.println("Just before exiting the proThreaded Program, there were " + threadCount + " threads in use." );

        Platform.exit();
        System.exit(0);

        threadPool.shutdown(); // Disable new tasks from being submitted
        try {

            // Wait a while for existing tasks to terminate
            if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                threadPool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!threadPool.awaitTermination(60, TimeUnit.SECONDS))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            threadPool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

}
















