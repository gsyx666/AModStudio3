import brut.androlib.Androlib;
import brut.androlib.ApkOptions;
import com.android.apksig.ApkSigner;
import com.android.apksig.ApkVerifier;
import com.android.apksig.apk.ApkFormatException;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;

public class lib_apk_compile {
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
    public static boolean zipAlign(String inputFile,String outputPath,String toolpath,boolean showMsgbox){
        //zipalign -p 4 my.apk my-aligned.apk
        //String toolpath = utils.getCurrentPath()+"\\tools\\zipalign.exe";
        String out = utils.runFastTool(new String[]{toolpath,"-p","-v","4",inputFile,outputPath});
        if(showMsgbox){
            utils.MessageBox("zipAlign Results",out);
        }
        return true; //TODO: properly check output.
    }
    public static boolean zipAlgnCheck(String inputFile,String toolpath,boolean showMsgBox){
        //String toolpath = getToolPath("zipAlign.exe");
        String out = utils.runFastTool(new String[]{toolpath,"-c","-v","4",inputFile});
        if(showMsgBox) {
            utils.MessageBox("ZipAlignCheck", out);
        }
        return out.contains("Verification succesful");
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
            androlib.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if(showErrorBox){utils.ErrorBox("ReCompilation Eroor",e.getMessage());};
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
}
