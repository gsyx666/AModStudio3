import javax.swing.*;

public class Experiment {
    public static void main(String[] args) {
        String[] inputs = new String[]{"a","b","c","d","e","f","g","h"};
        String[] output = utils.showSelectionDialog(inputs);
        if(output==null){return;}
        for(String s: output){
            System.out.println(s);
        }
    }

}
