// ManojBhaskarPCM : OrhanBank v1.1 : APK Modding IDE.

import com.formdev.flatlaf.FlatDarkLaf;
import mbpcm.ui.ManojUI;
import mbpcm.ui.TabbedFileEditor;
import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    window_log logwindow;
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
    ManojUI ui;
    TabbedFileEditor tabbedFileEditor;
    JTextArea LogWindow;
    //boolean developmentMode = true;
    public static void main(String[] args) {
        FlatDarkLaf.setup();
        thisClass = new Editor();

    }
    String getVersion(){
        return "AMOD Studio v1.5";
    }
    void initComponents() {
        ui = new ManojUI();
        tabbedFileEditor = new TabbedFileEditor();
        //FlatIntelliJLaf.setup();
        //FlatLightLaf.setup();
        //FlatDarculaLaf.setup();
        mainWindow = ui.f;
        menuBar = ui.menuBar;
        toolBar = ui.toolBar;
        toolBar.setMaximumSize(new Dimension(500,25));
        toolBar.setPreferredSize(new Dimension(500,25));
        //taSmali = new JEditorPane();
        taSmali = new subc_EditorWindow();
        taJava = new subc_EditorWindow();
        //fSM = new FileSystemModel(new File("C:\\"));
        fileTree = new JTree();
        statusLabel = new JLabel("status");
        progressBar = new JProgressBar();
        LogWindow = new JTextArea();

    }

    void initPlugins() {
        defaultMenus = new mod_defaultMenus(this);
        apkUtils = new mod_apkUtils(this);
        adbUtils = new mod_adbUtils(this);
        packageUtils = new mod_packageUtils(this);
        logwindow = new window_log();
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

        mainWindow.setIconImage(utils.getImageFromRes("main.png"));
        toolBar.setMaximumSize(new Dimension(mainWindow.getWidth(), 20));

        taJava.setEditable(false);

        fileTree.setEditable(false);
        fileTree.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    File file = (File) fileTree.getLastSelectedPathComponent();
                    if(file!=null && file.isFile()) {
                        tabbedFileEditor.addFile(file.getAbsolutePath());
                    }
                }
            }
        });
        JScrollPane spFileTree = new JScrollPane();
        spFileTree.setBorder(BorderFactory.createEmptyBorder());
        spFileTree.setViewportView(fileTree);


        ui.setLeftItem(spFileTree);
        ui.setCenterItem(tabbedFileEditor);
        ui.setRightItem(taJava);

        progressBar.setPreferredSize(new Dimension(150,4));
        progressBar.setMaximumSize(new Dimension(150,4));
        progressBar.setVisible(false);
        statusLabel.setText("Everything is Ready !");
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);

        ui.statusBar.add(progressBar);
        ui.statusBar.add(statusLabel);

        JToggleButton fileTreeToggle = ManojUI.getVerticalButton("Project",true);
        fileTreeToggle.addActionListener(e -> {
            //fileTree.setVisible(fileTreeToggle.isSelected());
            spFileTree.setVisible(fileTreeToggle.isSelected());
        });
        fileTreeToggle.setSelected(true);
        ui.leftBar.add(fileTreeToggle);


        JToggleButton taJavaToggle = ManojUI.getVerticalButton("Java",true);
        taJavaToggle.addActionListener(e -> {
            if(!taJavaToggle.isSelected()){
                taJavaToggle.putClientProperty("dPos",ui.getRightPane().getDividerLocation());
            }else{
                SwingUtilities.invokeLater(() -> {
                    ui.getRightPane().setDividerLocation((int)taJavaToggle.getClientProperty("dPos"));
                    ui.f.setVisible(true);
                });

            }
            taJava.setVisible(taJavaToggle.isSelected());
            ui.f.setVisible(true);
        });
        taJavaToggle.setSelected(true);
        ui.leftBar.add(taJavaToggle);
        tabbedFileEditor.addFile("Welcome","This is AMod Studio v1.5\nAuthor: ManojBhakarPCM");

        initPlugins();


        ui.leftBar.add(logwindow.getButton());
        ui.setBottomItem(logwindow.getWindow());

        ui.rightBar.setVisible(false);
        ui.bottomBar.setVisible(false);

        ui.f.setVisible(true);
        //SwingUtilities.invokeLater(() -> apkUtils.getDevices());

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

 /*
        taJava.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                String findWhat = ".line " + (taJava.getCaretLineNumber()+1) + "\n";
                int pos = taSmali.getText().indexOf(findWhat) + findWhat.length() + 1;
                taSmali.setCaretPosition(pos);
                //setStatusBarTextFlash(e.getDot() + " : " + taJava.getCaretLineNumber(),2);
            }
        });*/