package CurrencyConverterJavaFX;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;

public class AlertBox {

    public static void display(String title, String message, String backgroundColorOfTheWindow) {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL); //Block user interaction until this is taken care of.
        window.setTitle(title);
        window.setMinWidth(250);

        Label label = new Label();
        label.setText(message);
        Button closeButton = new Button("Close the window");
        closeButton.setOnAction(e -> window.close());

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, closeButton);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle(backgroundColorOfTheWindow);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }
}