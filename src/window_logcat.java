import mbpcm.customViews.ModernScrollPane;
import mbpcm.ui.I_Window;
import mbpcm.ui.ManojUI;
import mbpcm.ui.uiUtils;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.ChangeableHighlightPainter;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.awt.SystemColor.text;
import static javax.swing.Box.createHorizontalStrut;

public class window_logcat extends Thread implements I_Window {
    JTextArea textPane = new JTextArea();
    ManojUI ui;
    Highlighter h = textPane.getHighlighter();
    String regex = "(.{19})\\s+(\\d*)\\s+(\\d*)\\s+(I|W|D|E|V)\\s+(.*?):(.*)";
    Pattern pattern = Pattern.compile(regex);
    HashMap<String,String> procs = new HashMap<String,String>();
    Highlighter.HighlightPainter Epainter = new DefaultHighlighter.DefaultHighlightPainter(new Color(0x6C0101));
    Highlighter.HighlightPainter Wpainter = new DefaultHighlighter.DefaultHighlightPainter(new Color(0x9A0135));
    Highlighter.HighlightPainter Ipainter = new DefaultHighlighter.DefaultHighlightPainter(new Color(0x017A01));
    Highlighter.HighlightPainter Dpainter = new DefaultHighlighter.DefaultHighlightPainter(new Color(0x000000));
    Highlighter.HighlightPainter Vpainter = new DefaultHighlighter.DefaultHighlightPainter(new Color(0x021748));
    //StyledDocument doc = textPane.getStyledDocument();
    int len = 0;
    //Style style;

    ModernScrollPane logcatScrollPane;
    JToggleButton toggleLogcat = ManojUI.getVerticalButton("Logcat",true);
    String command = "D:\\Program Files\\Nox\\bin\\adb.exe";
    String cmdlogcat = "logcat";
    String startDir = "";
    DefaultCaret caret;

