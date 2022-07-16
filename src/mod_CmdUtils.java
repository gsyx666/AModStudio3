import javax.swing.*;
import java.io.*;

public class mod_CmdUtils extends Thread {
    JTextArea t;
    String cmd;
    String[] cmds;
    String lastOutput = "";
    String lastError = "";
    int method;
    String startDir = "";
    I_itct idtct;
    String _ID;
    mod_CmdUtils(JTextArea textArea, I_itct _idtct){
        this.idtct = _idtct;
        this.t = textArea;
    }
    public void runCommand(String ID,String command){
        this._ID = ID;
        this.cmd = command;
        this.method = 1;
        this.startDir = "";
        start();
    }
    public void runCommand(String ID,String[] command){
        this._ID = ID;
        this.cmds = command;
        this.method = 2;
        this.startDir = "";
        start();
    }
    public void runCommand(String ID,String command,String workingdir){
        this._ID = ID;
        this.cmd = command;
        this.method = 1;
        this.startDir = workingdir;
        start();
    }
    public void runCommand(String ID,String[] command,String workingdir){
        this._ID = ID;
        this.cmds = command;
        this.method = 2;
        this.startDir = workingdir;
        start();
    }
    public void run() {
        switch (method){
            case 1:
                RunCommandOnCMD(this.cmd);
                break;
            case 2:
                RunWithoutCMD(this.cmds);
        }
    }
    private void RunWithoutCMD(String[] cmd){
        try {
            lastOutput = "";
            lastError = "";
            ProcessBuilder processBuilder = new ProcessBuilder(cmd);
            if(!this.startDir.equals("")) {
                processBuilder.directory(new File(this.startDir));
            }
            final Process proc = processBuilder.start();
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            String s;
            while ((s = stdInput.readLine()) != null) {
                t.append(s + "\n");
                lastOutput += s + "\n";
                idtct.onProgress(this._ID,"OUTPUT",s);
            }

            // read any errors from the attempted command
            while ((s = stdError.readLine()) != null) {
                t.append(s + "\n");
                lastError += s + "\n";
                idtct.onProgress(this._ID,"ERROR",s);
            }
        }catch (Exception e){
            e.printStackTrace();
            idtct.onProgress(this._ID,"EXCEPTION",e.getMessage());
        }
        //idtct.onComplete(this._ID,this.cmd,lastOutput);
    }
    private void RunCommandOnCMD(String command){
        try {
            lastOutput = "";
            lastError = "";
            String[] args = {"cmd.exe","/K"};
            //String commands = "ipconfig && ping google.com";
            //String adb = "\"D:\\Program Files\\Nox\\bin\\adb.exe\"";
            //String commands = adb + " devices && " + adb + " shell logcat" ;
            ProcessBuilder processBuilder = new ProcessBuilder(args);
            if(!this.startDir.equals("")) {
                processBuilder.directory(new File(this.startDir));
            }
            final Process proc = processBuilder.start();
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            BufferedWriter stWriter= new BufferedWriter(new OutputStreamWriter(proc.getOutputStream()));
            String s;
            stWriter.write(this.cmd);
            stWriter.newLine();
            stWriter.flush();
            stWriter.close();
            while ((s = stdInput.readLine()) != null) {
                t.append(s + "\n");
                lastOutput += s + "\n";
                idtct.onProgress(this._ID,"OUTPUT",s);
            }

            while ((s = stdError.readLine()) != null) {
                t.append(s + "\n");
                lastError += s + "\n";
                idtct.onProgress(this._ID,"ERROR",s);
            }
        }catch (Exception e){
            e.printStackTrace();
            idtct.onProgress(this._ID,"EXCEPTION",e.getMessage());
        }
        //idtct.onComplete(this._ID,this.cmd,lastOutput);
    }

}
