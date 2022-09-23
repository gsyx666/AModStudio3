import mbpcm.ui.I_Window;
import mbpcm.ui.SmoothIcon;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class mod_compiler extends super_MenuInterface implements I_Window {
    Editor editor;
    mod_compiler(Editor editor_){
        super(editor_);
        editor = editor_;
    }
    void RecompileAndRun(){
        Thread_compileAndInstall compileAndInstall = new Thread_compileAndInstall(editor.statusBarTasks);
        compileAndInstall._decompiledFolderPath = editor.vars.get("project");
        compileAndInstall._zipAlignToolPath = editor.vars.get("zipAlign.exe");
        compileAndInstall._devId = editor.vars.get("device");
        compileAndInstall._adbpath = editor.vars.get("adb.exe");
        compileAndInstall._pemPath = editor.vars.get("cert.pem");
        compileAndInstall._pk8Path = editor.vars.get("pk.pk8");
        compileAndInstall._packageName = editor.vars.get("packageName");
        compileAndInstall._MainClass = editor.vars.get("mainClass");
        compileAndInstall.start();
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
        if(a.equals("init")) {
            JButton run = new JButton();
            run.setIcon(new SmoothIcon(utils.getImageFromRes("start.png")));
            run.addActionListener(e -> RecompileAndRun());
            editor.toolBar.add(run);

            addMenuItems("APK","ReCompile&Run,Locate ReCompiled,Check ZipAlignment,Check Signature,Zip Align,Sign Apk");

        }
    }
    @Override
    public void onMenuItemClick(String menuName, ActionEvent actionEvent) {
        String filepath;
        switch (menuName) {
            case "ReCompile&Run" -> RecompileAndRun();
            case "Check ZipAlignment" -> {
                filepath = utils.selectFileByDialog("Select Apk to Check Zip Alignment", utils.getCurrentPath(), "Apk Files(apk)");
                if (!filepath.equals("")) {
                    if (lib_apk_compile.zipAlgnCheck(filepath, getToolPath("zipAlign.exe"), true)) {
                        utils.MessageBox("Alignment Check", " Yes, Aligned properly");
                    } else {
                        utils.MessageBox("Alignment Check", " No, Not Aligned");
                    }
                }
            }
            case "Check Signature" -> {
                filepath = utils.selectFileByDialog("Select Apk to Check if Signed", utils.getCurrentPath(), "Apk Files(apk)");
                if (!filepath.equals("")) {
                    lib_apk_compile.checkApkSignatures(filepath, true, true);
                }
            }
            case "Zip Align" -> {
                filepath = utils.selectFileByDialog("Select Apk to ZipAlign", utils.getCurrentPath(), "Apk Files(apk)");
                if (!filepath.equals("")) {
                    String outpath = utils.removeExtension(filepath) + "_aligned.apk";
                    lib_apk_compile.zipAlign(filepath, outpath, getToolPath("zipAlign.exe"), true);
                    if (!lib_apk_compile.zipAlgnCheck(outpath, getToolPath("zipAlign.exe"), false)) {
                        utils.MessageBox("Error", "Dont know how this method failed!");
                    } else {
                        utils.MessageBox("Success", "Verified ! now apk aligned properly.");
                    }
                }
            }
            case "Sign Apk" -> {
                String pem = getToolPath("cert.pem");
                String pk8 = getToolPath("pk.pk8");
                filepath = utils.selectFileByDialog("Select Apk to Sign it", utils.getCurrentPath(), "Apk Files(apk)");
                if (!filepath.equals("")) {
                    String outpath = utils.removeExtension(filepath) + "_aligned.apk";
                    lib_apk_compile.signApk(filepath, outpath, pem, pk8, true, true);
                }
            }

        }
    }
    String getToolPath(String key){
        if(editor.vars.containsKey(key)){
            return editor.vars.get(key);
        }else{
            utils.MessageBox("Typo","Developer mistakenly used Wrong Key for hashmap\nthis command will not work\n Bad luck. contact developer!");
            return "";
        }
    }
}
