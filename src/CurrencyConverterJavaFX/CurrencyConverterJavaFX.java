package CurrencyConverterJavaFX;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class CurrencyConverterJavaFX extends Application {
    String backgroundColorOfTheWindow = "-fx-background-color: #C6EDFF";
    HashMap<String, Double> rates = new HashMap<>();
    String contentFromWebpage;
    String dateFromStringFromWebpage;
    String webpageWeWannaReadFrom = "https://api.exchangeratesapi.io/latest?symbols=JPY,USD,SEK";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage window) {
        try {
            contentFromWebpage = readFromInternet(webpageWeWannaReadFrom);
            setRatesFromString(contentFromWebpage);
        } catch (IOException ioException) {
            AlertBox.display("Error - IOException", "The webpage cant be read", backgroundColorOfTheWindow);
        } catch (NumberFormatException numberFormatException) {
            AlertBox.display("Error - NumberFormatException", "The string cant be processed", backgroundColorOfTheWindow);
        }

        window.setTitle("CC - Currency Converter"); //Title of the window
        //Menubar ------------------------------------------------------------------------------------------------------------------
        /* Create a new MenuBar. */
        MenuBar menu = new MenuBar();
        /* Create new sub menus. */
        Menu menuMain = new Menu("Main");
        MenuItem menuMainStart = new MenuItem("Start");
        MenuItem menuMainRates = new MenuItem("Rates");
        MenuItem menuMainExit = new MenuItem("Exit");
        menuMain.getItems().addAll(menuMainStart, menuMainRates, menuMainExit);
        Menu menuHelp = new Menu("Help");
        MenuItem menuHelpAbout = new MenuItem("About");
        menuHelp.getItems().add(menuHelpAbout); //Adds about to Help
        menu.getMenus().addAll(menuMain, menuHelp); ///* Adding all sub menus at ones to a MenuBar. */
        VBox menuVBox = new VBox(); //Sets VBox for menu row
        menuVBox.getChildren().addAll(menu); //Add all menu items to menus

        //Main window --------------------------------------------------------------------------------------------------------------
        TextField fromHowMuchToConvertTextField = new TextField();
        fromHowMuchToConvertTextField.setText("1");
        fromHowMuchToConvertTextField.setEditable(true);
        fromHowMuchToConvertTextField.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> fromCurrencyComboBox = new ComboBox<>();
        fromCurrencyComboBox.getItems().add("SEK");
        fromCurrencyComboBox.getItems().add("EUR");
        fromCurrencyComboBox.getItems().add("USD");
        fromCurrencyComboBox.getItems().add("JPY");
        fromCurrencyComboBox.setValue("SEK");

        TextField toHowMuchToConvertTextField = new TextField();
        toHowMuchToConvertTextField.setEditable(false);
        toHowMuchToConvertTextField.setText("");
        toHowMuchToConvertTextField.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> toCurrencyComboBox = new ComboBox<>();
        toCurrencyComboBox.getItems().add("SEK");
        toCurrencyComboBox.getItems().add("EUR");
        toCurrencyComboBox.getItems().add("USD");
        toCurrencyComboBox.getItems().add("JPY");
        toCurrencyComboBox.setValue("SEK");

        Button convertButton = new Button("Convert!");
        convertButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        Label dateLabel = new Label();
        dateLabel.setText("Rates collected");
        dateLabel.setFont(new Font("Arial", 12));

        TextField dateTheRatesWasPickedUp = new TextField();
        dateTheRatesWasPickedUp.setEditable(false);
        dateTheRatesWasPickedUp.setText(dateFromStringFromWebpage);
        dateTheRatesWasPickedUp.setMaxWidth(80);

        //Format hashmap to ListView to display it on rates scene ---------------------------------------------------------------
        ObservableList<Object> ratesList = FXCollections.observableArrayList(hashmapToArrayListAndSorted(rates)); //Array list to observable list
        ListView<Object> listToDisplayInLayout = new ListView<>(ratesList); //Observable list to listview only to display it on rates scene.

        //GridPane --------------------------------------------------------------------------------------------------------------
        GridPane gridPane = new GridPane(); //Creates a grid pane
        gridPane.setPadding(new Insets(10,10,10,10)); //Setting the padding
        gridPane.setVgap(5);//Setting vertical gaps between columns.
        gridPane.setHgap(5);//Setting horizontal gaps between columns.
        gridPane.setAlignment(Pos.TOP_LEFT); //Setting grid alignment
        gridPane.setStyle(backgroundColorOfTheWindow); //Setting background color on the grid.

        //Arranging all the nodes in the grid
        gridPane.add(fromHowMuchToConvertTextField,0,0);
        gridPane.add(fromCurrencyComboBox,2,0);

        gridPane.add(toHowMuchToConvertTextField,0,1);
        gridPane.add(toCurrencyComboBox,2,1);

        gridPane.add(convertButton,0,5,1,5);
        gridPane.add(dateLabel,2,4);
        gridPane.add(dateTheRatesWasPickedUp,2,5);
        //---------------------------------------------------------------------------------------------------------------------------
        /*   Action Handling                  */
        menuMainStart.setOnAction(e -> {
            BorderPane root = new BorderPane();

            /* Adding the menus as well as the content pane to the root node. */
            root.setTop(menu);
            root.setCenter(gridPane);

            Scene mainScene = new Scene(root, 280,200);

            window.setScene(mainScene);//Sets that the window show the scene
        });

        menuMainExit.setOnAction(e -> System.exit(0));

        menuMainRates.setOnAction(e -> {
            // Rates scene when clicking button -------------------------------------------------------------------------------------
            window.setTitle("CC - Rates");
            VBox ratesLayout = new VBox();
            ratesLayout.getChildren().add(listToDisplayInLayout);

            BorderPane root = new BorderPane();
            root.setTop(menu);
            root.setCenter(ratesLayout);
            Scene sceneRates = new Scene(root, 280,200);
            window.setScene(sceneRates);
        });

        menuHelpAbout.setOnAction(e -> About.display(backgroundColorOfTheWindow));

        convertButton.setOnAction(e -> {
            if (checkWhatComesFromTextFieldToConvert(fromHowMuchToConvertTextField.getText())) {
                double valueToConvert = Double.parseDouble(fromHowMuchToConvertTextField.getText());
                toHowMuchToConvertTextField.setText(calculateConvertedValue(valueToConvert, fromCurrencyComboBox.getValue(), toCurrencyComboBox.getValue()));
            } else {
                AlertBox.display("Error input", "Illegal input, please choose something else", backgroundColorOfTheWindow);
            }
        });

        fromHowMuchToConvertTextField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                convertButton.arm();
                PauseTransition pause = new PauseTransition(Duration.seconds(0.1));
                pause.setOnFinished(evt -> {
                    convertButton.disarm();
                    convertButton.fire();
                });
                pause.play();
            }
        });
        //---------------------------------------------------------------------------------------------------------------------------
        /* Create a root node as BorderPane. */
        BorderPane root = new BorderPane();

        /* Adding the menus as well as the content pane to the root node. */
        root.setTop(menu);
        root.setCenter(gridPane);

        Scene mainScene = new Scene(root,280,200);

        window.setScene(mainScene);//Sets that the window show the scene
        window.show();
    }

    /**
     * Reads from a given webpage and returns a string with the content
     * @param homePageWeWannaReadFrom url for the webpage we want to read
     * @return returns a String that contains the content from Webpage.
     * @throws IOException throws IOException if the webpage couldn't be read.
     */
    public String readFromInternet(String homePageWeWannaReadFrom) throws IOException {
        String contentFromWebpage = "";
        URL urlForHomePageWeWannaRead = new URL(homePageWeWannaReadFrom);
        HttpURLConnection urlConnection = (HttpURLConnection) urlForHomePageWeWannaRead.openConnection(); //Creates connection to the URL.
        urlConnection.setRequestMethod("GET"); //Sets that we want to GET data from the site.
        InputStream in = urlForHomePageWeWannaRead.openStream(); //Creates InputStream from url
        Scanner sc = new Scanner(in); //Read the line.
        while(sc.hasNextLine()) {
            contentFromWebpage = sc.nextLine();
        }
        sc.close();
        return contentFromWebpage;
    }

    /**
     * Takes string from readFromInternet method to sort and adds the rates to a given Hashmap (rates)
     * @param input string to try and isolate rates
     */
    private void setRatesFromString(String input) {
        /* LETS CHOP THE STRING UP ----------------------------------------------------------------------------------------  */
        String[] ratesDoubles = new String[4];
        boolean run = false;
        String target= "";
        String temp = "";
        int k = 0;

        for (int i = 0; i<input.length(); i++) {
            temp += input.charAt(i);
            if (temp.contains("JPY") || temp.contains("SEK") || temp.contains("USD")) {
                i += 2;
                run = true;
                temp = "";
            } else if (temp.contains("date")) {
                i += 3;
                run = true;
                temp = "";
            } else if (input.charAt(i) == ',' || input.charAt(i) == '}' && input.charAt(i + 1) == ',') {
                if (!target.isEmpty() || !target.equals("")) {
                    ratesDoubles[k] = target;
                    temp = "";
                    target = "";
                    k++;
                    run = false;
                }
            } else if (input.charAt(i) == '"' && input.charAt(i+1) == '}')  {
                if (!target.isEmpty() || !target.equals("")) {
                    ratesDoubles[k] = target;
                    break;
                }
            } else if (run){
                target += input.charAt(i);
            }

        }
        rates.put("EURtoJPY", Double.parseDouble(ratesDoubles[0]));
        rates.put("EURtoSEK", Double.parseDouble(ratesDoubles[1]));
        rates.put("EURtoUSD", Double.parseDouble(ratesDoubles[2]));
        rates.put("EURtoEUR", 1.0);
        dateFromStringFromWebpage = ratesDoubles[3];
    }

    /**
     * Takes inputs and returns a converted value based on rates from hashmap.
     * @param valueToConvert input value how much to convert of fromCurrency -> toCurrency.
     * @param fromCurrency From currency as String ("SEK", "USD", "JPY", "EUR").
     * @param toCurrency To currency as String ("SEK", "USD", "JPY", "EUR").
     * @return converted value as String
     */
    String calculateConvertedValue(double valueToConvert, String fromCurrency, String toCurrency) {
        double a = 0;
        double b = 0;

        //Foreach loop that will get correct value depending on what we send into the method
        for (Map.Entry<String, Double> mapElement: rates.entrySet()) {
            String s = mapElement.getKey();
            if (s.substring(5).contains(fromCurrency)) {
                a = (mapElement.getValue());
            }
            if (s.substring(5).contains(toCurrency)) {
                b = (mapElement.getValue());
            }
        }

        if (fromCurrency.equals("SEK") && toCurrency.equals("SEK")) { //Some rounding problem when 1SEK -> 1 SEK... shows 0.9999... only on SEK...
            a = 1;
            b = 1;
        }
        double calculatedConvertedValue = (valueToConvert * ((1 / a) * b)); // The calculated value after conversion of currencies

        BigDecimal convertedAndRoundedValue = BigDecimal.valueOf(calculatedConvertedValue).setScale(2, RoundingMode.FLOOR); //Converts to BigDecimal just to get only 2 decimals (google uses it so why not us to?)
        return convertedAndRoundedValue.toString(); //Return the calculatedValue as a string
    }

    /**
     * Takes a hashmap and converts it to Arraylist that is sorted based on smallest to highest.
     * @param ratesToSort Hashmap to sort
     * @return arraylist that is sorted
     */
    public ArrayList<Object> hashmapToArrayListAndSorted(HashMap<String, Double> ratesToSort) {
        return ratesToSort.entrySet()
                            .stream()
                                .sorted(Map.Entry.comparingByValue()).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Takes input from textfield and checks what is given as input, returns true or false depending if legit input.
     * @param input input from textfield as String.
     * @return true/false based on input to method
     */
    public Boolean checkWhatComesFromTextFieldToConvert(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        } else {
            for(int i=0; i < input.length(); i++) {
                if(!Character.isDigit(input.charAt(i))) {
                    return false;
                }
            }
            return  (Double.parseDouble(input) >= 0) || (Double.parseDouble(input) <= Double.MAX_VALUE);
        }
    }

    /**
     * Returns a HashMap with rates for test purposes (Uses readfrominternet method and setRatesFromString method).
     * @return hashmap with rates
     */
    public HashMap<String, Double> readAndSetRatesForTest() throws IOException {
        String test = readFromInternet(webpageWeWannaReadFrom);
        setRatesFromString(test);
        return rates;
    }
}