import brut.androlib.Androlib;
import brut.androlib.ApkOptions;
import com.android.apksig.ApkSigner;
import com.android.apksig.ApkVerifier;
import com.android.apksig.apk.ApkFormatException;
import mbpcm.customViews.FileSystemModel;
import mbpcm.ui.SmoothIcon;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.*;
import java.util.prefs.Preferences;

import static org.apache.commons.lang3.ArrayUtils.reverse;

public class mod_apkUtils extends super_MenuInterface implements I_itct {
    Editor mainwin;
    String allTools ="";
    String notSetTools = "";
    boolean allToolsSet = true;
    String[] keys = {"adb.exe","zipAlign.exe","pk.pk8","cert.pem"};
    HashMap<String, String> paths = new HashMap<>();
    JComboBox<String> devices;
    HashMap<String,String> AndroidDevices = new HashMap<>();
    //String packageName = "";
    //String MainClass = "";
    String SignedApkPath;

    mod_apkUtils(Editor _mainWin) {
        super(_mainWin);
        mainwin = _mainWin;
        //cmdWindow = new CmdUtils(mainwin.taSmali,this);
        autoSetJAVA();
        refreshToolsPaths(true);
        //---------------Create---------
        StringBuilder menuNames = new StringBuilder();
        for(String key:keys){
            menuNames.append("set ").append(key).append(" path,");
        }
        menuNames.append("check all path");
        loadRecentProjects();
        addMenuItems("File","Decompile,OpenFolderAsProject");
        addSubMenuItems("Settings","Apk Tools Path", menuNames.toString());
        addMenuItems("Help","Apk Tools Path");
        addMenuItems("Beta","test1");
        addMenuItems("APK","ReCompile&Run,Locate ReCompiled,Check ZipAlignment,Check Signature,Zip Align,Sign Apk");
        //--------------------------------
        devices = new JComboBox<>();
        devices.setMaximumSize(new Dimension(200,30));
        JButton refreshDevices = new JButton();
        refreshDevices.setIcon(new SmoothIcon(utils.getImageFromRes("icons8-synchronize-12.png")));
        JButton run = new JButton();
        run.setIcon(new SmoothIcon(utils.getImageFromRes("start.png")));
        run.addActionListener(e -> RecompileAndRun());
        refreshDevices.addActionListener(e -> getDevices());


        mainwin.toolBar.add(Box.createHorizontalGlue());
        mainwin.toolBar.add(devices);
        //mainwin.toolBar.add(Box.createHorizontalStrut(2));
        mainwin.toolBar.add(refreshDevices);
        //mainwin.toolBar.add(Box.createHorizontalStrut(2));
        mainwin.toolBar.add(run);
    }
    public void getDevices(){
        devices.removeAllItems();
        reconnectNox();
        refreshAndroidDevices();
        for (HashMap.Entry<String, String> set : AndroidDevices.entrySet()) {
            devices.addItem(set.getKey());
        }
        if(devices.getItemCount()==1){
            devices.setSelectedIndex(0);
        }
    }
    public void loadProject(String lastProject){
        if(lastProject==null){return;}
        mainWin.mainWindow.setTitle(mainwin.getVersion() + " : " + lastProject);
        mainWin.fileTree.fileTree.setModel(new FileSystemModel(new File(lastProject)));
        String pa = LIb_apkFunctions.parsePackageAndMainActivity(lastProject + "\\AndroidManifest.xml");
        if(pa==null){return;}
        String[] tmp = pa.split(";");
        mainWin.vars.put("packageName",tmp[0]);
        mainWin.vars.put("mainClass",tmp[1]);
    }
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void onMenuItemClick(String menuName, ActionEvent actionEvent){
        String filepath;
        for(String key:keys){
            if(menuName.equals("set " + key + " path")){
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
            case "Decompile" -> {
                filepath = utils.selectFileByDialog("Select Apk To Decompile", getCurrentPath(), "Apk Files(apk)|Split Apk(zip)");
                if (!filepath.equals("")) {
                    new Thread_Decompile(mainwin.statusBarTasks, filepath).start();
                }
            }
            case "ReCompile&Run" -> RecompileAndRun();
            case "Check ZipAlignment" -> {
                filepath = utils.selectFileByDialog("Select Apk to Check Zip Alignment", utils.getCurrentPath(), "Apk Files(apk)");
                if (!filepath.equals("")) {
                    if (zipAlgnCheck(filepath, getToolPath("zipAlign.exe"), true)) {
                        utils.MessageBox("Alignment Check", " Yes, Aligned properly");
                    } else {
                        utils.MessageBox("Alignment Check", " No, Not Aligned");
                    }
                }
            }
            case "Check Signature" -> {
                filepath = utils.selectFileByDialog("Select Apk to Check if Signed", utils.getCurrentPath(), "Apk Files(apk)");
                if (!filepath.equals("")) {
                    checkApkSignatures(filepath, true, true);
                }
            }
            case "Zip Align" -> {
                filepath = utils.selectFileByDialog("Select Apk to ZipAlign", utils.getCurrentPath(), "Apk Files(apk)");
                if (!filepath.equals("")) {
                    String outpath = utils.removeExtension(filepath) + "_aligned.apk";
                    zipAlign(filepath, outpath, getToolPath("zipAlign.exe"), true);
                    if (!zipAlgnCheck(outpath, getToolPath("zipAlign.exe"), false)) {
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
                    signApk(filepath, outpath, pem, pk8, true, true);
                }
            }
            case "Locate ReCompiled" -> utils.LocateFileInExplorer(SignedApkPath);
            case "OpenFolderAsProject" -> {
                filepath = utils.selectFolder("Select Project Folder", utils.getCurrentPath());
                if (!filepath.equals("")) {
                    addToRecentProject(filepath);
                    loadProject(filepath);
                }
            }
            default -> {
                File file = new File(menuName);
                if (file.exists() && file.isDirectory()) {
                    utils.file_put_contents("lastproject.txt", menuName);
                    loadProject(menuName);
                }
                throw new IllegalStateException("Unexpected value: " + menuName);
            }
        }
    }
    void RecompileAndRun(){
       Thread_compileAndInstall compileAndInstall = new Thread_compileAndInstall(mainWin.statusBarTasks);
        compileAndInstall._decompiledFolderPath = mainWin.fileTree.fileTree.getModel().getRoot().toString();
        compileAndInstall._zipAlignToolPath = getToolPath("zipAlign.exe");
        compileAndInstall._devId = AndroidDevices.get(devices.getSelectedItem().toString());
        compileAndInstall._adbpath = getToolPath("adb.exe");
        compileAndInstall._pemPath = getToolPath("cert.pem");
        compileAndInstall._pk8Path = getToolPath("pk.pk8");
        compileAndInstall._packageName = mainWin.vars.get("packageName");
        compileAndInstall._MainClass = mainWin.vars.get("mainClass");
        compileAndInstall.start();
    }
    void refreshToolsPaths(boolean showError){
        allTools = "";
        notSetTools = "";
        allToolsSet = true;
        Preferences prefs = Preferences.userNodeForPackage(mod_apkUtils.class);
        for(String key:keys){
            String toolPath = prefs.get(key,key);
            if(new File(toolPath).exists()) {
                paths.put(key, toolPath);
                allTools += key + " : " + toolPath + "\n";
            }else if(new File(".\\tools\\" + toolPath).exists()){
                paths.put(key, toolPath);
                allTools += key + " : " + getCurrentPath() + "\\" + toolPath + "\n";
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

     void setToolsPath(String key){
         String filepath;
        Preferences prefs = Preferences.userNodeForPackage(mod_apkUtils.class);
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
    void autoSetJAVA(){
        Preferences prefs = Preferences.userNodeForPackage(mod_apkUtils.class);
        String jpath = prefs.get(keys[0],"");
        if(jpath.equals("") || !new File(jpath).exists()){
            String javapath = utils.getJavaPath();
            if(javapath!=""){
                prefs.put(keys[0],javapath);
            }
        }

    }
    String getToolPath(String key){
        if(paths.containsKey(key)){
            return paths.get(key);
        }else{
            utils.MessageBox("Typo","Developer mistakenly used Wrong Key for hashmap\nthis command will not work\n Bad luck. contact developer!");
            return "";
        }
    }



    public static String getCurrentPath(){
        try {
            String path = new File(".").getCanonicalPath();
            return path;
        } catch (IOException e) {
            return "";
        }
    }
    public static boolean recompileApk(String srcDirPath,String outputApkPath,boolean showErrorBox,boolean showSuccessBox) {
        if(!new File(srcDirPath).exists()){
            if(showErrorBox){ utils.ErrorBox("Cant Recompile","Input Folder path does not exists");}
            return false;
        }
        ApkOptions apkOptions = new ApkOptions();
        try {
            apkOptions.debugMode = true;
            Androlib androlib = new Androlib(apkOptions);
            androlib.build(new File(srcDirPath), new File(outputApkPath));
            //androlib.build(new File(srcDirPath), new File(outputApkPath));
            androlib.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if(showErrorBox){utils.ErrorBox("ReCompilation Eroor",e.getMessage());};
            return false;
        }
    }
    public static boolean zipAlgnCheck(String inputFile,String toolpath,boolean showMsgBox){
        //String toolpath = getToolPath("zipAlign.exe");
        String out = utils.runFastTool(new String[]{toolpath,"-c","-v","4",inputFile});
        if(showMsgBox) {
            utils.MessageBox("ZipAlignCheck", out);
        }
        return out.contains("Verification succesful");
    }
    public static boolean zipAlign(String inputFile,String outputPath,String toolpath,boolean showMsgbox){
        //zipalign -p 4 my.apk my-aligned.apk
        //String toolpath = utils.getCurrentPath()+"\\tools\\zipalign.exe";
        String out = utils.runFastTool(new String[]{toolpath,"-p","-v","4",inputFile,outputPath});
        if(showMsgbox){
        utils.MessageBox("zipAlign Results",out);
        }
        return true; //TODO: properly check output.
    }

    public static boolean checkApkSignatures(String filepath,boolean showErrorBox,boolean showMsgbox){
        try {
            ApkVerifier.Result result = new ApkVerifier.Builder(new File(filepath)).build().verify();
            if(!result.isVerified() && showErrorBox) {
                utils.ErrorBox("Not Verified", "Signature Error-\n" + result.getAllErrors() + "\n" + result.getWarnings());
            }
            if(result.isVerified() && showMsgbox) {
                utils.MessageBox("Verified", "Genuine Signed APK");
            }
            return result.isVerified();
        } catch (IOException | NoSuchAlgorithmException | ApkFormatException e) {
            if(showErrorBox) {
                utils.ErrorBox("APK VERIFIER", e.getMessage());
            }
            return false;
        }
    }
    public static boolean signApk(String inputApkPath,String outputApkPath,String pem,String pk8,boolean showErrorBox,boolean showSuccessBox) {
        String toolsPath = utils.getCurrentPath() + "\\tools\\";
        try {
            PrivateKey privateKey = utils.getPK8_privateKey(pk8);
            X509Certificate certificate = utils.getPem_publicCertificate(pem);
            List<X509Certificate> certList = Collections.singletonList(certificate);
            List<ApkSigner.SignerConfig> listSignerConfig = Collections.singletonList(new ApkSigner.SignerConfig.Builder("signer #0", privateKey, certList).build());
            ApkSigner.Builder builder = new ApkSigner.Builder(listSignerConfig);
            builder.setInputApk(new File(inputApkPath));
            builder.setOutputApk(new File(outputApkPath));
            builder.build().sign();
            if (showSuccessBox) {
                utils.MessageBox("Success", "ApkSigned");
            }
            return true;
        } catch (Exception e) {
            if (showErrorBox) {
                utils.ErrorBox("Signing Error", e.getMessage());
            }
            return false;
        }
    }

    void refreshAndroidDevices(){
        AndroidDevices.clear();
        String adbpath = getToolPath("adb.exe");
        String results = utils.runFastTool(new String[]{adbpath,"devices"});
        System.out.println(results);
        List<String> deviceList = new ArrayList<String>();
        String[] lines = results.split("\n");

        for(String line:lines){
            if (line.endsWith("device")) {
                deviceList.add(line.split("\\t")[0]);
            }
        }

        for (String device : deviceList) {
            results = utils.runFastTool(new String[]{adbpath, "-s" ,device ,"shell","getprop","ro.product.model"});
            AndroidDevices.put(results,device);
        }
    }
    void reconnectNox(){
        String adbpath = getToolPath("adb.exe");
        String results = utils.runFastTool(new String[]{adbpath, "connect" ,"127.0.0.1:62001"});
        System.out.println(results);
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
    public void onProgress(String what, Object detail, String data) {}
}
