import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class splitMergeErrorSolutions {
    String mBasePath;
    String[] resFolders;
    String[] remainingResFolders;
    public static void main(String[] args){
        new splitMergeErrorSolutions("H:\\splitApks\\Garuda [v4.0.7]-split\\base\\res");
    }
    splitMergeErrorSolutions(String basepath){
        mBasePath = basepath;
        resFolders = listResFoldersForHDPI(basepath);
        startFinding(basepath);     //solution: resource not found for HDPI devices such as Nox Emulator.
        //solution: drawable already defined.. double defined drawables.
        for(String vals: remainingResFolders){
            if(vals.contains("values")) {
                checkAndCleanValuesDrawable(basepath, vals);
            }
        }
        for(String vals: resFolders){
            if(vals.contains("values")) {
                checkAndCleanValuesDrawable(basepath, vals);
            }
        }

    }
    private void startFinding(String basepath){

        String drawableFolder = basepath + "\\drawable\\";
        String[] layoutFiles = enumFolders(basepath + "\\layout");
        HashMap<String,Boolean> done = new HashMap<>();
        Set<String> types = new HashSet<>();
        for(String layout: layoutFiles){
            //System.out.println(layout);
            try {
                String file_content = Files.readString(Paths.get(layout), StandardCharsets.UTF_8);
                Pattern pattern = Pattern.compile("\"@([a-z:]+)/(.+?)\""); //find "@xxxxx/yyyyy"
                Matcher matcher = pattern.matcher(file_content);
                while (matcher.find()) { //for each regex match
                    String folder = matcher.group(1);
                    types.add(folder);
                    String file = matcher.group(2);
                    if(folder.startsWith("android:")){
                        System.out.println(folder + "\\" + file);
                    }
                    if(folder.equals("drawable")){
                        if(!done.containsKey(file)){
                            if(!resExistsForHDPI(file)){
                                done.put(file,false);
                                //System.out.println("Error: drawable not found in drawable folder- " + file + " in " + layout);
                                //TODO: find these missing files in other folders and then copy + remove from values/drawables.xml
                                String missingSRC = findMissingRes(file);
                                if(missingSRC!=null){
                                    System.out.println("Found Missing resource " + file + " as " + missingSRC);
                                    try {
                                        File destDir = new File(drawableFolder);
                                        File srcFile = new File(missingSRC);
                                        FileUtils.copyFileToDirectory(srcFile, destDir);
                                    } catch(Exception e) {
                                        System.out.println("ERROR: But was Unable To Copy it to " + drawableFolder + "\nPlease Copy it manually");
                                    }
                                }else{
                                    System.out.println("Resource " + file + " Not Found Anywhere in this project. its permanently missing");
                                }
                            }else{
                                done.put(file,true);
                                //System.out.println("FOUND: " + file);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println(types);
    }
    public void checkAndCleanValuesDrawable(String basepath,String valuesfolder){
        /* Concept:
        * The resources used From SDK are listed in values/drawables.xml
        * Decompiler puts missing non-SDK components in this list just because it cannot find it
        * thinking that it must be from SDK.(But they are not. they are in another part of split apk)
        *
        * so when we merge all apks, we must remove these entries from list.
        * this concept should be applying to all other xml files in values folder.
        * */
        //base path is res folder path.
        String drawables_path = valuesfolder + "drawables.xml";
        //System.out.println("CLEAN DRAWABLES: " + drawables_path);
        // read xml -> every entry -> check if file exists in HDPI folder, remove if exists.
        File inputFile = new File(drawables_path);
        if(!inputFile.exists()){return;}
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputFile);
            doc.getDocumentElement().normalize();
            Node root = doc.getDocumentElement();
            NodeList nList = root.getChildNodes();
            int len = nList.getLength();
            Node nNode;
            for (int i = 0; i< nList.getLength(); i++) {
                nNode = nList.item(i);
                if(nNode.hasAttributes()) {
                    String name = nNode.getAttributes().getNamedItem("name").getNodeValue();
                        if(resExistsForHDPI(name)){
                            nNode.getParentNode().removeChild(nNode); //remove it.
                            System.out.println("REMOVED From : " + drawables_path + "\t\t\t" + name);
                        }else{
                            String missing = findMissingRes(name);
                            if(missing!=null){
                                nNode.getParentNode().removeChild(nNode); //remove it.
                                //TODO: maybe, we must copy all findable res from drawables.xml to drawable folder.
                                System.out.println("REMOVED From : " + drawables_path + "\t\t\t" + name);
                            }
                        }
                }
            }
            writeXml(doc,new FileOutputStream(drawables_path));
            System.out.println("------------------------------\n\n");
        }catch (Exception exeption){
            exeption.printStackTrace();
        }
    }
    private static void writeXml(Document doc, OutputStream output) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        //transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //pretty print
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(output);
        transformer.transform(source, result);
    }
    public String findMissingRes(String res){
        //TODO: first find in low resoulution folders. then in high.
        for(String folder: remainingResFolders){
            String[] ext  = new String[]{".png",".xml",".9.png",".jpg"};
            for(String extension:ext) {
                String fullpath = folder + res + extension;
                if(new File(fullpath).exists()){
                    return fullpath;
                }
            }
        }
        return null;
    }
    public boolean resExistsForHDPI(String resName){
        for(String resF:resFolders){
            if(resExists(resF,resName)){
                return true;
            }
        }
        return false;
    }
    public static boolean resExists(String folder,String resName){ //TODO: can be used to check duplicate files ie pic.png and pic.jpg in same folder.
        String[] ext  = new String[]{".png",".xml",".9.png",".jpg"};
        boolean ret = false;
        for(String extension:ext) {
            if(new File(folder + resName + extension).exists()){
               if(ret){
                   System.out.println("same resources with different extension in same folder: " + resName);
               }else{
                   ret = true;
               }
            }
        }
        return ret;
    }
    public static String[] enumFolders(String filepath){
        List<String> outlist = new ArrayList<>();
        try(Stream<Path> walk =  Files.walk(Paths.get(filepath),1)) {
            walk
                    .filter(Files::isRegularFile)
                    .forEach(path->outlist.add(path.toString()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String[] out = new String[outlist.size()];
        return outlist.toArray(out);
    }
    public String[] listResFoldersForHDPI(String resFolderPath){
        List<String> out = new ArrayList<>();
        List<String> rest = new ArrayList<>();
        try {
            Files.list(new File(resFolderPath).toPath())
                    .forEach(path -> {
                        String p = path.toString();
                        if(p.contains("-hdpi") || p.contains("-mdpi") || p.contains("ldpi") || p.contains("-nodpi") || p.contains("-anydpi") || p.endsWith("\\drawable")){
                            out.add(path.toString() + "\\");
                        }else{
                            rest.add(path.toString() + "\\");
                        }
                    });
            String[] o = new String[out.size()];
            remainingResFolders = new String[rest.size()];
            rest.toArray(remainingResFolders);
            return out.toArray(o);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
