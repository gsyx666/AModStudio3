package manoj;

import brut.androlib.Androlib;
import brut.androlib.ApkDecoder;
import brut.androlib.ApkOptions;
import jadx.api.JadxArgs;
import jadx.api.JadxDecompiler;
import jadx.core.Jadx;
import org.jf.smali.Smali;
import org.jf.smali.SmaliOptions;

import java.io.File;
import java.io.IOException;

public class ManojTools {
    public static boolean decompileAPk(String inputFilePath,String outputDirPath) {
        if(!new File(inputFilePath).exists()){
            System.out.println("Cant decompile Input File does not exists");
            return false;
        }
        ApkDecoder apkDecoder = new ApkDecoder();
        try {
            apkDecoder.setApkFile(new File(inputFilePath));
            apkDecoder.setOutDir(new File(outputDirPath));
            apkDecoder.decode();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean recompileApk(String srcDirPath,String outputApkPath) {
        if(!new File(srcDirPath).exists()){
            System.out.println("Cant Recompile Input Folder path does not exists");
            return false;
        }
        ApkOptions apkOptions = new ApkOptions();
        try {
            apkOptions.debugMode = true;
            Androlib androlib = new Androlib(apkOptions);
            androlib.build(new File(srcDirPath), new File(outputApkPath));
            androlib.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static void smaliToDexFast(String inputFilePath, String outputFilePath) throws Exception{
        SmaliOptions smaliOptions = new SmaliOptions();
        smaliOptions.outputDexFile = outputFilePath;
        smaliOptions.verboseErrors = true;
        Smali.assemble(smaliOptions,inputFilePath);
    }
    public static String DexToJava(String inputDex){
        JadxArgs args = new JadxArgs();
        args.setInputFile(new File(inputDex));
       // args.close();

        try(JadxDecompiler jadx = new JadxDecompiler(args)) {
            jadx.load();
            return(jadx.getClasses().get(0).getClassNode().decompile().getCodeStr());
        }catch (Exception ignored){
            return "";
        }
    }
}
