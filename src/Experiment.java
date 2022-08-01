import com.formdev.flatlaf.FlatDarkLaf;
import mbpcm.ui.IButton;

import javax.swing.*;
import java.awt.*;

public class Experiment {

    public static void main(String[] args) {
        FlatDarkLaf.setup();
        new Experiment();

    }
    Experiment(){
        String testImage = "start.png";
        JFrame f = getJFrame("Test");
        JPanel panel = getHPanel();
        panel.add(new IButton(utils.getImageFromRes(testImage),"Run"));
        panel.add(new IButton(null,"RUN"));
        panel.add(new IButton(utils.getImageFromRes(testImage)));
        f.add(panel,BorderLayout.NORTH);
        f.setVisible(true);
    }
    JFrame getJFrame(String name){
        JFrame f = new JFrame("hello");
        f.setSize(new Dimension(400,500));
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
        return f;
    }
    JPanel getHPanel(){
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));
            panel.setMaximumSize(new Dimension(Integer.MAX_VALUE,40));
            panel.setPreferredSize(new Dimension(Integer.MAX_VALUE,40));
            return panel;
    }
}