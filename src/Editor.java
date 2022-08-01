// ManojBhaskarPCM : AMod Studio v3 : APK Modding IDE.

import com.eztech.util.JavaClassFinder;
import com.formdev.flatlaf.FlatDarculaLaf;
import mbpcm.ui.I_Window;
import mbpcm.ui.ManojUI;
import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;

import javax.swing.*;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

class Editor {
    static Editor thisClass;
    mod_defaultMenus defaultMenus;
    mod_apkUtils apkUtils;
    mod_adbUtils adbUtils;
    mod_packageUtils packageUtils;
    mod_MainMenu mainMenu;
    window_log logwindow;
    window_fileTree fileTree;
    Listener_StatusBarTasks statusBarTasks;
    Bar_Status statusBarPanel;
    window_JavaView javaView;
    window_main mainEditor;
    JFrame mainWindow;
    JMenuBar menuBar;
    JToolBar toolBar;
    HashMap<String,String> vars = new HashMap<>();
    ManojUI ui;
    window_logcat logcat;
    WindowManager wmgr;
    List<I_Window> loadedWindows = new ArrayList<>();
    //boolean developmentMode = true;
    public static void main(String[] args) {
        //FlatDarkLaf.setup();
        //FlatIntelliJLaf.setup();
        //FlatLightLaf.setup();
        FlatDarculaLaf.setup();
        thisClass = new Editor();

    }
    String getVersion(){
        return "AMOD Studio v3";
    }
    void initComponents() {
        ui = new ManojUI();
        mainEditor = new window_main();
        mainWindow = ui.f;
        menuBar = ui.menuBar;
        toolBar = ui.toolBar;
        toolBar.setMaximumSize(new Dimension(500,25));
        toolBar.setPreferredSize(new Dimension(500,25));
    }

    void initPlugins() {
        fileTree = new window_fileTree(ui,mainEditor.tabbedFileEditor);
        defaultMenus = new mod_defaultMenus(this);
        apkUtils = new mod_apkUtils(this);
        adbUtils = new mod_adbUtils(this);
        packageUtils = new mod_packageUtils(this);
        logwindow = new window_log();
        mainMenu = new mod_MainMenu(this);
        statusBarTasks = new Listener_StatusBarTasks(this);
        javaView = new window_JavaView(ui);
        statusBarPanel = new Bar_Status();
        logcat = new window_logcat(ui);
        wmgr = new WindowManager(ui);
    }

    Editor() {
        //TODO: Smali Syntax Check before Compiling.
        //TODO: direct dex class editing.
        //TODO: Run Window(modified version of logcat)
        //TODO: plugins support.
        //TODO: Reference Finder
        //TODO: Ad Remover.
        //TODO: Snippet Injector.
        //TODO: BUG: splitted apk merger not working properly.

        initComponents();
        initPlugins();

        AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory) TokenMakerFactory.getDefaultInstance();
        atmf.putMapping("text/smali", "smaliSyntax");
        mainEditor.tabbedFileEditor.syntaxMap.put("smali","text/smali");

        mainWindow.setIconImage(utils.getImageFromRes("main.png"));
        toolBar.setMaximumSize(new Dimension(mainWindow.getWidth(), 20));
        ui.rightBar.setVisible(false);
        ui.bottomBar.setVisible(false);
        ui.statusBar.add(statusBarPanel.getView());

        //============================= : Load Windows : ========================

        loadedWindows.add(mainEditor);
        loadedWindows.add(fileTree);
        loadedWindows.add(logcat);
        loadedWindows.add(javaView);
        loadedWindows.add(logwindow);

        for (I_Window cls:loadedWindows) {
            wmgr.addWindow(cls.getWindowName(),cls.getWindow(),cls.getButton(),cls.getPrefPosition());
        }
        wmgr.finishAdding();
        //========================================================


        ui.f.setVisible(true);

        mainEditor.tabbedFileEditor.addFile("Welcome","This is AMod Studio v1.5\nAuthor: ManojBhakarPCM");
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

    public void settingChanged(I_Window window,String a,String b,Object c){
        System.out.println(I_Window.class.getName() + ": SETTING CHANGED:" + a + " : " + b);
        for (I_Window cls:loadedWindows) {
            if(!cls.equals(window)){
                cls.onSettingChanged(a,b,c);
            }
        }
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
        });
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
 }*/