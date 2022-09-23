import com.formdev.flatlaf.FlatDarculaLaf;
import mbpcm.smaliIndexer.Indexer;
import mbpcm.ui.I_Window;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//DONE: Method Rename
//DONE: Field Rename
//DONE: CLASS Rename
//DONE: Class Move path

//TODO: use gson instead of javax.json : will make updating database faster and memory efficient.
//TODO: package renaming and deletion.
//TODO: TreeView Refresh
//TODO: AndroidManifest activity rename on class name change.
//TODO: how we will remove data of oldFile from db?
//TODO: Better UI

public class Renamer implements I_Window {
    Indexer indexer;
    JsonArray db;
    String dbPath;
    JFrame f;
    JTextPane LBLselectedText;
    JTextField textField;
    Editor editor_;
    String selectedTextWithClassName;

    public static void main(String[] args){
        new Renamer();
    }
    Renamer(){
    }
    Renamer(Editor editor){
        editor_ = editor;
        FlatDarculaLaf.setup();
    }


    private JsonObject getClassObjByFilePath(String path){
        int size = db.size();
        int clsNo = -1;
        for(int i=0;i<size;i++) {
            JsonObject smali = db.getJsonObject(i);
            String filepath = smali.getString("filepath");
            if(path.equals(filepath)){
                clsNo = i;
                break;
            }
        }
        if(clsNo<0){
            System.out.println("Class Not Found in Database");
            return null;
        }
        return db.getJsonObject(clsNo);
    }

    private void prepareRenamingEnvironment(String selectedTextWithClassNamee){
        //load jsonDatabase.

        indexer = Indexer.getInstance();
        db = indexer.db;

        dbPath = editor_.vars.get("project") + "\\searchIndex.txt";
        //loadJSONDatabase(dbPath);

        //recognize replacement type: class/method/field

        selectedTextWithClassName = selectedTextWithClassNamee;
        System.out.println(selectedTextWithClassName);
        //call appropriate method to replace
        createUI();
        LBLselectedText.setText(selectedTextWithClassNamee);
        textField.setText(selectedTextWithClassName);
        f.setVisible(true);

    }
    @Override
    public JComponent getWindow() {
        return null;
    }

    @Override
    public JToggleButton getButton() {
        return null;
    }

    @Override
    public String getWindowName() {
        return null;
    }

    @Override
    public int getPrefPosition() {
        return 0;
    }

    @Override
    public void onSettingChanged(String a, String b, Object c) {
        if(a.equals("action_rename")){
            prepareRenamingEnvironment(b);
        }
    }
    public void createUI(){
        f = utils.createBasicWindow("Renamer",0.5,0.5);
        f.setLayout(new BorderLayout());
        f.setSize(new Dimension(500,300));
        JPanel box = new JPanel(new BorderLayout());
        Font font  = box.getFont().deriveFont(Font.BOLD,14);


        LBLselectedText = new JTextPane();
        LBLselectedText.setEditable(false);
        LBLselectedText.setText("firstLine\nSecondLine");
        JLabel editLabel = new JLabel("New Name: ");

        textField = new JTextField();
        JButton doit = new JButton("DO IT");
        JProgressBar pgb  = new JProgressBar();

        LBLselectedText.setFont(font);
        editLabel.setFont(font);
        textField.setFont(font);

        textField.setMinimumSize(new Dimension(600,30));
        textField.setPreferredSize(new Dimension(600,30));
        textField.setBorder(new LineBorder(Color.BLACK));
        textField.setBackground(Color.BLACK);
        textField.setForeground(Color.YELLOW);
        doit.setMaximumSize(new Dimension(100,Integer.MAX_VALUE));
        pgb.setMaximumSize(new Dimension(800,4));
        pgb.setIndeterminate(true);
        pgb.setVisible(false);
        doit.setAlignmentX(Component.RIGHT_ALIGNMENT);
        doit.addActionListener(e -> {
            pgb.setVisible(true);
            String replaceWhat = selectedTextWithClassName;
            String replaceWith = textField.getText();
            CompletableFuture.runAsync(()-> rename(replaceWhat,replaceWith));

        });

        Box V = Box.createVerticalBox();
        V.add(LBLselectedText);
        V.add(Box.createVerticalStrut(10));

        Box H = Box.createHorizontalBox();
        H.add(editLabel);
        H.add(textField);

        Box h = Box.createHorizontalBox();
        h.add(doit);
        h.add(Box.createHorizontalStrut(10));
        h.add(pgb);



        f.add(V,BorderLayout.NORTH);
        f.add(H,BorderLayout.CENTER);
        f.add(h,BorderLayout.SOUTH);
        f.pack();
        f.setVisible(true);
    }

