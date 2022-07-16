import brut.androlib.ApkDecoder;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Thread_Decompile extends Thread{
    I_itct itctl;
    String apkPath = "";
    long time = 0;
    Thread_Decompile(I_itct _itctl,String _apkPath){
        itctl = _itctl;
        apkPath = _apkPath;
    }

    public void run(){
        resetProfiler();
        itctl.onProgress("task_start",-1,"Preparing ......");
        if(isSplitApk()){
            String[] list = ExtractAllApk();
            if(list==null){return;}
            String[] folders =  decompileApkList(list);

            List<String> otherFolders = new ArrayList<>();
            String basePath = "";
            for(String fol: folders){
                if(fol.endsWith("base")){
                    basePath = fol;
                }else{
                    otherFolders.add(fol);
                }
            }
            itctl.onProgress("task",-1,"Merging..");
            mbpcm.splitMerger.MergeSplitAPK(basePath,otherFolders.toArray(new String[0]));
            itctl.onProgress("task_finish",basePath,"DECOMPILER");
            addToRecentProject(basePath);
            //removing non useful dirs.
            try {
                for (String dir : otherFolders) {
                    FileUtils.deleteDirectory(new File(dir));
                }
                for (String apk: list){
                    FileUtils.delete(new File(apk));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            String outputPath = utils.removeExtension(apkPath) + "_d";
            decompileAPk(apkPath,utils.getUniquePath(outputPath,""));
            addToRecentProject(outputPath);
            itctl.onProgress("task_finish",outputPath,"DECOMPILER");
        }
        itctl.onProgress("task_end",-1,"Everything Ready !");

    }
    void resetProfiler(){
        time = System.currentTimeMillis();
    }
    void showTakenTime(String text){
        System.out.println(text + " took " + (System.currentTimeMillis()-time) + " ms");
        resetProfiler();
    }
    void showError(String err){
        System.out.println(err);
        itctl.onProgress("task_end",0,err + " see build Window for more details!");
    }
    boolean isSplitApk(){
        if(apkPath.toLowerCase(Locale.ROOT).endsWith(".zip")){  //Problem1: path is not a zip extension.
            String[] files = utils.getZipFileList(apkPath);
                if(files!=null){ //Problem2: path is zip extension but is not a valid zip file.
                    return Arrays.asList(files).contains("base.apk"); //Problem3: path is zip and valid zip file but do not contains base.apk and others..
                }
            }
        return false;
    }
    String[] ExtractAllApk(){
        //returns list of extracted apk paths for decompilation.
        java.util.List<String> out = new ArrayList<>();
        //path: bla/bla/myapp.zip => bla/bla/split/[base.apk,config.apk,....]
        String taskFolder = utils.removeExtension(apkPath) + "-split";
        //String taskFolder = utils.getUniquePath(baseFolder + "\\split","");
        String[] zipFileList = utils.getZipFileList(apkPath);
        if(zipFileList==null){return null;} //though it should never be null because we will check this zip well using isSplitApk() function.
        String[] selectedFiles = utils.showSelectionDialog(zipFileList);
        if(selectedFiles==null){return null;}
        for(String zipFile:selectedFiles){
            if(zipFile.toLowerCase(Locale.ROOT).endsWith(".apk")) {
                String outPath = taskFolder + "\\" + zipFile;
                utils.extractZipFile(apkPath, zipFile, outPath);
                out.add(outPath);
            }
        }
        String[] retout = new String[out.size()];
        return out.toArray(retout);
    }
    boolean decompileAPk(String inputFilePath,String outputDirPath) {
        ApkDecoder apkDecoder = new ApkDecoder();
        try {
            itctl.onProgress("task",-1,"decompiling : " + java.nio.file.Paths.get(inputFilePath).getFileName());
            apkDecoder.setApkFile(new File(inputFilePath));
            apkDecoder.setOutDir(new File(outputDirPath));
            apkDecoder.decode();
            return true;
        } catch (Exception e) {
            itctl.onProgress("task",-1,"decompiling Failed : " + java.nio.file.Paths.get(inputFilePath).getFileName());
            return false;
        }
    }
    String[] decompileApkList(String[] apkfiles){
        List<String> list = new ArrayList<>();
        for(String inFile: apkfiles){
            String outDir = utils.removeExtension(inFile);
            if(!decompileAPk(inFile,outDir)){
                list.add(outDir);
            }else{
                list.add(outDir);
            }
        }
        return list.toArray(new String[0]);
    }
    void addToRecentProject(String projectPath){
        String data = utils.file_get_contents("recentProjects.txt");
        if(data!=null) {
            String[] lines = data.split("\n");
            for (String line : lines) {
                if (line.contains(projectPath)) {
                    return;
                }
            }
        }
        utils.file_append("recentProjects.txt",projectPath + "\n");
        utils.file_put_contents("lastproject.txt",projectPath);
    }

}