    JPanel mainPanel = new JPanel();
    JPanel optionBar = new JPanel();
    boolean keepRunning = true;
    void parseString(String s){
        textPane.append(s + "\n");
        Matcher matcher = pattern.matcher(s);
        if (matcher.find()) {
            String errotype = matcher.group(4);
            String activity = matcher.group(5);
            if(Objects.equals(activity, "ActivityManager")){
                String data = matcher.group(6).trim();
                if(data.startsWith("Start proc")){
                    System.out.println(s);
                }
            }
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
    window_logcat(ManojUI _ui){
        ui = _ui;
        int Max_H = 20;
        mainPanel.setLayout(new BorderLayout());
        textPane.setFont(new Font("Fixedsys",Font.PLAIN,10));
        textPane.setBackground(new Color(43,43,43));
        textPane.setForeground(Color.LIGHT_GRAY);
        textPane.setEditable(false);

        logcatScrollPane = new ModernScrollPane(textPane);
        new SmartScroller(logcatScrollPane);

        mainPanel.add(logcatScrollPane,BorderLayout.CENTER);
        mainPanel.add(optionBar,BorderLayout.NORTH);

        optionBar.setLayout(new BorderLayout());
        optionBar.setBackground(new Color(0xBABAEF));

        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons,BoxLayout.X_AXIS));
        JButton jButtonstart = uiUtils.getJButton("start");
        JButton jButtonstop = uiUtils.getJButton("stop");
        JButton jButtonsave = uiUtils.getJButton("save");
        jButtonsave.addActionListener(ae->{
                utils.file_put_contents("E:\\locatlog.txt",textPane.getText());
                utils.MessageBox("Written logcat successfully");
        });
        buttons.add(jButtonstart);
        buttons.add(jButtonstop);
        buttons.add(jButtonsave);
        buttons.add(createHorizontalStrut(10));


        JPanel selectors = new JPanel();
        selectors.setLayout(new BoxLayout(selectors,BoxLayout.X_AXIS));
        JComboBox<String> jComboBoxApp = new JComboBox<>();
        JComboBox<String> jComboBoxLogType = new JComboBox<>();
        jComboBoxApp.setBorder(BorderFactory.createEmptyBorder());
        jComboBoxLogType.setBorder(BorderFactory.createEmptyBorder());
        jComboBoxApp.setMinimumSize(new Dimension(100,Max_H));
        jComboBoxLogType.setMinimumSize(new Dimension(100,Max_H));
        jComboBoxApp.setMaximumSize(new Dimension(200,Max_H));
        jComboBoxLogType.setMaximumSize(new Dimension(200,Max_H));
        selectors.add(jComboBoxApp);
        selectors.add(createHorizontalStrut(10));
        selectors.add(jComboBoxLogType);

        JPanel search = new JPanel();
        search.setLayout(new BoxLayout(search,BoxLayout.X_AXIS));
        JTextField searchbox = new JTextField();
        searchbox.setBorder(BorderFactory.createEmptyBorder());
        searchbox.setMaximumSize(new Dimension(200,Max_H));
        searchbox.setMinimumSize(new Dimension(100,Max_H));
        searchbox.setPreferredSize(new Dimension(200,Max_H));
        searchbox.setAlignmentX(Component.RIGHT_ALIGNMENT);
        search.add(searchbox);

            search.add(new JLabel("Test Label"));
        optionBar.add(buttons,BorderLayout.WEST);
        optionBar.add(selectors,BorderLayout.CENTER);
        optionBar.add(search,BorderLayout.EAST);

        logcatScrollPane.setBorder(BorderFactory.createEmptyBorder());
        toggleLogcat.setSelected(true);
        toggleLogcat.addActionListener(ae->{
            if(!toggleLogcat.isSelected()){
                toggleLogcat.putClientProperty("dPos",ui.getBottomPane().getDividerLocation());
                //ui.getBottomPane().setDividerSize(0);
            }else{
                SwingUtilities.invokeLater(() -> {
                    ui.getBottomPane().setDividerLocation((int)toggleLogcat.getClientProperty("dPos"));
                    //ui.getBottomPane().setDividerSize(3);
                    ui.f.setVisible(true);
                });
            }
            mainPanel.setVisible(toggleLogcat.isSelected());
        });
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                startThread();
            }
        });
    }
    public void startThread(){
        this.start();
    }
    public void run() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command,cmdlogcat);
            if(!this.startDir.equals("")) {
                processBuilder.directory(new File(this.startDir));
            }
            final Process proc = processBuilder.start();
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            String s;
            while ((s = stdInput.readLine()) != null && keepRunning) {
                //addColoredText(s + "\n",Color.BLUE);
                parseString(s);
            }
            //while ((s = stdError.readLine()) != null && keepRunning) {
                //addColoredText(s + "\n",Color.RED);
                //textPane.append(s+"\n");
            //}
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    void addColoredText(String text,Color color){
        //StyleConstants.setForeground(style, color);
            textPane.setForeground(color);
            //textPane.append(text);
            len += text.length();
    }
    @Override
    public JComponent getWindow() {
        return mainPanel;
    }

    @Override
    public JToggleButton getButton() {
        return toggleLogcat;
    }
    public void stopLogcat(){
        keepRunning = false;
    }
    public void startWithCommand(String command_,String startDir_){
        command = command_;
        startDir = startDir_;
        this.start();
    }


    /**
     *  The SmartScroller will attempt to keep the viewport positioned based on
     *  the users interaction with the scrollbar. The normal behaviour is to keep
     *  the viewport positioned to see new data as it is dynamically added.
     *
     *  Assuming vertical scrolling and data is added to the bottom:
     *
     *  - when the viewport is at the bottom and new data is added,
     *    then automatically scroll the viewport to the bottom
     *  - when the viewport is not at the bottom and new data is added,
     *    then do nothing with the viewport
     *
     *  Assuming vertical scrolling and data is added to the top:
     *
     *  - when the viewport is at the top and new data is added,
     *    then do nothing with the viewport
     *  - when the viewport is not at the top and new data is added, then adjust
     *    the viewport to the relative position it was at before the data was added
     *
     *  Similiar logic would apply for horizontal scrolling.
     */
    public class SmartScroller implements AdjustmentListener
    {
        public final static int HORIZONTAL = 0;
        public final static int VERTICAL = 1;

        public final static int START = 0;
        public final static int END = 1;

        private int viewportPosition;

        private JScrollBar scrollBar;
        private boolean adjustScrollBar = true;

        private int previousValue = -1;
        private int previousMaximum = -1;

        /**
         *  Convenience constructor.
         *  Scroll direction is VERTICAL and viewport position is at the END.
         *
         *  @param scrollPane the scroll pane to monitor
         */
        public SmartScroller(JScrollPane scrollPane)
        {
            this(scrollPane, VERTICAL, END);
        }

        /**
         *  Convenience constructor.
         *  Scroll direction is VERTICAL.
         *
         *  @param scrollPane the scroll pane to monitor
         *  @param viewportPosition valid values are START and END
         */
        public SmartScroller(JScrollPane scrollPane, int viewportPosition)
        {
            this(scrollPane, VERTICAL, viewportPosition);
        }

        /**
         *  Specify how the SmartScroller will function.
         *
         *  @param scrollPane the scroll pane to monitor
         *  @param scrollDirection indicates which JScrollBar to monitor.
         *                         Valid values are HORIZONTAL and VERTICAL.
         *  @param viewportPosition indicates where the viewport will normally be
         *                          positioned as data is added.
         *                          Valid values are START and END
         */
        public SmartScroller(JScrollPane scrollPane, int scrollDirection, int viewportPosition)
        {
            if (scrollDirection != HORIZONTAL
                    &&  scrollDirection != VERTICAL)
                throw new IllegalArgumentException("invalid scroll direction specified");

            if (viewportPosition != START
                    &&  viewportPosition != END)
                throw new IllegalArgumentException("invalid viewport position specified");

            this.viewportPosition = viewportPosition;

            if (scrollDirection == HORIZONTAL)
                scrollBar = scrollPane.getHorizontalScrollBar();
            else
                scrollBar = scrollPane.getVerticalScrollBar();

            scrollBar.addAdjustmentListener( this );

            //  Turn off automatic scrolling for text components

            Component view = scrollPane.getViewport().getView();

            if (view instanceof JTextComponent)
            {
                JTextComponent textComponent = (JTextComponent)view;
                DefaultCaret caret = (DefaultCaret)textComponent.getCaret();
                caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
            }
        }

        @Override
        public void adjustmentValueChanged(final AdjustmentEvent e)
        {
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    checkScrollBar(e);
                }
            });
        }

        /*
         *  Analyze every adjustment event to determine when the viewport
         *  needs to be repositioned.
         */
        private void checkScrollBar(AdjustmentEvent e)
        {
            //  The scroll bar listModel contains information needed to determine
            //  whether the viewport should be repositioned or not.

            JScrollBar scrollBar = (JScrollBar)e.getSource();
            BoundedRangeModel listModel = scrollBar.getModel();
            int value = listModel.getValue();
            int extent = listModel.getExtent();
            int maximum = listModel.getMaximum();

            boolean valueChanged = previousValue != value;
            boolean maximumChanged = previousMaximum != maximum;

            //  Check if the user has manually repositioned the scrollbar

            if (valueChanged && !maximumChanged)
            {
                if (viewportPosition == START)
                    adjustScrollBar = value != 0;
                else
                    adjustScrollBar = value + extent >= maximum;
            }

            //  Reset the "value" so we can reposition the viewport and
            //  distinguish between a user scroll and a program scroll.
            //  (ie. valueChanged will be false on a program scroll)

            if (adjustScrollBar && viewportPosition == END)
            {
                //  Scroll the viewport to the end.
                scrollBar.removeAdjustmentListener( this );
                value = maximum - extent;
                scrollBar.setValue( value );
                scrollBar.addAdjustmentListener( this );
            }

            if (adjustScrollBar && viewportPosition == START)
            {
                //  Keep the viewport at the same relative viewportPosition
                scrollBar.removeAdjustmentListener( this );
                value = value + maximum - previousMaximum;
                scrollBar.setValue( value );
                scrollBar.addAdjustmentListener( this );
            }

            previousValue = value;
            previousMaximum = maximum;
        }
    }
}
