package CurrencyConverterJavaFX;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;

public class CurrencyConverterJavaFXTest {
    CurrencyConverterJavaFX currencyConverterJavaFX = new CurrencyConverterJavaFX();

    @Test
    @DisplayName("Testing that the input from text field to conversion is legal actions")
    void TestInputsFromTextFieldToConvert() {
        Assertions.assertFalse(currencyConverterJavaFX.checkWhatComesFromTextFieldToConvert("kalle"));
        Assertions.assertFalse(currencyConverterJavaFX.checkWhatComesFromTextFieldToConvert(""+Double.MIN_VALUE));
        Assertions.assertFalse(currencyConverterJavaFX.checkWhatComesFromTextFieldToConvert(""));
        Assertions.assertFalse(currencyConverterJavaFX.checkWhatComesFromTextFieldToConvert(null));
        Assertions.assertTrue(currencyConverterJavaFX.checkWhatComesFromTextFieldToConvert("100"));
        Assertions.assertTrue(currencyConverterJavaFX.checkWhatComesFromTextFieldToConvert("0"));
        Assertions.assertFalse(currencyConverterJavaFX.checkWhatComesFromTextFieldToConvert(""+Double.MAX_VALUE));
    }

    @Test
    @DisplayName("Testing that the readFromInternet method doesnt throw any exceptions")
    void TestReadFromInternetMethod() {
        String homePageWeWannaReadFrom = "https://api.exchangeratesapi.io/latest?symbols=JPY,USD,SEK";
        Assertions.assertDoesNotThrow(() -> currencyConverterJavaFX.readFromInternet(homePageWeWannaReadFrom));
    }

    @Test
    @DisplayName("Test that the conversion is right")
    void TestMathForConversion() {
        TestReadFromInternetMethod(); //Read from internet to get the rates.
        HashMap<String, Double> rates = currencyConverterJavaFX.getRatesHashMap(); //Take in the hashmap with rates

        double EURtoJPY = rates.get("EURtoJPY"); //Takes from hashmap and puts into doubles
        double EURtoSEK = rates.get("EURtoSEK");
        double EURtoUSD = rates.get("EURtoUSD");
        double EURtoEUR = rates.get("EURtoEUR");
        double valueToConvert = 100;

        //Test 1 (100eur to sek)
        BigDecimal targetValue1 = BigDecimal.valueOf(valueToConvert * (1/EURtoEUR) * EURtoSEK).setScale(2, RoundingMode.FLOOR);
        Assertions.assertEquals(targetValue1.toString(), currencyConverterJavaFX.getRatesAndCalculate(valueToConvert, "EUR", "SEK"));

        //Test 2 (100sek to eur)
        BigDecimal targetValue2 = BigDecimal.valueOf(valueToConvert * (1/EURtoSEK) * EURtoEUR).setScale(2, RoundingMode.FLOOR);
        Assertions.assertEquals(targetValue2.toString(), currencyConverterJavaFX.getRatesAndCalculate(valueToConvert, "SEK", "EUR"));

        //Test 3 (100usd to jpy)
        BigDecimal targetValue3 = BigDecimal.valueOf(valueToConvert * (1/EURtoUSD) * EURtoJPY).setScale(2, RoundingMode.FLOOR);
        Assertions.assertEquals(targetValue3.toString(), currencyConverterJavaFX.getRatesAndCalculate(valueToConvert, "USD", "JPY"));

        //Test 4 special case for SEK to SEK due to rounding problem...
        Assertions.assertEquals("1.00", currencyConverterJavaFX.getRatesAndCalculate(1, "SEK", "SEK"));
    }

    @Test
    @DisplayName("Test that the method hashmapToListAndSorted")
    void TestHashmapToListAndSorted() {
        //Create a hashmap
        HashMap<String, Double> animals = new HashMap<>();
        animals.put("Elephants", 10.0);
        animals.put("Cats", 2.0);
        animals.put("Dogs", 3.0);
        animals.put("Seahorses", 44.0);
        animals.put("Dinosaurs", 0.0);
        ArrayList<Object> animalsHashMaptoArraylist = currencyConverterJavaFX.hashmapToArrayListAndSorted(animals);

        ArrayList<Object> animalsInArrayList = new ArrayList<>();
        animalsInArrayList.add("Dinosaurs=0.0");
        animalsInArrayList.add("Cats=2.0");
        animalsInArrayList.add("Dogs=3.0");
        animalsInArrayList.add("Elephants=10.0");
        animalsInArrayList.add("Seahorses=44.0");

        Assertions.assertEquals(animalsInArrayList.toString(), animalsHashMaptoArraylist.toString());
    }
}
