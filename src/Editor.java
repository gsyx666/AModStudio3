// ManojBhaskarPCM : OrhanBank v1.1 : APK Modding IDE.

import com.formdev.flatlaf.FlatDarkLaf;
import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

class Editor {
    ScheduledExecutorService ex = Executors.newSingleThreadScheduledExecutor();
    static Editor thisClass;
    mod_defaultMenus defaultMenus;
    mod_apkUtils apkUtils;
    mod_adbUtils adbUtils;
    mod_packageUtils packageUtils;
    mod_MainMenu mainMenu;
    subc_LogWindow logwindow;
    Listener_StatusBarTasks statusBarTasks;
    //JEditorPane taSmali;
    subc_EditorWindow taSmali;
    subc_EditorWindow taJava;
    JFrame mainWindow;
    JTree fileTree;
    JMenuBar menuBar;
    JLabel statusLabel;
    JToolBar toolBar;
    JProgressBar progressBar;
    HashMap<String,String> vars = new HashMap<>();
    //boolean developmentMode = true;
    public static void main(String[] args) {
        thisClass = new Editor();
    }
    String getVersion(){
        return "AMOD Studio v1.5";
    }
    void initComponents() {
        FlatDarkLaf.setup();
        //FlatIntelliJLaf.setup();
        //FlatLightLaf.setup();
        //FlatDarculaLaf.setup();
        mainWindow = new JFrame(getVersion());
        menuBar = new JMenuBar();
        toolBar = new JToolBar();
        toolBar.setMaximumSize(new Dimension(500,25));
        toolBar.setPreferredSize(new Dimension(500,25));
        //taSmali = new JEditorPane();
        taSmali = new subc_EditorWindow();
        taJava = new subc_EditorWindow();
        //fSM = new FileSystemModel(new File("C:\\"));
        fileTree = new JTree();
        statusLabel = new JLabel("status");
        progressBar = new JProgressBar();

    }

    void initPlugins() {
        defaultMenus = new mod_defaultMenus(this);
        apkUtils = new mod_apkUtils(this);
        adbUtils = new mod_adbUtils(this);
        packageUtils = new mod_packageUtils(this);
        //logwindow = new JTextAreaOutputStream();
        mainMenu = new mod_MainMenu(this);
        statusBarTasks = new Listener_StatusBarTasks(this);
    }

    Editor() {
        //TODO: LogCat Window. Build Window
        //TODO: GUI [work in progress]
        //TODO: log window.
        //TODO: Smali Syntax Check before Compiling.
        //TODO: direct dex class editing.
        //TODO: logcat window.
        //TODO: Run Window(modified version of logcat)
        //TODO: plugins support.
        //TODO: Reference Finder
        //TODO: Ad Remover.
        //TODO: Snippet Injector.

        //TODO: BUG: splitted apk merger not working properly.

        initComponents();
        AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory) TokenMakerFactory.getDefaultInstance();
        atmf.putMapping("text/smali", "smaliSyntax");
        taSmali.setSyntaxEditingStyle("text/smali");

        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setPreferredSize(new Dimension(1500, 800));
        JTabbedPane tabbedPane = new JTabbedPane();
        JComponent panel1 = makeTextPanel("Panel #1");
        tabbedPane.addTab("Tab 1",null, new RTextScrollPane(taJava), "Does nothing");
        mainWindow.setIconImage(utils.getImageFromRes("main.png"));
        mainWindow.setJMenuBar(menuBar);
        toolBar.setMaximumSize(new Dimension(mainWindow.getWidth(), 20));
        mainWindow.add(toolBar, BorderLayout.NORTH);

        taJava.setEditable(false);
        taJava.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                String findWhat = ".line " + (taJava.getCaretLineNumber()+1) + "\n";
                int pos = taSmali.getText().indexOf(findWhat) + findWhat.length() + 1;
                taSmali.setCaretPosition(pos);
                //setStatusBarTextFlash(e.getDot() + " : " + taJava.getCaretLineNumber(),2);
            }
        });
        fileTree.setEditable(true);

        fileTree.addTreeSelectionListener(event -> {
            File file = (File) fileTree.getLastSelectedPathComponent();
            if(file!=null) {
                taSmali.setFile(file.getAbsolutePath(), true);
            }
        });
        initPlugins();
        JSplitPane splitPaneEditors = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, new RTextScrollPane(taSmali), new JScrollPane(tabbedPane));
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, new JScrollPane(fileTree), new JPanel().add(splitPaneEditors));
        splitPaneEditors.setDividerSize(4);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        splitPane.setDividerSize(4);

        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        mainWindow.add(statusPanel, BorderLayout.SOUTH);
        statusPanel.setPreferredSize(new Dimension(mainWindow.getWidth(), 20));
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        progressBar.setPreferredSize(new Dimension(150,4));
        progressBar.setMaximumSize(new Dimension(150,4));
        progressBar.setVisible(false);
        statusLabel.setText("Everything is Ready !");
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusPanel.add(progressBar);
        statusPanel.add(statusLabel);

        mainWindow.getContentPane().add(splitPane);

        mainWindow.setVisible(true);
        mainWindow.pack();

        //taSmali.setContentType("text/html");
        splitPane.setDividerLocation(300);
        splitPaneEditors.setDividerLocation(splitPaneEditors.getWidth() / 2);
        //mainWindow.show();
        //((Runnable) () -> apkUtils.getDevices()).run();
        SwingUtilities.invokeLater(() -> apkUtils.getDevices());

    }
    protected JComponent makeTextPanel(String text) {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);
        return panel;
    }
    public void setStatusBarTextFlash(String text, long seconds) {
        statusLabel.setForeground(Color.YELLOW);
        statusLabel.setText(text);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                statusLabel.setText("Everything .... OK ! :)");
                statusLabel.setForeground(Color.getColor("control"));
            }
        }, seconds*1000);
    }
}

