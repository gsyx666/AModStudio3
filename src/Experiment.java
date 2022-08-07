import com.formdev.flatlaf.FlatDarkLaf;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Experiment {

    public static void main(String[] args) {
        FlatDarkLaf.setup();
        new Experiment();

    }
    Experiment(){
        //String file = "H:\\splitApks\\MoviesFree [1.2]-split\\base\\smali_classes4\\com\\google\\android\\gms\\measurement\\internal\\zzkn.smali";
        //String file = "H:\\splitApks\\Send Files To TV [1.2.2]-split\\base\\smali\\com\\google\\android\\gms\\internal\\measurement\\zzjk.smali";
        String file = "G:\\portableApps\\Hacking & Programming\\APK Easy Tool portable\\1-Decompiled APKs\\base\\smali\\androidx\\exifinterface\\media\\ExifInterface.smali";
        file = "C:\\Users\\MbPCM\\ApkProjects\\pikashow\\smali\\out\\s74.smali";
        new smaliParser(file);
    }
    static class smaliParser{
        String mFilePath = "";
        List<String> lines;
        Cls cls = new Cls();
        smaliParser(String filepath){
            cls.filepath = filepath;
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
            int index = -1;
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
                    case ".super" -> {printBlock("super",index,index);}
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
            //System.out.println();
            //System.out.println("===" + name + " start:" + start + "  end: " + end + "  " + lines.get(start));
            switch (name){
                case "method" -> {
                   List<String> ret = parseByRegex(start,".method\\s+(.*)\\s+(.*?)\\((.*?)\\)(L(.*?);|[A-Z]{1})",5);
                   if(ret!=null){
                       String body = "";
                       for(int i= start+1;i<end;i++){
                           if(!lines.get(i).equals("")) {
                               body += lines.get(i);
                           }
                       }
                       Methods methods = new Methods(ret.get(0),ret.get(1),ret.get(2),ret.get(3),body);
                       cls.methods.add(methods);
                       //System.out.println("Method:" + ret.get(1));
                   }else{
                       System.out.println("[Line " + start + "] Invalid Method Declaration:" + lines.get(start));
                   }
                }
                case "field" -> { //TODO: how a field appear in smali if it is given an initial value?
                    List<String> ret = parseByRegex(start,".field\\s+(.*)\\s+(.*):(L(.*?);|[A-Z]{1})",4);
                    if(ret!=null){
                        String body = "";
                        if(start!=end) {
                            for (int i = start + 1; i < end; i++) {
                                if (!lines.get(i).equals("")) {
                                    body += lines.get(i);
                                }
                            }
                        }
                        Fields fields = new Fields(ret.get(0),ret.get(1),ret.get(2),body);
                        cls.fields.add(fields);
                        //System.out.println("Field:" + ret.get(1));
                    }else{
                        System.out.println("[Line " + start + "] Invalid Field Declaration:" + lines.get(start));
                    }
                }
                case "source" -> {}
                case "class" -> {
                    List<String> ret = parseByRegex(start,".class(.*)\\s+L((.*));",3);
                    if(ret!=null){
                        cls.access_mod = ret.get(0);
                        cls.clsName = ret.get(1);
                        System.out.print(ret.get(0).trim() + " class " + ret.get(1) + " ");
                    }else{
                        System.out.println("[Line " + start + "] Invalid Class Declaration:" + lines.get(start));
                    }
                }
                case "super" -> {
                    List<String> ret = parseByRegex(start,".super\\s+L((.*));",2);
                    if(ret!=null){
                        cls.extend = ret.get(0);
                        System.out.print(" extends " + ret.get(0).replace('/','.'));
                    }else{
                        System.out.println("[Line " + start + "] Invalid extends Declaration:" + lines.get(start));
                    }
                }
                case "implements" -> {}
            }
        }
        private List<String> parseByRegex(int lineNo, String regex, int expectedGroups){
            List<String> out = new ArrayList<>();
            String line = lines.get(lineNo);
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(line);
            if(matcher.matches() && matcher.groupCount()==expectedGroups) {
                for (int i = 1; i < expectedGroups; i++) {
                    out.add(matcher.group(i));
                }
                return out;
            }else{
                System.out.println("group Count:" + matcher.groupCount() + "  matched : " + matcher.matches());
                for(int i=0;i<matcher.groupCount();i++){
                    System.out.println("Group" + i + " : " + matcher.group(i));
                }
                return null;
            }
        }
    }
    static class Methods {
        public String methodName;
        public String access;
        public String inputs;
        public String output;
        public String body;
        Methods(String methodName_, String access_spec, String inputs_, String output_, String body_){
            methodName = methodName_;
            access = access_spec;
            inputs = inputs_;
            output = output_;
            body = body_;
        }
    }
    static class Fields {
        public String Name;
        public String access;
        public String value;
        public String body;
        Fields(String Name_, String access_spec, String value_, String body_){
            Name = Name_;
            access = access_spec;
            value = value_;
            body = body_;
        }
    }
    static class Cls{
        public String filepath;
        public String extend;
        public String clsName;
        public String source;
        public String comments;
        public String access_mod;
        public boolean isInterface;
        public List<String> implement = new ArrayList<>();
        public List<Fields> fields = new ArrayList<>();
        public List<Methods> methods = new ArrayList<>();
    }
    /*
     public static int getAcc(String name) {
        if (name.equals("public")) {
            return ACC_PUBLIC;
        } else if (name.equals("private")) {
            return ACC_PRIVATE;
        } else if (name.equals("protected")) {
            return ACC_PROTECTED;
        } else if (name.equals("static")) {
            return ACC_STATIC;
        } else if (name.equals("final")) {
            return ACC_FINAL;
        } else if (name.equals("synchronized")) {
            return ACC_SYNCHRONIZED;
        } else if (name.equals("volatile")) {
            return ACC_VOLATILE;
        } else if (name.equals("bridge")) {
            return ACC_BRIDGE;
        } else if (name.equals("varargs")) {
            return ACC_VARARGS;
        } else if (name.equals("transient")) {
            return ACC_TRANSIENT;
        } else if (name.equals("native")) {
            return ACC_NATIVE;
        } else if (name.equals("interface")) {
            return ACC_INTERFACE;
        } else if (name.equals("abstract")) {
            return ACC_ABSTRACT;
        } else if (name.equals("strict")) {
            return ACC_STRICT;
        } else if (name.equals("synthetic")) {
            return ACC_SYNTHETIC;
        } else if (name.equals("annotation")) {
            return ACC_ANNOTATION;
        } else if (name.equals("enum")) {
            return ACC_ENUM;
        } else if (name.equals("constructor")) {
            return ACC_CONSTRUCTOR;
        } else if (name.equals("declared-synchronized")) {
            return ACC_DECLARED_SYNCHRONIZED;
        }
        return 0;
        case 'V':
                case 'Z':
                case 'C':
                case 'B':
                case 'S':
                case 'I':
                case 'F':
                case 'J':
                case 'D':
    }
     */
}