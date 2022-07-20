import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Experiment {

    public static void main(String[] args) {
        new Experiment();

    }
    Experiment(){
      JFrame f = new JFrame("hello");
      f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      JButton jButton = new JButton("helllo");
      jButton.addActionListener(e -> System.out.println("action 1"));
        jButton.addActionListener(e -> System.out.println("action 2"));
        f.add(jButton);
      f.pack();
      f.setVisible(true);
    }


}