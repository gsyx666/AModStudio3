import mbpcm.ui.I_Window;
import mbpcm.ui.ManojUI;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class window_log extends OutputStream implements I_Window {
    public JTextArea textArea;
    public JScrollPane spLogWindow;
    public JToggleButton logWinToggle;
    public window_log() {
        textArea = new JTextArea();
        System.setOut (new PrintStream (this));
        textArea.setBackground(new Color(43,43,43));
        textArea.setEditable(false);
        textArea.setForeground(Color.LIGHT_GRAY);
        textArea.setFont(new Font("Fixedsys",Font.PLAIN,14));

        spLogWindow = new JScrollPane(textArea);
        spLogWindow.setBorder(BorderFactory.createEmptyBorder());

        logWinToggle = ManojUI.getVerticalButton("Logs",true);
        logWinToggle.setSelected(true);
    }

    @Override
    public void write(byte[] buffer, int offset, int length) throws IOException {
        final String text = new String (buffer, offset, length);
        SwingUtilities.invokeLater(() -> textArea.append (text));
    }

    @Override
    public void write(int b) throws IOException {
        write (new byte [] {(byte)b}, 0, 1);
    }

    @Override
    public JComponent getWindow() {
        return spLogWindow;
    }

    @Override
    public JToggleButton getButton() {
        return logWinToggle;
    }

    @Override
    public String getWindowName() {
        return "log";
    }

    @Override
    public int getPrefPosition() {
        return WindowManager.BOTTOM;
    }

    @Override
    public void onSettingChanged(String a, String b, Object c) {

    }
}