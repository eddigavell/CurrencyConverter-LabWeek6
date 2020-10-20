package CurrencyConverterJava;

import javax.swing.*;
import java.io.*;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.math.BigDecimal;

public class CurrencyConverterGUI extends JFrame{
    private JPanel mainPanel;
    private JTextField fromTextField;
    private JComboBox fromCurrencyComboBox;
    private JComboBox toCurrencyComboBox;
    private JButton convertButton;
    private JTextPane toCurrencyTextPane;
    private JTextField dateWhenGotRates;
    HashMap<String, Double> rates = new HashMap<>();

    public CurrencyConverterGUI(String title) throws IOException {
        super(title);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);
        this.pack();

        mainPanel.getRootPane().setDefaultButton(convertButton); //Sets that enter is the deafult button and "clicks" convertButton.

        readFromInternetAndSetRates(); //initiate and sets Exhangerates

        //Code below what happens if convertbutton is used.
        convertButton.addActionListener(e -> {
            //Kod som körs om man trycker på convertknappen.
            /* Combo box index
            index 0 = SEK
            index 1 = EUR
            index 2 = USD
            index 3 = JPY
            */
            String fromCurrency = "";
            String toCurrency = "";
            switch (fromCurrencyComboBox.getSelectedIndex()) {
                case 0 -> fromCurrency = "SEK";
                case 1 -> fromCurrency = "EUR";
                case 2 -> fromCurrency = "USD";
                case 3 -> fromCurrency = "JPY";
            }
            switch (toCurrencyComboBox.getSelectedIndex()) {
                case 0 -> toCurrency = "SEK";
                case 1 -> toCurrency = "EUR";
                case 2 -> toCurrency = "USD";
                case 3 -> toCurrency = "JPY";
            }


            if (checkWhatComesFromTextFieldToConvert(fromTextField.getText())) {
                printConvertedValueToTextField(fromCurrency, toCurrency);
            } else {
                JOptionPane.showMessageDialog(null, "Illegal input. Allowed input is only positive numbers");
            }

        });
    }

    Boolean checkWhatComesFromTextFieldToConvert(String x) {

        if (!x.isEmpty()) {
            for(int i=0; i < x.length(); i++) {
                if(!Character.isDigit(x.charAt(i))) {
                    return false;
                }
            }

            return  (Double.parseDouble(x) >= 0) || (Double.parseDouble(x) <= Double.MAX_VALUE);
        } else {
            return false;
        }
    }

    void printConvertedValueToTextField(String x, String y) {
        double a = 0;
        double b = 0;
        double c = Double.parseDouble(fromTextField.getText());

        //Foreach loop that will get correct value depending on what we send into the method
        for (Map.Entry mapElement: rates.entrySet()) {
            String s = mapElement.getKey().toString();
            if (s.substring(5).contains(x)) {
                a = (double) (mapElement.getValue());
            }
            if (s.substring(5).contains(y)){
                b = (double) (mapElement.getValue());
            }
        }

        double calculatedConvertedValue = (c * ((1 / a) * b)); // The calculated value after conversion of currencies
        BigDecimal toCurrencyInTextField = BigDecimal.valueOf(calculatedConvertedValue).setScale(2, RoundingMode.FLOOR); //Converts to BigDecimal just to get only 2 decimals (google uses it so why not us to?)
        toCurrencyTextPane.setText(toCurrencyInTextField.toString()); //Writes text to TextPane to show
    }

    void readFromInternetAndSetRates() throws IOException {
        /*Read from internet-----------------------------------------------------------------------------------------------------------------------*/
        String s = "";
        URL urlForHomepageWeWannaRead = new URL("https://api.exchangeratesapi.io/latest?symbols=JPY,USD,SEK"); //Sets the url we want to read from
        HttpURLConnection urlConnection = (HttpURLConnection) urlForHomepageWeWannaRead.openConnection(); //Creates connection to the URL.
        urlConnection.setRequestMethod("GET"); //Sets that we want to GET data from the site.
        InputStream in = urlForHomepageWeWannaRead.openStream(); //Creates InputStream from url
        Scanner sc = new Scanner(in); //Read the line.
        while(sc.hasNextLine()) {
            s = sc.nextLine();
        }
        sc.close();

        /*Sets Rates-------------------------------------------------------------------------------------------------------------------------------*/
        double rateEURtoJPY = Double.parseDouble(s.substring(s.indexOf("JPY")+5, s.indexOf(","))); //Trims the string to double.
        double rateEURtoUSD = Double.parseDouble(s.substring(s.indexOf("USD")+5, s.indexOf("USD")+5+6)); //Trims the string to double.
        double rateEURtoSEK = Double.parseDouble(s.substring(s.indexOf("SEK")+5, s.indexOf("SEK")+5+6)); //Trims the string to double.

        rates.put("EURtoJPY", rateEURtoJPY); //Sets EURtoJPY to hashmap
        rates.put("EURtoUSD", rateEURtoUSD); //Sets eur->usd to hashmap
        rates.put("EURtoSEK", rateEURtoSEK); //Sets eur->sek to hashmap
        rates.put("EURtoEUR", 1.0); //Sets eur->eur to hashmap

        dateWhenGotRates.setText(s.substring(s.indexOf("date")+7, s.lastIndexOf("}")-1)); //Adds date from the url feed to the text field.
        dateWhenGotRates.setHorizontalAlignment(JTextField.CENTER); //Sets text to be centered inside the text field.
    }

    public static void main(String[] args) throws IOException {
        JFrame frame = new CurrencyConverterGUI("Currency Converter"); //Title for program window
        frame.setVisible(true); //Sets program window to show
    }
}