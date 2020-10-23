package CurrencyConverterJavaFX;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class About {

    public static void display(String backgroundColorOfTheWindow) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL); //Block user interaction until this is taken care of.
        window.setTitle("- About -");

        Label label = new Label();
        label.setText("A small program that takes rates from a webpage and calculates conversions");
        label.setFont(new Font("Arial", 14));

        Label whyCreated = new Label();
        whyCreated.setText(" The program was created as a small project during studying java");

        Hyperlink url = new Hyperlink("https://api.exchangeratesapi.io/latest?symbols=JPY,USD,SEK");
        url.setOnAction(e -> {
            try {
                Desktop.getDesktop().browse(new URI("https://api.exchangeratesapi.io/latest?symbols=JPY,USD,SEK"));
            } catch (IOException | URISyntaxException ioException) {
                ioException.printStackTrace();
            }
        });

        Label label2 = new Label();
        label2.setText("Creators: Eddi & Marco");
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> window.close());

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label,whyCreated ,url ,label2, closeButton);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle(backgroundColorOfTheWindow);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }
}