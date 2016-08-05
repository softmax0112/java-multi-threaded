package edu.uchicago.mhmcdonald;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.apache.commons.lang.StringUtils;

public class HelloController
{


    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private Label messageLabel;

    public void sayHello() {

        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();

        StringBuilder builder = new StringBuilder();

        if (!StringUtils.isEmpty(firstName)) {
            builder.append(firstName);
        }

        if (!StringUtils.isEmpty(lastName)) {
            if (builder.length() > 0) {
                builder.append(" ");
            }
            builder.append(lastName);
        }

        if (builder.length() > 0) {
            String name = builder.toString();

            messageLabel.setText("Hello " + name);
        } else {
            messageLabel.setText("Hello mysterious person");
        }
    }

}
