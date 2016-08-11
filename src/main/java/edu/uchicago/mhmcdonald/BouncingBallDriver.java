package edu.uchicago.mhmcdonald;

/**
 * Created by markmcdonald on 8/8/16.
 */


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


/*This class is where the proThreaded program can be run. It is responsible for constructing an instance of the
 Application class. It contains references to the .fxml and .css files which control the appearance of the application
 gui. It follows the root, scene, stage pattern one would expect in a JavaFX application. The main() method is ignored.
 This method only serves in case the application can not be launched through deployment artifacts, e.g., in IDEs with
 limited FX support. */

public class BouncingBallDriver extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/threaded.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/styles.css");
        stage.setTitle("ProThreaded");
        stage.setScene(scene);
        stage.show();
    }

    /*The stop method is the last method to be called in the life-cycle of the Application class. Instead of simply
    stopping a thread, we have to notify it that it should be terminated. The shutdown and Await termination method
    defined in the BallController class is doing this for all threads in the thread pool as soon as the user clicks close.
     * Attribution: http://stackoverflow.com/questions/26619566/javafx-stage-close-handler */
    @Override
    public void stop() {
        BallController.shutdownThreadsAndWaitforRunnabletoStop(BallController.threadPool);
    }


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
