import mbpcm.customViews.FileSystemModel;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class Listener_StatusBarTasks implements I_itct {
    Editor mainWin;
    Listener_StatusBarTasks(Editor _mainWin) {
        mainWin = _mainWin;
    }

    void startTask(boolean indefinit){
        mainWin.statusBarPanel.progressBar.setVisible(true);
        mainWin.statusBarPanel.progressBar.setIndeterminate(indefinit);
    }



    @Override
    public void onProgress(String what, Object detail, String data) {
        //task_start:       progressbarType{-1 means infinit, else means maximum}           label text
        //task      :        >= 0 means set value                                           label text
        //task_end  :       null                                                            label text
        //task_finish:      data provided by Thread to process further                      ID of Thread.
        int p;
        mainWin.statusBarPanel.statusLabel.setText("\t" + data);
        switch (what) {
            case "task" -> {
                p = (int) detail;
                if (p >= 0) {
                    mainWin.statusBarPanel.progressBar.setValue(p);
                }
            }
            case "task_start" -> {
                mainWin.statusBarPanel.progressBar.setPreferredSize(new Dimension(150, 4));
                mainWin.statusBarPanel.progressBar.setMaximumSize(new Dimension(150, 4));
                mainWin.statusBarPanel.progressBar.setVisible(true);
                p = (int) detail;
                if (p < 0) {
                    mainWin.statusBarPanel.progressBar.setIndeterminate(true);
                } else {
                    mainWin.statusBarPanel.progressBar.setIndeterminate(false);
                    mainWin.statusBarPanel.progressBar.setMaximum(p);
                }
            }
            case "task_end" -> {
                mainWin.statusBarPanel.progressBar.setPreferredSize(new Dimension(0, 4));
                mainWin.statusBarPanel.progressBar.setMaximumSize(new Dimension(0, 4));
                mainWin.statusBarPanel.progressBar.setVisible(false);
            }
            case "task_finish" ->{
                if ("DECOMPILER".equals(data)) {
                    String decompiledFolder = (String) detail;
                    mainWin.settingChanged(null,"app_decompiled",decompiledFolder,null);
                    //mainWin.fileTree.fileTree.setModel(new FileSystemModel(new File(decompiledFolder)));
                    //mainWin.mainWindow.setTitle(mainWin.getVersion() + " : " + decompiledFolder);
                    String info = LIb_apkFunctions.parsePackageAndMainActivity(decompiledFolder + "\\AndroidManifest.xml");
                    String[] inffo = info .split(";");
                    mainWin.vars.put("packageName",inffo[0]);
                    mainWin.vars.put("mainClass",inffo[1]);
                }
            }
            case "setting" ->{
                String pkg = (String) detail;
                mainWin.settingChanged(null,"app_launched",pkg,null);
            }
        }

    }
}