    private JsonObject getClassObjByClassName(String className){
        int size = db.size();
        int clsNo = -1;
        for(int i=0;i<size;i++) {
            JsonObject smali = db.getJsonObject(i);
            String clsName = smali.getString("name");
            if(clsName.equals(className)){
                clsNo = i;
                break;
            }
        }
        if(clsNo<0){
            System.out.println("Class Not Found in Database");
            return null;
        }
        return db.getJsonObject(clsNo);
    }
    public void replaceAllInFile(String filepath,String replaceWhat,String replaceWith){
        String data;
        try {
            Path pFilePath = Path.of(filepath);
            data = Files.readString(pFilePath, StandardCharsets.UTF_8);
            data = data.replace(replaceWhat,replaceWith);
            Files.writeString(pFilePath,data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public HashMap<Integer,String> findInAPIs(String what,boolean matchAll){
        HashMap<Integer,String> output = new HashMap<>();
        //find and replace in files and notedown affected files
        int size = db.size();
        for(int i=0;i<size;i++) {
            JsonObject objClass = db.getJsonObject(i);
            String FilePathOfRefClass = objClass.getString("filepath");
            JsonArray methodss = objClass.getJsonArray("methods");
            for(int j=0;j<methodss.size();j++) {
                JsonArray APIS = methodss.getJsonObject(j).getJsonArray("apis");
                for (int k = 0; k < APIS.size(); k++) {
                    String s = APIS.getString(k);
                    if(matchAll){
                        if(s.equals(what)){
                            output.put(i,FilePathOfRefClass);
                            System.out.println("Affected File: " + FilePathOfRefClass);
                            break; //the filename is enough, we will manually search all occurrences
                        }
                    }else{
                        if(s.startsWith(what)){
                            output.put(i,FilePathOfRefClass);
                            System.out.println("Affected File: " + FilePathOfRefClass);
                            break;
                        }
                    }
                }
            }
        }
        return output;
    }
    public boolean checkIfNameOccupied(String className,String name){

        JsonObject smali = getClassObjByClassName(className);
        if(smali==null){
            return true; //Error : name occupied => no further operations
        }

        return checkIfFieldExists(smali, name) || checkIfMethodExists(smali, name);
    }
    public boolean checkIfFieldExists(JsonObject jo, String name){
        //check Fields with same name.
        JsonArray fields = jo.getJsonArray("fields");
        for(int j=0;j<fields.size();j++){
            String f = fields.getJsonObject(j).getString("name");
            if(f.equals(name)){
                //System.out.println("Cannot Rename Because A Field with Same name already Exists");
                return true;
            }
        }
        return false;
    }
    public boolean checkIfMethodExists(JsonObject jo, String name){
        //check Method with Same Name
        JsonArray methods = jo.getJsonArray("methods");
        for(int j=0;j<methods.size();j++) {
            JsonObject method = methods.getJsonObject(j);
            String f = method.getString("name");
            if(f.equals(name)){
                //System.out.println("Cannot Rename Because A Method with Same name already Exists");
                return true;
            }
        }
        return false;
    }
    public void replaceMethodFieldInDeclaration(String filepath,String methodName,String newMethodName,boolean isMethod){
        String methodNameR;
        String methodNameN;
        String search;
        if(isMethod){
            methodNameR = " " + methodName;
            methodNameN = " " + newMethodName;
            search = ".method ";
        }else{
            methodNameR = " " + methodName;
            methodNameN = " " + newMethodName;
            search = ".field ";
        }

        String data;
        try {
            Path of = Path.of(filepath);
            data = Files.readString(of, StandardCharsets.UTF_8);
            String[] lines = data.split("\r\n");
            for(int m=0;m<lines.length;m++){
                String cLine = lines[m];
                if(cLine.trim().startsWith(search)){
                    if(cLine.contains(methodNameR)) {
                        String newLine = cLine.replace(methodNameR, methodNameN);
                        lines[m] = newLine;
                        break; //First Occurance only.. because method with same name should be declared only once.
                    }
                }
            }
            Files.writeString(of,String.join("\r\n",lines));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void moveClassToNewPath(String oldFilePath,String newFilePath){

        Path target = Path.of(newFilePath);
        try {
            Files.createDirectories(target.getParent());
            Files.move(Paths.get(oldFilePath), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void rename(String oldName,String newName){
        indexer = Indexer.getInstance();
        db = indexer.db;
        //Format for both names - Lclass;->field_or_method[(:]
        //compare to view changes.
        boolean classChanged = false;
        boolean NameChanged = false;
        String className_old;
        String fieldMethodName_old;
        String className_new;
        String fieldMethodName_new;

        boolean oldIsMethod = false;
        boolean newIsMethod = false;

        String[] oldd = oldName.split("->");
        String[] neww = newName.split("->");
        className_old = oldd[0];
        fieldMethodName_old = oldd[1];

        className_new = neww[0];
        fieldMethodName_new = neww[1];
        String filepath = "";
        JsonObject jo = getClassObjByClassName(className_old.trim());
        if(jo!=null){
            filepath = jo.getString("filepath");
        }
        if(!className_old.equals(className_new)){
            classChanged = true;
            //if contains "/" : check if path changed or its just name.
        }
        if(!fieldMethodName_old.equals(fieldMethodName_new)){
            NameChanged = true;
        }
        if(fieldMethodName_old.endsWith("(")){
            oldIsMethod = true;
        }
        if(fieldMethodName_new.endsWith("(")){
            newIsMethod = true;
        }

        if(NameChanged){
            if(oldIsMethod && newIsMethod){
                // check if already used.
                boolean b = checkIfNameOccupied(className_old,fieldMethodName_new);
                if (b){
                    System.out.println("Name already Exists as Field or Method Name, Cant Rename");
                }

                // change in method declaration.
                replaceMethodFieldInDeclaration(filepath,fieldMethodName_old,fieldMethodName_new,true);


                // change in affected files
                HashMap<Integer,String> hashMap = findInAPIs(oldName,true);
                for(Map.Entry<Integer, String> s: hashMap.entrySet()){
                    String aFilePath = s.getValue(); // affected filepath
                    replaceAllInFile(aFilePath,oldName,newName);
                }


                // change in database.
               indexer.updateDatabase(hashMap);
                sendRefreshSignal(hashMap);

            }else if(!oldIsMethod && !newIsMethod){
                // check if already used.
                boolean b = checkIfNameOccupied(className_old,fieldMethodName_new);
                if (b){
                    System.out.println("Name already Exists as Field or Method Name, Cant Rename");
                }

                // change in method declaration.
                replaceMethodFieldInDeclaration(filepath,fieldMethodName_old,fieldMethodName_new,false);


                // change in affected files
                HashMap<Integer,String> hashMap = findInAPIs(oldName,true);
                for(Map.Entry<Integer, String> s: hashMap.entrySet()){
                    String aFilePath = s.getValue(); // affected filepath
                    replaceAllInFile(aFilePath,oldName,newName);
                }


                // change in database.
               indexer.updateDatabase(hashMap);
                sendRefreshSignal(hashMap);
            }else{
                System.out.println("one is method and other is field.. cant replace method with field and field with method. why?");
            }
        }

        if(classChanged){

            String CLS_OLD_NO_SUFFIX_PREFIX;
            String clsOLD_path;
            String clsOLD_name;
            CLS_OLD_NO_SUFFIX_PREFIX = className_old.substring(1);
            CLS_OLD_NO_SUFFIX_PREFIX = CLS_OLD_NO_SUFFIX_PREFIX.substring(0,CLS_OLD_NO_SUFFIX_PREFIX.length()-1);
            if(className_old.contains("/")){
                clsOLD_name = CLS_OLD_NO_SUFFIX_PREFIX.substring(CLS_OLD_NO_SUFFIX_PREFIX.lastIndexOf("/")+1);
                clsOLD_path = CLS_OLD_NO_SUFFIX_PREFIX.substring(0,CLS_OLD_NO_SUFFIX_PREFIX.lastIndexOf("/")+1);
            }else{
                clsOLD_path = "";
                clsOLD_name = className_old;
            }

            String CLS_NEW_NO_SUFFIX_PREFIX;
            String clsNEW_path;
            String clsNEW_name;
            CLS_NEW_NO_SUFFIX_PREFIX = className_new.substring(1);
            CLS_NEW_NO_SUFFIX_PREFIX = CLS_NEW_NO_SUFFIX_PREFIX.substring(0,CLS_NEW_NO_SUFFIX_PREFIX.length()-1);
            if(className_old.contains("/")){
                clsNEW_name = CLS_NEW_NO_SUFFIX_PREFIX.substring(CLS_NEW_NO_SUFFIX_PREFIX.lastIndexOf("/")+1);
                clsNEW_path = CLS_NEW_NO_SUFFIX_PREFIX.substring(0,CLS_NEW_NO_SUFFIX_PREFIX.lastIndexOf("/")+1);
            }else{
                clsNEW_path = "";
                clsNEW_name = className_new;
            }
            if(!clsOLD_name.equals(clsNEW_name)) { //Class Name Change Procedures.
                // check if name already exists.
                JsonObject object = getClassObjByClassName(className_new);
                if(object!=null){
                    System.out.println("Class Name Already Exists");
                    f.setVisible(false);
                    return;
                }
                // Change in apis
                HashMap<Integer,String> hashMap = findInAPIs(className_old,false);
                for(Map.Entry<Integer, String> s: hashMap.entrySet()){
                    String fpath = s.getValue();
                    replaceAllInFile(fpath,className_old,className_new);
                }
                // change in class declaration - included in above procedure.
                // rename file -> refresh file tree.
                renameClassFile(filepath,clsNEW_name + ".smali");
                // change in database
                indexer.updateDatabase(hashMap);
                sendRefreshSignal(hashMap);
            }
            if(!clsNEW_path.equals(clsOLD_path)){ //class path Changed
                // check if name already exists in new path.
                String classFolders = clsOLD_path.replace("/","\\") + clsOLD_name + ".smali";
                String basepath = filepath.replace(classFolders,"");
                String newFilePath = basepath + clsNEW_path.replace("/","\\") + clsNEW_name + ".smali";
                if(new File(newFilePath).exists()){
                    System.out.println("File Already Exists... cannot move file");
                }
                // Change in apis
                HashMap<Integer,String> hashMap = findInAPIs(className_old,false);
                for(Map.Entry<Integer, String> s: hashMap.entrySet()){
                    String fpath = s.getValue();
                    replaceAllInFile(fpath,className_old,className_new);
                }

                moveClassToNewPath(filepath,newFilePath);
                indexer.updateDatabase(hashMap);
                sendRefreshSignal(hashMap);
            }
        }
        f.setVisible(false);
    }
    public static void renameClassFile(String filepath,String newName){
        //problem, its not just a one file, it have siblings like xxx$aaa;
        Path source = Paths.get(filepath);
        try {
            Files.move(source, source.resolveSibling(newName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void sendRefreshSignal(HashMap<Integer,String> hashMap){
        String[] ar = new String[hashMap.size()];
        int i  = 0;
        for(Map.Entry<Integer, String> s: hashMap.entrySet()){
            String fpath = s.getValue();
            ar[i] = fpath;
            i++;
        }
        editor_.settingChanged("reload_files",null,ar);
        editor_.settingChanged("file_tree_reload",null,null);
    }
    public static String prettyPrintJSON(String unformattedJsonString) {
        StringBuilder prettyJSONBuilder = new StringBuilder();
        int indentLevel = 0;
        boolean inQuote = false;
        for(char charFromUnformattedJson : unformattedJsonString.toCharArray()) {
            switch(charFromUnformattedJson) {
                case '"':
                    // switch the quoting status
                    inQuote = !inQuote;
                    prettyJSONBuilder.append(charFromUnformattedJson);
                    break;
                case ' ':
                    // For space: ignore the space if it is not being quoted.
                    if(inQuote) {
                        prettyJSONBuilder.append(charFromUnformattedJson);
                    }
                    break;
                case '{':
                case '[':
                    // Starting a new block: increase the indent level
                    prettyJSONBuilder.append(charFromUnformattedJson);
                    indentLevel++;
                    appendIndentedNewLine(indentLevel, prettyJSONBuilder);
                    break;
                case '}':
                case ']':
                    // Ending a new block; decrese the indent level
                    indentLevel--;
                    appendIndentedNewLine(indentLevel, prettyJSONBuilder);
                    prettyJSONBuilder.append(charFromUnformattedJson);
                    break;
                case ',':
                    // Ending a json item; create a new line after
                    prettyJSONBuilder.append(charFromUnformattedJson);
                    if(!inQuote) {
                        appendIndentedNewLine(indentLevel, prettyJSONBuilder);
                    }
                    break;
                default:
                    prettyJSONBuilder.append(charFromUnformattedJson);
            }
        }
        return prettyJSONBuilder.toString();
    }
    private static void appendIndentedNewLine(int indentLevel, StringBuilder stringBuilder) {
        stringBuilder.append("\n");
        // Assuming indention using 2 spaces
        stringBuilder.append("  ".repeat(Math.max(0, indentLevel)));
    }
}
