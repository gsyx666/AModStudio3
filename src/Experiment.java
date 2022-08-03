import com.formdev.flatlaf.FlatDarkLaf;
import mbpcm.ui.IButton;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Experiment {

    public static void main(String[] args) {
        FlatDarkLaf.setup();
        new Experiment();

    }
    Experiment(){
        //String file = "H:\\splitApks\\MoviesFree [1.2]-split\\base\\smali_classes4\\com\\google\\android\\gms\\measurement\\internal\\zzkn.smali";
        //String file = "H:\\splitApks\\Send Files To TV [1.2.2]-split\\base\\smali\\com\\google\\android\\gms\\internal\\measurement\\zzjk.smali";
        String file = "G:\\portableApps\\Hacking & Programming\\APK Easy Tool portable\\1-Decompiled APKs\\base\\smali\\androidx\\exifinterface\\media\\ExifInterface.smali";
        new smaliParser(file);
    }
    static class smaliParser{
        String mFilePath = "";
        List<String> lines;
        smaliParser(String filepath){
            System.out.println("Started...");
            mFilePath = filepath;
            try {
                lines = Files.readAllLines(Paths.get(mFilePath));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if(lines.size()==0){return;}
            String expect = null;
            int startline = 0;
            String type = "";
            int index = 0;
            boolean optional = false;
            String st = ".field,.method,.super,.source,.interface,.implements,.class";
            for(String line:lines){
                index ++; //starts from 1
                line = line.trim();
                if(line.equals("")){continue;}
                String[] words = line.split("\\s");
                if(expect!=null){
                    if(line.startsWith(expect)){
                        printBlock(type,startline,index);
                        expect = null;
                        type = null;
                        startline = 0;
                        continue;
                    }else if(optional && st.contains(words[0] + ",")){
                        printBlock(type,startline,startline);
                        expect = null;
                        type = null;
                        startline = 0;
                    }
                }
                switch (words[0]){
                    case ".method" -> {
                        expect = ".end method";
                        type="method";
                        optional = false;
                        startline=index;
                    }
                    case ".field" -> {
                        expect = ".end field";
                        type="field";
                        optional = true;
                        startline=index;
                    }
                    case ".annotation" -> {
                        if(expect==null) { //because it is also internal directive.
                            expect = ".end annotation";
                            type = "annotation";
                            optional = false;
                            startline = index;
                        }
                    }
                    case ".class" -> {printBlock("class",index,index);}
                    case ".super" -> {printBlock("extends",index,index);}
                    case ".source" -> {printBlock("source",index,index);}
                    case ".implements" -> {printBlock("implements",index,index);}
                    case ".interface" -> {printBlock("interface",index,index);}
                    default -> {
                        if(expect==null) {
                            if(!line.startsWith("#")) {
                                System.out.println("UnExpected Word " + words[0]);
                            }
                        }
                    }
                }
            }
        }
        private void printBlock(String name,int start,int end){
            /*
            * Errors:
            * 1. More than one occurance of .class etc.
            * 2. Method filed name special characters.
            *
            *
            * */
            System.out.println("===" + name + " start:" + start + "  end: " + end);
        }
    }
}