package mbpcm.smaliIndexer;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IndexMakerService {
    public static void main(String[] args) {
        new IndexMakerService("\"H:\\\\splitApks\\\\Garuda [v4.0.7]-split\\\\base\\\\smali_classes2\"");
    }
    IndexMakerService(String basepath){
        //bugExperiment();
        walkAll(basepath);
    }
    void bugExperiment(){
        String filepath = "H:\\splitApks\\Garuda [v4.0.7]-split\\base\\smali_classes2\\com\\squareup\\okhttp\\internal\\framed\\Settings.smali";
        new smaliParser(filepath);
    }
    void walkAll(String basepath){
        JsonArrayBuilder database = Json.createArrayBuilder();
        try {
            Files.walk(Paths.get(basepath))
                    .filter(p -> p.toString().endsWith(".smali"))
                    .forEach(p ->{
                        database.add(new smaliParser(p.toString()).c.build());
                    });
            Files.writeString(Paths.get("H:\\smaliDatabase.txt"),database.build().toString());
            System.out.println("DONE");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    static class smaliParser{
        String mFilePath = "";
        List<String> lines;
        public JsonObjectBuilder c = Json.createObjectBuilder();
        JsonArrayBuilder methods = Json.createArrayBuilder();
        JsonArrayBuilder fields = Json.createArrayBuilder();
        JsonArrayBuilder implementsI = Json.createArrayBuilder();
        smaliParser(String filepath){
            c.add("filepath",filepath);
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
            c.add("methods",methods.build());
            c.add("fields",fields.build());
            c.add("implements",implementsI.build());
        }
        private void printBlock(String name,int start,int end){
            switch (name){
                case "method" -> {
                   List<String> ret = parseByRegex(start,".method\\s+(.*)\\s+(.*?)\\((.*?)\\)(\\[{0,3}L(.*?);|\\[{0,3}[A-Z]{1})",5);
                   if(ret!=null){
                       String body = "";
                       for(int i= start+1;i<end;i++){
                           if(!lines.get(i).equals("")) {
                               body += lines.get(i);
                           }
                       }
                       JsonObjectBuilder method = Json.createObjectBuilder();
                       method.add("name",ret.get(1));
                       method.add("access",ret.get(0));
                       method.add("inputs",ret.get(2));
                       method.add("output",ret.get(3));

                       JsonArray apis = parseByRegex(body,"L(\\S*?);(->(\\S*?)([(:]))?",0);
                       JsonArray strs = parseByRegex(body,"\"(.+?)\"",1);
                       JsonArray longs = parseByRegex(body,"-?0x[a-fA-F0-9]{2,}L",0);
                       method.add("apis",apis);
                       method.add("strings",strs);
                       method.add("longs",longs);
                       methods.add(method.build());

                   }else{
                       System.out.println("[Line " + start + "] Invalid Method Declaration:" + lines.get(start));
                   }
                }
                case "field" -> {
                    List<String> ret = parseByRegex(start,".field\\s+(.*)\\s+(.*):(\\[{0,3}L(.*?);|\\[{0,3}[A-Z]{1})(\\s+=\\s+(.*))?",6);
                    if(ret!=null){
                        String body = "";
                        if(start!=end) {
                            for (int i = start + 1; i < end; i++) {
                                if (!lines.get(i).equals("")) {
                                    body += lines.get(i);
                                }
                            }
                        }
                        JsonObjectBuilder field = Json.createObjectBuilder();
                        field.add("name",ret.get(1));
                        field.add("access",ret.get(0));
                        field.add("value",ret.get(2));
                        fields.add(field.build());

                    }else{
                        System.out.println("[Line " + start + "] Invalid Field Declaration:" + lines.get(start) + mFilePath);
                    }
                }
                case "source" -> {
                    List<String> ret = parseByRegex(start,".source\\s+\\\"((.*?))\\\"",2);
                    if(ret!=null){
                        c.add("src",ret.get(0));
                    }else{
                        System.out.println("[Line " + start + "] Invalid Source Declaration" + lines.get(start));
                    }
                }
                case "class" -> {
                    List<String> ret = parseByRegex(start,".class(.*)\\s+L((.*));",3);
                    if(ret!=null){
                        c.add("access_mod",ret.get(0).trim());
                        c.add("name",ret.get(1));
                    }else{
                        System.out.println("[Line " + start + "] Invalid Class Declaration:" + lines.get(start));
                    }
                }
                case "super" -> {
                    List<String> ret = parseByRegex(start,".super\\s+L((.*));",2);
                    if(ret!=null){
                        c.add("super",ret.get(0));
                    }else{
                        System.out.println("[Line " + start + "] Invalid extends Declaration:" + lines.get(start));
                    }
                }
                case "implements" -> {
                    List<String> ret = parseByRegex(start,".implements\\s+L((.*?));",2);
                    if(ret!=null){
                        implementsI.add(ret.get(0));
                    }else{
                        System.out.println("[Line " + start + "] Invalid Interface Declaration:" + lines.get(start));
                    }
                }
            }
        }
        private JsonArray parseByRegex(String content, String regex, int group){
            Set<String> apiSet = new HashSet<>();
            JsonArrayBuilder apis = Json.createArrayBuilder();
            Pattern pattern_api = Pattern.compile(regex);
            Matcher matcher_api = pattern_api.matcher(content);
            while(matcher_api.find()){
                apiSet.add(matcher_api.group(group));
            }
            for(String s : apiSet){
                apis.add(s);
            }
            return apis.build();
        }
        private List<String> parseByRegex(int lineNo, String regex, int expectedGroups){
            List<String> out = new ArrayList<>();
            String line = lines.get(lineNo);
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(line);
            boolean matchess = matcher.matches();
            int gcount = matcher.groupCount();
            if(matchess && gcount==expectedGroups) {
                for (int i = 1; i < expectedGroups; i++) {
                    out.add(matcher.group(i));
                }
                return out;
            }else{
                System.out.println("group Count:" + matcher.groupCount() + "  matched : " + matcher.matches());
                if(matcher.matches()) {
                    for (int i = 0; i < matcher.groupCount(); i++) {
                        System.out.println("Group" + i + " : " + matcher.group(i));
                    }
                }
                return null;
            }
        }
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