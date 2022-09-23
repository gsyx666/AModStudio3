import mbpcm.ui.I_Window;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.HashMap;
import java.util.prefs.Preferences;

public class mod_toolSet extends super_MenuInterface implements I_Window {
    String allTools ="";
    String notSetTools = "";
    boolean allToolsSet = true;
    String[] keys = {"adb.exe","zipAlign.exe","pk.pk8","cert.pem"};
    HashMap<String, String> paths = new HashMap<>();
    Editor editor;
    mod_toolSet(Editor _mainWin) {
        super(_mainWin);
        editor = _mainWin;
    }

    void setToolsPath(String key){
        String filepath;
        Preferences prefs = Preferences.userNodeForPackage(mod_toolSet.class);
        if(key.contains(".")) {
            filepath = utils.selectFileByDialog("Select " + key, utils.getCurrentPath(), null);
        }else{
            filepath = utils.selectFolder("Select " + key,"C:");
        }
        if(!filepath.equals("")){
            prefs.put(key,filepath);
            refreshToolsPaths(false);
            utils.MessageBox("Success","Path of " + key + " Saved Successfully!!\nNow Settings are-\n" + allTools);
        }
    }

    void refreshToolsPaths(boolean showError){
        allTools = "";
        notSetTools = "";
        allToolsSet = true;
        Preferences prefs = Preferences.userNodeForPackage(mod_toolSet.class);
        for(String key:keys){
            String toolPath = prefs.get(key,key);
            if(new File(toolPath).exists()) {
                editor.vars.put(key,toolPath);
                paths.put(key, toolPath);
                allTools += key + " : " + toolPath + "\n";
            }else if(new File(".\\tools\\" + toolPath).exists()){
                editor.vars.put(key,toolPath);
                paths.put(key, toolPath);
                allTools += key + " : " + utils.getCurrentPath() + "\\" + toolPath + "\n";
            }else{
                allToolsSet = false;
                notSetTools += key  + "\n";
                paths.put(key,"");
                allTools += key + " : *** Missing ***\n";
            }
        }
        if(!allToolsSet && showError){
            // utils.ErrorBox("Some Tools are Not Set","Please Set Missing Tools Form Setting Menu\n\n" + allTools );
        }
    }

    @Override
    public JComponent getWindow() {
        return null;
    }

    @Override
    public JToggleButton getButton() {
        return null;
    }

    @Override
    public String getWindowName() {
        return null;
    }

    @Override
    public int getPrefPosition() {
        return 0;
    }

    @Override
    public void onSettingChanged(String a, String b, Object c) {
        if(a.equals("base_load")){

        }else if(a.equals("init")){
            refreshToolsPaths(true);
            StringBuilder menuNames = new StringBuilder();
            for(String key:keys){
                menuNames.append("set ").append(key).append(" path,");
            }
            menuNames.append("check all path");
            addSubMenuItems("Settings","Apk Tools Path", menuNames.toString());
            //addMenuItems("Help","Apk Tools Path");
            addMenuItems("Beta","test1");

        }
    }
    @Override
    public void onMenuItemClick(String menuName, ActionEvent actionEvent) {
        String filepath;
        for (String key : keys) {
            if (menuName.equals("set " + key + " path")) {
                setToolsPath(key);
                break;
            }
        }

        switch (menuName) {
            case "check all path" -> utils.MessageBox("Tools Status", allTools);
            case "Apk Tools Path" -> utils.MessageBox("Help ApkTools Path", """
                    set Tools Path for Apk Compiling And Decompiling.
                    without them You Wont be able to do anything in this software.
                    Default Tools are included in this software(in tools folder).
                    but if you want to set new version of tool. then just set its path.
                    list of tools not set-
                                          
                    """ + notSetTools);
        }
    }

}
