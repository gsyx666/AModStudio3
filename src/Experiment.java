import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Experiment {

    StyledDocument doc;
    int len = 0;
    Style style;
    JTextArea textPane = new JTextArea();

    Highlighter h = textPane.getHighlighter();
    String regex = "(.{19})\\s+(\\d*)\\s+(\\d*)\\s+(I|W|D|E|V)\\s+(.*?):(.*)";
    Pattern pattern = Pattern.compile(regex);
    Highlighter.HighlightPainter Epainter = new DefaultHighlighter.DefaultHighlightPainter(new Color(0xA10202));
    Highlighter.HighlightPainter Wpainter = new DefaultHighlighter.DefaultHighlightPainter(new Color(0xA47D02));
    Highlighter.HighlightPainter Ipainter = new DefaultHighlighter.DefaultHighlightPainter(new Color(0x017A01));
    Highlighter.HighlightPainter Dpainter = new DefaultHighlighter.DefaultHighlightPainter(new Color(0x000000));
    Highlighter.HighlightPainter Vpainter = new DefaultHighlighter.DefaultHighlightPainter(new Color(0x6E6D6D));
    public static void main(String[] args) {
        new Experiment();

    }
    Experiment(){

        textPane.setForeground(Color.WHITE);
        JFrame frame = new JFrame("Test");
        frame.getContentPane().add(textPane);
        //frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setSize(new Dimension(500,500));
        frame.setVisible(true);
        parseString("07-18 15:58:14.148  2466  2707 W MobStoreFlagStore: \tat bwpb.a(:com.google.android.gms@222413022@22.24.13 (040700-455379205):0)");
        parseString("07-18 15:58:14.075  3850  3855 I art     : Increasing code cache capacity to 128KB");
        parseString("07-18 16:00:10.971  2126  2175 E WifiMode: WiredSSID, Invalid SupportedRates!!!");
        parseString("07-18 16:00:10.971  2126  2175 V WifiMode: WiredSSID, Invalid SupportedRates!!!");
        parseString("07-18 16:00:10.971  2126  2175 D WifiMode: WiredSSID, Invalid SupportedRates!!!");
        parseString("07-18 16:00:10.971  2126  2175 E WifiMode: WiredSSID, Invalid SupportedRates!!!");
    }
    void parseString(String s){
        textPane.append(s + "\n");
        Matcher matcher = pattern.matcher(s);
        if (matcher.find()) {
            String errotype = matcher.group(4);
              try {
                  switch (errotype){
                      case "E" -> h.addHighlight(len, len + s.length(), Epainter);
                      case "I" -> h.addHighlight(len, len + s.length(), Ipainter);
                      case "V" -> h.addHighlight(len, len + s.length(), Vpainter);
                      case "W" -> h.addHighlight(len, len + s.length(), Wpainter);
                      case "D" -> h.addHighlight(len, len + s.length(), Dpainter);
                  }
                    //h.addHighlight(len + matcher.start(i), len + matcher.end(i), cyanPainter);
                } catch (BadLocationException e) {
                    throw new RuntimeException(e);
                }
        }
        len += s.length() + 1;
    }

}