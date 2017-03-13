package TicTacMath;

import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;


import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;

public class Controller implements Initializable {

    //turn == 1 for player, turn == 0 for CPU.
    private int turn = 1;
    private Integer[] numList = {1,2,3,4,5,6,7,8,9};
    private ArrayList<Integer> availableNumbers = new ArrayList<Integer>(Arrays.asList(numList));

    @FXML private TextField textField11;
    @FXML private TextField textField12;
    @FXML private TextField textField13;

    @FXML private TextField textField21;
    @FXML private TextField textField22;
    @FXML private TextField textField23;

    @FXML private TextField textField31;
    @FXML private TextField textField32;
    @FXML private TextField textField33;

    private ArrayList<TextField> gameGrid;

    @FXML
    private TextField availableNumbersTF;

    @FXML
    private TextField titleTF;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Initializing?");

        TextField[] textFields = {textField11, textField12, textField13, textField21, textField22, textField23, textField31, textField32, textField33};
        gameGrid = new ArrayList<TextField>(Arrays.asList(textFields));

        for (TextField tf : gameGrid){
            tf.textProperty().addListener((observable, oldValue, newValue) -> onChangeHandler(observable, oldValue, newValue));
        }

    }

    private void onChangeHandler(Observable observable, String oldValue, String newValue){
        if (turn != 0) {
            Integer intVal = -1;
            try {
                intVal = Integer.parseInt(newValue);
            } catch (NumberFormatException e) {
                //do nothing
            }

            if (availableNumbers.indexOf(intVal) != -1) { //Only proceed if the number is in the list of available
                TextField editedTextField = getTextFieldByValue(newValue);
                //newValue.matches("[1-9]")

                if (!newValue.matches("[1-9]")) {
                    editedTextField.setText("-");
                    return;
                }

                editedTextField.setStyle("-fx-text-fill: red;");

                System.out.println("You entered: " + newValue);
                //remove that number from the available numbers:
                removeNumber(Integer.parseInt(newValue));

                editedTextField.setEditable(false);

                if (checkForWin()) {
                    System.out.println("Somebody won congrats");

                    doWinStuff();
                    return;
                }

                if (availableNumbers.size() == 0) {
                    doTieStuff();
                    return;
                }

                cpuTurn();

            } else {
                TextField enteredTextField = getTextFieldByFocus();
                enteredTextField.setText("-");
            }
        }
    }

    private void cpuTurn(){
        turn = 0; //set to the CPU's turn

        int randomIndex;

        //Pick a number to play:
        randomIndex = ThreadLocalRandom.current().nextInt(0, availableNumbers.size());

        Integer randomNumber = availableNumbers.get(randomIndex);
        removeNumber(randomNumber);

        //Pick a random square to put the number in:

        TextField randomSquare = getRandomSquare();

        randomSquare.setStyle("-fx-text-fill:black;");
        randomSquare.setText(randomNumber.toString());

        if (checkForWin()) {
            doWinStuff();
            return;
        }

        if (availableNumbers.size() == 0) {
            doTieStuff();
        }

        turn = 1; //set to the players turn.
    }

    private void doTieStuff(){

        availableNumbersTF.setText("Tied Game!");

        //set all text fields to editable false
        for (TextField tf : gameGrid){
            tf.setEditable(false);
        }

        titleTF.setText("GAME OVER");
    }

    private TextField getRandomSquare(){

        int randomIndex = ThreadLocalRandom.current().nextInt(1, 9);

        TextField tf = gameGrid.get(randomIndex);

        while(!tf.getText().equals("-")){
            randomIndex = ThreadLocalRandom.current().nextInt(1, 9);
            tf = gameGrid.get(randomIndex);
        }

        return tf;

    }

    private void doWinStuff(){

        //If the turn is 1, the player won, if the turn is 0, the CPU won:

        if (turn == 0){
            availableNumbersTF.setText("You lose.");
        } else {
            availableNumbersTF.setText("You win!");
        }

        //set all text fields to editable false
        for (TextField tf : gameGrid){
            tf.setEditable(false);
        }

        titleTF.setText("GAME OVER");

    }

    private TextField getTextFieldByFocus(){
        for (TextField tf : gameGrid){
            if (tf.isFocused()){
                return tf;
            }
        } return null;
    }

    private TextField getTextFieldByValue(String value){

        for (TextField tf : gameGrid){
            if (tf.getText().equals(value)){
                return tf;
            }
        }

        return null;

    }

    private void removeNumber(Integer numToRemove){
        //First remove the number required:
        availableNumbers.remove(availableNumbers.indexOf(numToRemove));

        //Update the text on the game screen to not include that number
        availableNumbersTF.setText(availableNumbers.toString().substring(1, availableNumbers.toString().length()-1));

    }

    private boolean checkForWin(){
        //Add up the rows first:
        //[0, 1, 2
        //[3, 4, 5
        //[6, 7, 8]

        //Check the rows:
        for (int i = 0; i < 8; i+= 3){
            int rowTotal = 0;
            for (int j = 0; j<3;j++){
                int intVal = 0;
                try {
                    intVal = Integer.parseInt(gameGrid.get(i+j).getText());
                } catch (NumberFormatException e){
                    rowTotal = 0;
                    break;
                }

                rowTotal += intVal;
            }
            if (rowTotal == 15) {
                return true;
            }
        }

        //Check the columns:
        for (int i = 0; i < 3; i+= 1){
            try {
                int intVal1 = Integer.parseInt(gameGrid.get(i).getText());
                int intVal2 = Integer.parseInt(gameGrid.get(i+3).getText());
                int intVal3 = Integer.parseInt(gameGrid.get(i+6).getText());

                if ((intVal1 + intVal2 + intVal3) == 15) {
                    return true;
                }
            } catch (NumberFormatException e){
                continue;
            }
        }

        //Check the diagonals:
        try {
            int val1 = Integer.parseInt(gameGrid.get(0).getText());
            int val2 = Integer.parseInt(gameGrid.get(4).getText());
            int val3 = Integer.parseInt(gameGrid.get(8).getText());

            if ((val1+val2+val3) == 15){return true;}
        } catch (NumberFormatException e){
            //do nothing
        }

        try {
            int val1 = Integer.parseInt(gameGrid.get(2).getText());
            int val2 = Integer.parseInt(gameGrid.get(4).getText());
            int val3 = Integer.parseInt(gameGrid.get(6).getText());

            if ((val1+val2+val3) == 15){return true;}
        } catch (NumberFormatException e){
            //do nothing
        }

        return false;
    }

}
