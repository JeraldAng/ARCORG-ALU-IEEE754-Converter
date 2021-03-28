import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ALUSimulationController extends Controller {


    public TextField txtFieldMan;
    public TextField txtFieldExp;
    public Label lblSignBit;
    public Label lblSignBit2;

    public Label lblExponent;
    public Label lblExponent2;

    public Label lblSignificand;
    public Label lblSignificand2;

    public Label lblHex;
    public Button calculateBtn;
    public Label finalAnswerLb;

    @Override
    protected void doneInit(HashMap<String, Object> data) {
        super.doneInit(data);

//        txtFieldMan.setText("11001.1111");
//        txtFieldExp.setText("12");

        txtFieldMan.setText("0");
        txtFieldExp.setText("0");
        getStage().setResizable(false);


        calculateBtn.setOnAction(event -> {
            if (txtFieldExp.getText().isEmpty() || txtFieldMan.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Detected");
                alert.setHeaderText("An error has been detected.");
                alert.setContentText("Please input both the mantissa and the exponent.");

                alert.showAndWait();
            }
            else
            convert();
        });
        convert();
    }

    private void convert() {

        String entry = txtFieldMan.getText();

        Pattern p = Pattern.compile("[2-9]");
        Matcher m = p.matcher(entry);
        if (m.find()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Detected");
            alert.setHeaderText("An error has been detected.");
            alert.setContentText("Please enter a valid input.");
            alert.show();
            return;
        }

        boolean isNegative = entry.contains("-");

        entry = entry.replace("-", "");

        String signBit = (isNegative ? "1" : "0");
        String exponentBin = "";
        String mantissa = ""; //significand
        StringBuilder hex = new StringBuilder();
        Integer exponentDec = 0;
        boolean NaN = false;

        String[] parts;

        Integer shifts;

        try {
            exponentDec = Integer.valueOf(txtFieldExp.getText()); //user entry for exp
        } catch (NumberFormatException e) { // if it has invalid character/s, parser will result into error
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Detected");
            alert.setHeaderText("An error has been detected.");
            alert.setContentText("Exponent cannot have a non integer input.");

            alert.showAndWait();
        }

        try {
            double d = Double.parseDouble(txtFieldMan.getText());
            if(d == 0.00){
                entry = "0";
            }
        } catch (NumberFormatException e) { // if it has invalid character/s, parser will result into error
            NaN = true;
            signBit = "x";
            exponentBin = "11111111111";
            mantissa = "0xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
        }

        Integer exponentBiased = 1023; //bias for exponent fixed at 1023 for 64 bit

        if (!NaN) {
            //STEP 1: NORMALIZE (2 cases; if shift left -> ADD; if shift right -> MINUS
            if (entry.charAt(0) == '0' && Double.parseDouble(entry) < 1) { //checks if first char is 0, move to the right until reach the first 1, also consider if it is < 1 (ex. 001.110 will not shift right)
                parts = entry.split("\\.");
                if(parts.length > 1) {
                    shifts = (parts[1].indexOf("1") + 1) * -1; //moving right-> - so times by negative 1
                }else{
                    shifts = 0;
                }
                exponentDec += shifts; //number of shifts you will do to normalize // (STEP 2: SIGN BIT)
            } else { //if first char is 1, get the index of the . to move to the left
                shifts = entry.indexOf(".") - entry.indexOf("1") - 1; //-1 kasi sobra index (start at 0 kasi), also consider if there are leading zeroes (ex. 0011.11)
                exponentDec += shifts; //number of shifts you will do to normalize // (STEP 2: SIGN BIT)
            }

            //STEP 3: Exponent Representation

            if(entry.equals("0") && txtFieldExp.getText().equals("0")){
                exponentBiased = 0;
            }


            // (exponent dec + exponent bias = exponent representation)
            System.out.println("Exponent in decimal: " + (exponentDec + exponentBiased));

            exponentBin = decToBinary(exponentDec + exponentBiased); //convert to binary

//            while(exponentBin.length() != 11)
//                exponentBin = "0" + exponentBin; // pad zeroes at the start

            //FIX 1: should be padded left if less than 1024
            if(exponentBiased+exponentDec < 1024){
                exponentBin = String.format("%011d", Integer.parseInt(exponentBin));
            }

            entry = entry.replace(".", "");

            //normalizes based on the shifts
            StringBuilder sb = new StringBuilder(entry);

            sb.insert(entry.indexOf("1") + 1, ".");

            if (entry.startsWith("0") && entry.length() > 1) // delete all leading zeroes (same for both positive and negative shifts)
                sb = sb.delete(entry.indexOf("0"), entry.indexOf("1"));

            System.out.println("Entry after shift: " + sb.toString());

            String normalized = sb.toString();

            parts = normalized.split("\\.");

            if(exponentDec+exponentBiased > -1) {
                if (parts.length > 1) {
                    mantissa = String.format("%-52s", parts[1]).replace(' ', '0'); //52 bits
                } else {
                    mantissa = String.format("%-52s", 0).replace(' ', '0'); //52 bits
                }
            }else{


                parts = txtFieldMan.getText().split("\\.");

                if(Double.parseDouble(parts[0]) != 0d) {
                    Integer zeroesToAdd = (exponentDec + exponentBiased) - 1;

                    entry = parts[0];

                    if(parts[1].contains("1")){
                        entry += parts[1];
                    }

                    entry = entry.replace(".", "");

                    entry = String.format("%" + (zeroesToAdd * -1) + "s", entry).replace(' ', '0');
                    sb = new StringBuilder(entry);
                    sb.insert(1, ".");
                    System.out.println(sb.toString());

                    parts = sb.toString().split("\\.");

                    mantissa = String.format("%-51s", parts[1]).replace(' ', '0');

                    mantissa = String.format("%52s", mantissa).replace(' ', '0');
                }else{
                    if (parts.length > 1) {
                        mantissa = String.format("%-52s", parts[1]).replace(' ', '0'); //52 bits
                    }else{
                        if(parts[0].equals("1")) {
                            mantissa = String.format("%52s", "1").replace(' ', '0');
                        }else{
                            mantissa =  String.format("%52s", "0").replace(' ', '0');
                        }
                    }
                }

                System.out.println(mantissa);
            }


//            if (exponentDec+exponentBiased < 0)
//            {
//                /*replace index of -(exponentDec+exponentBiased) from mantissa to "1"*/
//                sb = new StringBuilder(mantissa);
//                sb.deleteCharAt(mantissa.length()-1);
//                sb.append("1");
//                mantissa = sb.toString();
//            }
        }

//        if(exponentBin.equals("00000000000") && mantissa.contains("1")){
//            signBit = "0/1";
//            mantissa = "<>0";
//        }

        if(exponentDec+exponentBiased == 2047){
            mantissa = String.format("%-52s", "0").replace(' ', '0'); //52 bits
        }

        Integer n = 0;

        String IEEE = (signBit + exponentBin + mantissa);

        //Convert to Hex
        while (n <= IEEE.length() - 4) {
            String binPart = IEEE.substring(n, n + 4);
            hex.append(Integer.toString(binToDec(binPart), 16));
            n += 4;
        }

        if(exponentBiased+exponentDec > 2047 || NaN == true){
            exponentBin = decToBinary(2047);
            signBit = "x";
            if(NaN == true) {
                mantissa = "0";
            }else{
                mantissa = "1";
            }
            mantissa = String.format("%-52s", mantissa).replace(' ', 'x'); //52 bits
            hex.setLength(0);
        }

        lblSignBit.setText(signBit);
        //lblSignBit2.setText(signBit);

        lblExponent.setText(exponentBin);
        //lblExponent2.setText(exponentBin);

        lblSignificand.setText(mantissa); //set significand to label
        //lblSignificand2.setText(mantissa); //set significand to label

        finalAnswerLb.setText(signBit+"  "+exponentBin+"  "+mantissa);

        if(!hex.toString().isEmpty()) {
            lblHex.setText(hex.toString().toUpperCase() + "h");
        }else{
            lblHex.setText("");
        }
    }

    //CONVERT BINARY TO DECIMAL
    private Integer binToDec(String binary){

        Integer n = Double.valueOf(Math.pow(2,binary.length()-1)).intValue();

        Integer t = 0;


        for(int i = 0; i < binary.length(); i++){
            if(binary.charAt(i) == '1'){
                t += n;
            }
            n /= 2;
        }
        //System.out.println(binary+" : "+t);

        return t;
    }

    private String fracToBinary(Double num, Integer k){
//        System.out.println(num);
        Double t = num;
        StringBuilder bin = new StringBuilder();
        Integer c = 0;
        do {
            t = t * 2;
            String[] numberParts = t.toString().split("\\.");
            bin.append(numberParts[0]);
            t = Double.valueOf("." + numberParts[1]);
            c+=1;
        } while (c <= k);
        return bin.toString();
    }

    //CONVERT DECIMAL TO BINARY
    private String decToBinary(Integer num){

        Integer n = 1;

        ArrayList<String> bin = new ArrayList<>();

        while((n*2) <= num){
            n *= 2;
        }

        Integer t = 0;

        while(n > 0){
            if((n+t) > num) {
                bin.add("0");
            }else{
                bin.add("1");
                t += n;
            }
            n /= 2;
        }

        return String.join("", bin);
    }
}
