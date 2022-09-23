import mbpcm.ui.I_Window;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang3.ArrayUtils.reverse;

public class project_loader extends super_MenuInterface implements I_Window {
    Editor editor;
    project_loader(Editor _mainWin) {
        super(_mainWin);
        editor = _mainWin;
    }

    public void loadProject(String lastProject){
        if(lastProject==null){return;}
        mainWin.mainWindow.setTitle(editor.getVersion() + " : " + lastProject);
        //mainWin.fileTree.fileTree.setModel(new FileSystemModel(new File(lastProject)));
        String pa = lib_apk_xml.parsePackageAndMainActivity(lastProject + "\\AndroidManifest.xml");
        if(pa==null){return;}
        String[] tmp = pa.split(";");
        mainWin.vars.put("project",lastProject);
        mainWin.vars.put("packageName",tmp[0]);
        mainWin.vars.put("mainClass",tmp[1]);
        mainWin.settingChanged("project_loaded",lastProject,tmp);
    }
    public void addToRecentProject(String projectPath){
        String data = utils.file_get_contents("recentProjects.txt");
        if(data!=null){
            String[] lines = data.split("\n");
            for(String line:lines){
                if(line.contains(projectPath)){
                    return;
                }
            }
        }
        utils.file_append("recentProjects.txt",projectPath + "\n");
    }
    public void loadRecentProjects(){
        boolean first = true;
        String data = utils.file_get_contents("recentProjects.txt");
        if(data==null){return;}
        String[] lines = data.trim().split("\n");
        reverse(lines);
        List<String> sanitized = new ArrayList<>();
        int LIMIT = 10;
        int count = 0;
        for(String line: lines){
            if(count>LIMIT){break;}
            if(new File(line).exists()){
                if(first){
                    first = false;
                    loadProject(line);
                }
                sanitized.add(line);
                count++;
            }
        }
        addSubMenuItems("File","Recent", StringUtils.join(sanitized,","));
        Collections.reverse(sanitized);
        String out = StringUtils.join(sanitized,"\n");
        utils.file_put_contents("recentProjects.txt",out);
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
    public void onMenuItemClick(String menuName, ActionEvent actionEvent){
        switch (menuName) {
            case "OpenFolderAsProject" -> {
                String filepath = utils.selectFolder("Select Project Folder", utils.getCurrentPath());
                if (!filepath.equals("")) {
                    addToRecentProject(filepath);
                    loadProject(filepath);
                }
            }
            case "" -> {
                File file = new File(menuName);
                if (file.exists() && file.isDirectory()) {
                    utils.file_put_contents("lastproject.txt", menuName);
                    loadProject(menuName);
                }
                throw new IllegalStateException("Unexpected value: " + menuName);
            }
            case "Decompile" -> {
                   String filepath = utils.selectFileByDialog("Select Apk To Decompile", utils.getCurrentPath(), "Apk Files(apk)|Split Apk(zip)");
                    if (!filepath.equals("")) {
                        new Thread_Decompile(editor.statusBarTasks, filepath).start();
                    }
            }
        }
    }
    @Override
    public void onSettingChanged(String a, String b, Object c) {
        if(a.equals("base_load")){
            loadRecentProjects();
        }else if(a.equals("decompile_finish")){
            addToRecentProject(b);
        }else if(a.equals("init")){
            addMenuItems("File","Decompile,OpenFolderAsProject");
        }
    }
}
