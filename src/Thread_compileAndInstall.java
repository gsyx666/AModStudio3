import java.io.File;

public class Thread_compileAndInstall extends Thread{
    I_itct itctl;
    String _decompiledFolderPath = "";
    String _pemPath = "";
    String _pk8Path = "";
    String _zipAlignToolPath = "";
    String _adbpath = "";
    String _devId = "";
    String _packageName = "";
    String _MainClass = "";
    long time = 0;
    Thread_compileAndInstall(I_itct _itctl){
        itctl = _itctl;
    }
    public boolean validateTools(){
        String[] ar = {_decompiledFolderPath,_pemPath,_pk8Path,_zipAlignToolPath,_adbpath,_devId,_packageName,_MainClass};
        for(String m: ar){
            if (m.equals("")){
                System.out.println("Error: A Tool Path is Not set.");
                return false;
            }
        }
        return true;
    }

    public void run(){
        if(!validateTools()){
            return;
        }
        resetProfiler();
        String orignalApkPath;

        itctl.onProgress("task_start",-1,"Preparing ......");

        orignalApkPath = _decompiledFolderPath.replace("_decompiled","");
        String RecompiledApkPath = utils.getUniquePath(orignalApkPath , "_R.apk");
        String ZipAlignedApkPath = utils.getUniquePath(orignalApkPath, "_RZ.apk");
        String SignedApkPath = utils.getUniquePath(orignalApkPath, "_RZS.apk");
        _decompiledFolderPath = utils.removeLastChar(_decompiledFolderPath,'\\');

        itctl.onProgress("task",-1,"Compiling......");


        boolean ret = lib_apk_compile.recompileApk(_decompiledFolderPath,RecompiledApkPath,false,false);
        if(!ret){showError("ReCompiling.....failed!");return;}

        itctl.onProgress("task",-1,"Zip Aligning......");

        ret = lib_apk_compile.zipAlign(RecompiledApkPath,ZipAlignedApkPath,_zipAlignToolPath,false);
        if(!ret){showError("ZipAligning.....failed!");return;}


        itctl.onProgress("task",-1,"Signing......");

        ret = lib_apk_compile.signApk(ZipAlignedApkPath,SignedApkPath,_pemPath,_pk8Path,true,false);
        if(!ret){showError("Signing With Default Signatures...........failed!");return;}


        itctl.onProgress("task",-1,"Verifying Alignment......");

        ret = lib_apk_compile.zipAlgnCheck(SignedApkPath,_zipAlignToolPath,false);
        if(!ret){showError("ZipAlignment Check ...........Problem ! Not Aligned !");return;}

        itctl.onProgress("task",-1,"Verifying Signature ......");

        ret = lib_apk_compile.checkApkSignatures(SignedApkPath,true,false);
        if(!ret){showError("Signature Check ...........Problem ! Invalid Signature !");return;}

        showTakenTime("Compiling");

        //System.out.println("Installing apk to device ..... may take some time..");

        String result;
        itctl.onProgress("task",-1,"Installing ......");
        result = utils.runFastTool(new String[]{_adbpath,"-s",_devId,"install","-r", SignedApkPath});
        System.out.println(result); //Success
        showTakenTime("Direct Update");
        if(result.contains("INSTALL_FAILED_UPDATE_INCOMPATIBLE")){
            itctl.onProgress("task",-1,"Removing old apk......");
            result = utils.runFastTool(new String[]{_adbpath,"-s",_devId,"uninstall",_packageName});
            System.out.println(result);
            itctl.onProgress("task",-1,"Retrying installation......");
            result = utils.runFastTool(new String[]{_adbpath,"-s",_devId,"install","-r", SignedApkPath});
            System.out.println(result); //Success
            showTakenTime("Uninstall And Then Install");
        }
        itctl.onProgress("task",-1,"Launching Application......");
        itctl.onProgress("setting",_packageName,null);
        result = utils.runFastTool(new String[]{_adbpath,"shell","am","start","-n",_packageName + "/" + _MainClass});//adb shell am start -n $p/$a
        System.out.println(result);

        itctl.onProgress("task",-1,"Removing temp apk......");

        new File(RecompiledApkPath).delete();
        new File(ZipAlignedApkPath).delete();

        itctl.onProgress("task_end",-1,"Everything Ready...");
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
}
