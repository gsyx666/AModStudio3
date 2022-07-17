import mbpcm.ui.I_Bar;

import javax.swing.*;
import java.awt.*;

public class Bar_Status implements I_Bar {
    JProgressBar progressBar;
    JLabel statusLabel;
    JPanel statusPanel = new JPanel();
    Bar_Status(){
        progressBar = new JProgressBar();
        statusLabel = new JLabel();
        progressBar.setPreferredSize(new Dimension(150,4));
        progressBar.setMaximumSize(new Dimension(150,4));
        progressBar.setVisible(false);
        statusLabel.setText("Everything is Ready !");
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusPanel.add(progressBar);
        statusPanel.add(statusLabel);
    }
    @Override
    public JComponent getView() {
        return statusPanel;
    }
}
