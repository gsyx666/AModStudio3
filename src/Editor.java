// ManojBhaskarPCM : AMod Studio v3 : APK Modding IDE.

import com.formdev.flatlaf.FlatDarculaLaf;
import mbpcm.smaliIndexer.Indexer;
import mbpcm.ui.I_Window;
import mbpcm.ui.ManojUI;
import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

class Editor {
    static Editor thisClass;
    mod_toolSet toolSet;
    project_loader project_loader;
    mod_devices mod_devices;
    mod_compiler compiler;
    mod_defaultMenus defaultMenus;
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
    plugin_smaliHelper functionsBrowser;
    plugin_projectHelper projectHelper;
    IndexedSearchProvider searchProvider;
    //boolean developmentMode = true;
    Renamer renamer;
    Indexer indexer;
    public static void main(String[] args) {

        // FlatDarkLaf.setup();
        // FlatIntelliJLaf.setup();
        // FlatLightLaf.setup();
        //UIManager.put("TabbedPane:TabbedPaneTab.contentMargins", new Insets(10, 100, 0, 0));

        FlatDarculaLaf.setup();
        //UIManager.put("TabbedPane:TabbedPaneTab.contentMargins", new Insets(10, 100, 0, 0));
        thisClass = new Editor();

    }
    String getVersion(){
        return "AMOD Studio v3";
    }
    void initComponents() {
        ui = new ManojUI();
        mainEditor = new window_main(this);
        mainWindow = ui.f;
        menuBar = ui.menuBar;
        toolBar = ui.toolBar;
        toolBar.setMaximumSize(new Dimension(500,25));
        toolBar.setPreferredSize(new Dimension(500,25));
    }

    void initPlugins() {
        toolSet = new mod_toolSet(this);
        project_loader = new project_loader(this);
        mod_devices = new mod_devices(this);
        compiler = new mod_compiler(this);
        fileTree = new window_fileTree(this,mainEditor.tabbedFileEditor);
        //defaultMenus = new mod_defaultMenus(this);
        adbUtils = new mod_adbUtils(this);
        packageUtils = new mod_packageUtils(this);
        //logwindow = new window_log();
        mainMenu = new mod_MainMenu(this);
        statusBarTasks = new Listener_StatusBarTasks(this);
        javaView = new window_JavaView(this);
        statusBarPanel = new Bar_Status();
        logcat = new window_logcat(ui);
        wmgr = new WindowManager(ui);
        functionsBrowser = new plugin_smaliHelper(this);
        projectHelper = new plugin_projectHelper(this);
        searchProvider = new IndexedSearchProvider(this);
        renamer = new Renamer(this);
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
        ui.statusBar.setLayout(new BorderLayout());
        ui.statusBar.add(statusBarPanel.getView(),BorderLayout.WEST);

        //============================= : Load Windows : ========================
        loadedWindows.add(toolSet);
        loadedWindows.add(project_loader);
        loadedWindows.add(mod_devices);
        loadedWindows.add(compiler);
        loadedWindows.add(mainEditor);
        loadedWindows.add(fileTree);
        loadedWindows.add(logcat);
        loadedWindows.add(javaView);
        //loadedWindows.add(logwindow);
        loadedWindows.add(functionsBrowser);
        loadedWindows.add(projectHelper);
        loadedWindows.add(searchProvider);
        loadedWindows.add(renamer);

        for (I_Window cls:loadedWindows) {
            if(cls.getWindowName() != null) { //just plugins for other works.
                wmgr.addWindow(cls.getWindowName(), cls.getWindow(), cls.getButton(), cls.getPrefPosition());
            }
        }
        wmgr.finishAdding();
        //========================================================



        mainEditor.tabbedFileEditor.addFile("Welcome","This is AMod Studio v3\nAuthor: ManojBhakarPCM\nCTRL+ R(Rename) , B(Navigate), F(Find)");
        //SwingUtilities.invokeLater(() -> apkUtils.getDevices());
        settingChanged("base_load",null,null);
        settingChanged("init",null,null);
        ui.f.setVisible(true);
        settingChanged("init2",null,null);
        settingChanged("init3",null,null);

        indexer = Indexer.getInstance();
        indexer.Init(vars.get("project"));

    }

    public void settingChanged(String a, String b, Object c){
        System.out.println(I_Window.class.getName() + " fired Event  [" + a + "] : " + b);
        for (I_Window cls:loadedWindows) {
            cls.onSettingChanged(a,b,c);
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