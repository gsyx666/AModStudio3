package mbpcm;

import brut.androlib.meta.MetaInfo;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.yaml.snakeyaml.Yaml;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.nio.file.Files.walk;

public class splitMerger {
    /* ERRORS:
    * 1. You Need To Define AppCompat.Theme... => Garuda. because styles.xml in drawable-xhdpi of split-xhdpi.apk.
    * Lession - do not copy styles.xml, base.apk has enaugh styles
    * 2. Failed to capture screenshot... app exit without warning.. => initFirebase() caused the problem.
    * Lession - remove Firebase from app. we hackers never need it.
    *
    *
    *
    * */
    public static void MergeSplitAPK(String basePath,String[] otherFolders){

        System.out.println("Input basePath: " + basePath + " OtherFolders :" + otherFolders);

        String publicXmlpart = "\\res\\values\\public.xml";
        String manifest = "\\AndroidManifest.xml";
        String yaml = "\\apktool.yml";

        HashMap<String, String> id_dummy = parseMissingIDs(basePath + publicXmlpart);
        HashMap<String, String> id_names = parsePublicXML(basePath + publicXmlpart);

        if(id_names==null || id_dummy ==null){
            System.out.println("Weired Error:  Null Maps: id_names or id_dummy");
            return;
        }

        brut.androlib.meta.MetaInfo data_dst = loadYaml(basePath + yaml);

        for(String splitFolder: otherFolders){
            copyFolders(basePath,splitFolder);
            copyLibs(basePath,splitFolder);
            // Merging Yaml
            brut.androlib.meta.MetaInfo data_src = loadYaml(splitFolder + yaml);
            data_dst.doNotCompress.addAll(data_src.doNotCompress);

            HashMap<String,String> tmp = parseMissingIDs(splitFolder + publicXmlpart);
            if(tmp!=null){id_dummy.putAll(tmp);}else{System.out.println("No Public.xml Found in :" + splitFolder);}

            HashMap<String,String> tmp2 = parsePublicXML(splitFolder + publicXmlpart);
            if(tmp2!=null){id_names.putAll(tmp2);}else{System.out.println("No Public.xml Found in :" + splitFolder);}
        }

        // saving yaml for
        saveYaml(data_dst,basePath + yaml);
        HashMap<String,String> dummy_names = AnalyzeAndCreateDummyNameMap(id_dummy,id_names);

        // getting list of xml files in base folder
        Path[] xmls = getAllBaseXMLs(basePath);
        if(xmls==null){return;}


        //replacing dummy values in xml
        for(Path xml : xmls){
            replaceAllDummies(xml,dummy_names);
        }


        //patching manifest..
        patchManifest(basePath + manifest);

        //checking for remaining dummy values because of missing ids.
        System.out.println("\n\n ================ Missing Report ==================");
        for(Path xml : xmls){
           getDummyLines(xml);
        }

 //*/

    }
    public static brut.androlib.meta.MetaInfo loadYaml(String filepath){
        try {
            FileInputStream is = new FileInputStream(filepath);
            brut.androlib.meta.MetaInfo ret = new Yaml().load(is);
            is.close();
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            return new MetaInfo();
        }
    }
    public static void saveYaml(brut.androlib.meta.MetaInfo doc,String filepath){
        try {
            doc.save(new File(filepath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static HashMap<String,String> AnalyzeAndCreateDummyNameMap(HashMap<String,String> id_dummy, HashMap<String,String> id_names){
        HashMap<String,String> dummy_names = new HashMap<>();

        int missing = 0;
        int found = 0;
        System.out.println("size id_dummy: " + id_dummy.size() + "    size id_names: " + id_names.size());
        for(Map.Entry<String,String> entry: id_dummy.entrySet()){
            String key = entry.getKey();
            String val = entry.getValue();

            if(id_names.containsKey(key)){
                dummy_names.put(val,id_names.get(key));
                found ++;
            }else{
                System.out.println("**** Missing ID: " + key + " : " + val);
                missing ++;
            }
        }

        System.out.println("\nFound: " + found + " \tMissing:" + missing + " \tExtra:" + (id_names.size() - id_dummy.size()));
        return dummy_names;
    }



    public static String[] enumFolders(String filepath){
        List<String> outlist = new ArrayList<>();
        try(Stream<Path> walk =  Files.walk(Paths.get(filepath),1)) {
            walk
                    .filter(Files::isDirectory)
                    .forEach(path->outlist.add(path.toString()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String[] out = new String[outlist.size()];
        return outlist.toArray(out);
    }
    public static void copyFolders(String baseFolder,String splitFolder){
        //TODO: Current Method OverWrites the destination files, lets hope that destination files are unique.
        // Attention: just try to merge Files. do not overwrite files if already exists.
        File srcDir = new File(baseFolder);
        String srcResPath = splitFolder + "\\res";
        if(!new File(srcResPath).exists()){
            System.out.println("Info : copyFolders : No Resource Folders To Copy in :" + splitFolder);
            return;
        }
        String[] folders = enumFolders(srcResPath);
        for(String f:folders){
            if(f.equals(srcResPath)){continue;} //first item of folders is self.
            String file_name = Paths.get(f).getFileName().toString();
            if(!file_name.equals("values")){ // all other files including drawable*** and values-**
                File src = new File(f);
                File dest = new File(baseFolder + "\\res\\" + file_name);
                if(dest.exists()){
                    System.out.println("Already Exists: " + dest.getName());
                }
                try {
                    FileUtils.copyDirectory(src,dest);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static void copyLibs(String baseFolder,String splitFolder){
        String libSrc = splitFolder + "\\lib";
        String libDst = baseFolder + "\\lib";
        File srcLib = new File(libSrc);
        File dstLib = new File(libDst);
        // if src lib folder exists then make sure dest lib folder exists too.
        if(srcLib.exists()){
            if(!dstLib.exists()){
                if(!dstLib.mkdir()){
                    System.out.println("Cannot make destination lib folder");
                    return;
                }
            }
        }else{
            return; //No lib folder.
        }
        //lets copy.
        for(String f: Objects.requireNonNull(srcLib.list())){ //f is just a foldername, not path, its like armaabi64..
            File src = new File(libSrc + "\\" + f );
            File dest = new File(libDst + "\\" + f);
            if(dest.exists()){
                System.out.println("Already Exists: " + dest.getName());
            }
            try {
                FileUtils.copyDirectory(src,dest);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static HashMap<String,String> parsePublicXML(String public_xml_path){
        File inputFile = new File(public_xml_path);
        if(!inputFile.exists()){return null;}
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputFile);
            doc.getDocumentElement().normalize();
            Node root = doc.getDocumentElement();
            NodeList nList = root.getChildNodes();
            int len = nList.getLength();
            Node nNode;
            HashMap<String,String> ids = new HashMap<>();
            for (int i = 0; i< nList.getLength(); i++) {
                nNode = nList.item(i);
                if(nNode.hasAttributes()) {
                    String id = nNode.getAttributes().getNamedItem("id").getNodeValue();
                    String name = nNode.getAttributes().getNamedItem("name").getNodeValue();

                    if (!name.startsWith("APKTOOL_DUMMY_")) {
                        //System.out.println(id + " : " + name);
                        ids.put(id, name);
                    }
                }
            }
            System.out.println("------------------------------\n\n");
            return ids;
        }catch (Exception exeption){
            exeption.printStackTrace();
            return null;
        }
    }
    public static HashMap<String,String> parseMissingIDs(String base_public_xml_path){
        File inputFile = new File(base_public_xml_path);
        if(!inputFile.exists()){return null;}
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputFile);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("public");
            Node nNode;
            HashMap<String,String> ids = new HashMap<>();
            for (int i = 0; i< nList.getLength(); i++) {
                nNode = nList.item(i);
                String id = nNode.getAttributes().getNamedItem("id").getNodeValue();
                String name = nNode.getAttributes().getNamedItem("name").getNodeValue();

                if(name.startsWith("APKTOOL_DUMMY_")) {
                    ids.put(id,name);
                }
            }
            return ids;
        }catch (Exception exeption){
            exeption.printStackTrace();
            return null;
        }
    }
    public static Path[] getAllBaseXMLs(String basepath){
        String resFolder = basepath + "\\res";
        java.util.List<Path> baseXMLfiles = new ArrayList<>();
        try {
            walk(Paths.get(resFolder))
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith(".xml"))
                    .forEach(baseXMLfiles::add);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        Path[] out = new Path[baseXMLfiles.size()];
        return baseXMLfiles.toArray(out);
    }

    public static void getDummyLines(Path path) {
        try {
            String text = Files.readString(path);
            boolean firstTime = true;
            String[] data = text.split("\n");
            if(data.length==0){return;}
            for(String s: data){
                if(s.contains("APKTOOL_DUMMY_")){
                    if(firstTime){
                        firstTime = false;
                        System.out.println(path);
                    }
                    System.out.println("\t\t" + s);
                }
            }
        } catch (Exception e) {
            System.out.println("getDummyLines() :: Error Reading file " + path);
        }
    }
    public static void replaceAllDummies(Path path,HashMap<String,String> dummy_name){
        try {
            String text = Files.readString(path);
            Set<String> dummies = new HashSet<>();
            String REGEX = "APKTOOL_DUMMY_[a-f\\d]{1,4}[\"<]";
            Pattern pattern = Pattern.compile(REGEX);
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                dummies.add(matcher.group());
            }
            if(dummies.size()==0){
                return;
            }
            int count = 0;
            for(String dummy:dummies){
                int len1 = dummy.length()-1;
                String lastChar = dummy.substring(len1);
                String dummy_last_char_removed = dummy.substring(0,len1);
                if(!dummy_name.containsKey(dummy_last_char_removed)){
                    System.out.println("******* Missing Value Reference : " + dummy + " : " + path );
                    continue;
                }
                text = text.replace(dummy,dummy_name.get(dummy_last_char_removed) + lastChar);
                count++;
            }
            System.out.println(count + " Replacements in : " + path);
            Files.writeString(path,text);
        } catch (Exception e) {
            e.printStackTrace();
            //System.out.println("getDummyLines() :: Error Reading file " + path);
        }
    }
    public static void patchManifest(String filepath){
        try{
            Path path_manifest = Path.of(filepath);
            String manifest = Files.readString(path_manifest);
        String new_manifest = manifest.replace("android:extractNativeLibs=\"false\"", "")
                .replace("android:isSplitRequired=\"true\"", "")
                .replace("<meta-data android:name=\"com.android.vending.splits\" android:resource=\"@xml/splits0\"/>", "")
                .replace("<meta-data android:name=\"com.android.vending.splits.required\" android:value=\"true\"/>", "");
        Files.writeString(path_manifest,new_manifest);
        }catch (Exception e){
            System.out.println(" Error : Cant Read or Write Manifest.xml");
            e.printStackTrace();
        }
    }
    public static void mergeYaml(String dest,String src) throws IOException {
        brut.androlib.meta.MetaInfo data_src = new Yaml().load(new FileInputStream(src));
        brut.androlib.meta.MetaInfo data_dst = new Yaml().load(new FileInputStream(dest));
        data_dst.doNotCompress.addAll(data_src.doNotCompress);
        data_dst.save(new File(dest));
    }
}
