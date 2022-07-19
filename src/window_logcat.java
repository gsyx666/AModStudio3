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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.awt.SystemColor.text;
import static javax.swing.Box.createHorizontalStrut;

public class window_logcat extends Thread implements I_Window {
    Highlighter.HighlightPainter Epainter = new DefaultHighlighter.DefaultHighlightPainter(new Color(0xA60042));
    Highlighter.HighlightPainter Wpainter = new DefaultHighlighter.DefaultHighlightPainter(new Color(0x750098));
    Highlighter.HighlightPainter Ipainter = new DefaultHighlighter.DefaultHighlightPainter(new Color(0x017A01));
    Highlighter.HighlightPainter Dpainter = new DefaultHighlighter.DefaultHighlightPainter(new Color(0x000000));
    Highlighter.HighlightPainter Vpainter = new DefaultHighlighter.DefaultHighlightPainter(new Color(0x021748));

    final Pattern pattern = Pattern.compile("(.{18})\\s+(\\d*)\\s+(\\d*)\\s+([IWDEV])\\s+(.*?):(.*)");
    final Pattern pProcStart = Pattern.compile("Start proc\\s+(\\d+):(.*?)/");
    final Pattern pProcKill = Pattern.compile("Killing\\s+(\\d+):");
    final String command = "D:\\Program Files\\Nox\\bin\\adb.exe";
    final String cmdlogcat = "logcat";
    final String startDir = "";
    final String latest = "\n\n--------------------------- | L a t e s t | -----------------------------\n\n";
    ManojUI ui;
    Process proc;
    JTextArea textPane = new JTextArea();
    Highlighter h = textPane.getHighlighter();
    JComboBox<String> jComboBoxApp;
    ModernScrollPane logcatScrollPane;
    JToggleButton toggleLogcat = ManojUI.getVerticalButton("Logcat",true);
    JPanel mainPanel = new JPanel();
    JPanel optionBar = new JPanel();

    HashMap<String,String> procs = new HashMap<>();

    int len = 0;

    String watchPkg = "";
    Boolean watchMode = false;
    String watchPID = "";
    int FilterMode = 0;
    final int FILTER_WATCH = 1;
    final int FILTER_REGEX = 2;
    final int FILTER_NONE = 0;
    final int FILTER_LATEST = 4;
    Boolean isLogLatest = false;
    String currentTimeStamp = new SimpleDateFormat("MM-dd HH:mm:ss.SSS").format(new Date());
    long startTime = getCurrEmuTime();

    void parseString(String s){
        Matcher matcher = pattern.matcher(s);
        if (!matcher.find()) {return;}

        boolean isActivity = false;
        boolean isKill = false;
        String APID ="";
        String APKG ="";
        final String timeStamp = matcher.group(1);
        final String LPID = matcher.group(2);
        final String logtype = matcher.group(4);
        final String activity = matcher.group(5);

        if(!isLogLatest){if(startTime < parseMills(timeStamp)) {System.out.println(latest);textPane.append(latest); len+=latest.length(); isLogLatest = true;}}
        if(activity.equals("ActivityManager")){
            String data = matcher.group(6).trim();
            if(data.startsWith("Start proc")){
                Matcher pmatcher = pProcStart.matcher(data);
                if (pmatcher.find()) {
                    APID = pmatcher.group(1);
                    APKG = pmatcher.group(2);
                    procs.put(APID, APKG);
                    jComboBoxApp.addItem(APKG);
                    isActivity = true;
                }
            }else if(data.startsWith("Killing")){
                Matcher kmatcher = pProcKill.matcher(data);
                if (kmatcher.find()) {
                    APID = kmatcher.group(1);
                    System.out.println("killed: " + APID + "    pkg: " + procs.get(APID));
                    procs.remove(APID);
                    jComboBoxApp.removeItem(procs.get(APID));
                    isKill = true;
                }
            }
        }

        switch (FilterMode){
            case FILTER_NONE -> addColoredLog(s,logtype);
            case FILTER_WATCH -> {
                if(watchPID.equals("")){
                    if(isActivity){
                        if(APKG.equals(watchPkg)){
                            watchPID = APID;
                            addColoredLog(s,logtype);
                        }
                    }
                }else{
                    if(LPID.equals(watchPID)){
                        addColoredLog(s,logtype);
                    }
                }
            }
            case FILTER_LATEST -> {
                if(isLogLatest){
                    addColoredLog(s,logtype);
                }
            }
        }
    }
    void addColoredLog(String s,String logtype){
        textPane.append(s + "\n");
        try {
            switch (logtype){
                case "E" -> h.addHighlight(len, len + s.length(), Epainter);
                //case "I" -> h.addHighlight(len, len + s.length(), Ipainter);
                case "V" -> h.addHighlight(len, len + s.length(), Vpainter);
                case "W" -> h.addHighlight(len, len + s.length(), Wpainter);
                //case "D" -> h.addHighlight(len, len + s.length(), Dpainter);
            }
            //h.addHighlight(len + matcher.start(i), len + matcher.end(i), cyanPainter);
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
        len += s.length() + 1;
    }
    void clearWindow(){
        textPane.setText("");
        len = 0;
    }
    void setSelectedDevice(String id){}
    void setAdbPath(String path){}
    void setWatch(String pkg){}
    long getCurrEmuTime(){
        String ret = utils.runFastTool(new String[]{"D:\\Program Files\\Nox\\bin\\adb.exe","shell","date"});
        ret = ret.replace(" IST "," ");
        long emuTime = getMillsFromString(ret,"EEE MMM dd HH:mm:ss yyyy");
        String current = new SimpleDateFormat("MM-dd HH:mm:ss.SSS").format(emuTime);
        return getMillsFromString(current,"MM-dd HH:mm:ss.SSS");
    }
    long getMillsFromString(String date,String format){
        try {
            return new SimpleDateFormat(format).parse(date).getTime();
        } catch (ParseException e) {
            return 0;
        }
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

        jComboBoxApp = new JComboBox<>();
        JComboBox<String> jComboBoxLogType = new JComboBox<>();
        jComboBoxLogType.addItem("All");
        jComboBoxLogType.addItem("Error");
        jComboBoxLogType.addItem("Warning");
        jComboBoxLogType.addItem("Info");
        jComboBoxLogType.addItem("Debug");
        jComboBoxLogType.addItem("Verbose");
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
        //this.start();
    }
    public void run() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command,cmdlogcat);
            if(!this.startDir.equals("")) {
                processBuilder.directory(new File(this.startDir));
            }
            proc = processBuilder.start();
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String s;
            while ((s = stdInput.readLine()) != null) {
                parseString(s);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public JComponent getWindow() {
        return mainPanel;
    }

    @Override
    public JToggleButton getButton() {
        return toggleLogcat;
    }
    long parseMills(String date){
        try {
            return new SimpleDateFormat("MM-dd HH:mm:ss.SSS").parse(date).getTime();
        } catch (ParseException e) {
            return 0;
        }
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
