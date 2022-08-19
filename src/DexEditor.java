import com.android.dex.Dex;
import com.android.dx.command.dexer.DxContext;
import com.android.dx.merge.CollisionPolicy;
import com.android.dx.merge.DexMerger;
import com.google.common.base.Strings;
import org.jf.baksmali.Adaptors.ClassDefinition;
import org.jf.baksmali.BaksmaliOptions;
import org.jf.baksmali.formatter.BaksmaliWriter;
import org.jf.dexlib2.DexFileFactory;
import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.dexbacked.DexBackedClassDef;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.iface.Field;
import org.jf.dexlib2.iface.Method;
import org.jf.dexlib2.iface.MultiDexContainer;
import org.jf.smali.Smali;
import org.jf.smali.SmaliOptions;

import javax.annotation.Nonnull;
import javax.swing.*;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static java.nio.file.FileSystems.newFileSystem;

public class DexEditor {
    JTree jTree;
    String mApkPath;
    String mApkFolder;
    String mLoadedDexPath;
    String outDex;
    DexBackedDexFile dexFile;
    DexBackedDexFile.IndexedSection<DexBackedClassDef> classDef;
    HashMap<String,Integer> classIndexMap = new HashMap<>();
    List<String> dexes = new ArrayList<>();
    List<String> zipContent = new ArrayList<>();
    FileSystem fs;
    int apiLevel = 28; //TODO: auto Detection
    DexEditor(JTree filetree){
        jTree = filetree;
        int dexNo = 0;
        //dexFile = loadDexFile("");
        //String testAPK = "H:\\test.apk";
       // String testDex = "H:\\splitApks\\MoviesFree [1.2]-split\\base\\build\\apk\\classes.dex";
        //loadAPK(mApkPath);
        //loadDexFromApk(dexes.get(dexNo));
        //loadDexDirect(testDex);
        //System.out.println(getSmaliCode(1));
        //getMethods(1);
        //getFields(1);
        //String smali = getSmaliCode(0);
        //smali = smali.replace("dec2021","aug2022");
        //System.out.println(smali);
        //compileAndMerge(mLoadedDexPath,smali);
       // updateAPK(dexes.get(dexNo),outDex);
        //System.out.println("DONE");
    }
    //public static void main(String[] args){
       // new DexEditor();
   // }
    public void updateAPK(String filename,String filepath){
        Path myFilePath = Paths.get(filepath);
        Path zipFilePath = Paths.get(mApkPath);
        try( FileSystem fs = newFileSystem(zipFilePath, (ClassLoader) null) ){
            Path fileInsideZipPath = fs.getPath(filename);
            Files.delete(fileInsideZipPath);
            Files.copy(myFilePath, fileInsideZipPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void loadAPK(String path){
        mApkPath = path;
        jTree.setModel(new ApkModel(path));
        mApkFolder = path.substring(0,path.lastIndexOf("\\"));
        try(ZipFile zipFile = new ZipFile(mApkPath)) {
            zipFile.stream().map(ZipEntry::getName).forEach(s -> {
                zipContent.add(s);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Path zipFilePath = Paths.get(mApkPath);
        try {
            fs = newFileSystem(zipFilePath, (ClassLoader) null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void loadDexFromApk(String dexname){
        //Extract -> load From File.
        String outputDex = mApkFolder + "\\" + dexname;
        extractZipFile(mApkPath,dexname,outputDex);
        loadDexDirect(outputDex);
    }
    public void loadDexDirect(String dexPath){
        mLoadedDexPath = dexPath;
        dexFile = loadDexFile(dexPath);
        classDef = dexFile.getClassSection();
        System.out.println(classDef.size());
        int i=0;
        for(DexBackedClassDef cd: classDef){
            classIndexMap.put(cd.getType(),i);
            i++;
        }
    }
    public String getSmaliCode(String className){
        String className_ = toSmaliClassName(className);
        if(classIndexMap.containsKey(className_)){
            return getSmaliCode(classIndexMap.get(className_));
        }else{
            return null;
        }
    }
    private String toSmaliClassName(String className){
        if(className.contains(".")){
            className = className.replace(".","/");
        }
        if(!className.startsWith("L")){
            className = "L" + className;
        }
        if(!className.endsWith(";")){
            className = className + ";";
        }
        return className;
    }
    public String getSmaliCode(int index){
        StringWriter writer = new StringWriter();
        BaksmaliOptions options = new BaksmaliOptions();
        ClassDef classDef1 = getClassDef(index);
        ClassDefinition classDefinition = new ClassDefinition(options,classDef1);
        try {
            classDefinition.writeTo(new BaksmaliWriter(writer));
            System.out.println("Done");
            return writer.getBuffer().toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private ClassDef getClassDef(int index){
        return classDef.get(index);
    }
    private void getMethods(int index){
        ClassDef classDef1 = getClassDef(index);
        Iterable<? extends Method> methods = classDef1.getMethods();
        methods.forEach(method -> {
            System.out.println(method.getReturnType() + " " + method.getName() + method.getParameters().toString());
        });
    }
    private void getFields(int index){
        ClassDef classDef1 = getClassDef(index);
        Iterable<? extends Field> fields = classDef1.getFields();
        fields.forEach(field -> {
            System.out.println(toJavaClassName(field.getType()) + " " + field.getName() + " " + field.getDefiningClass());
        });
    }
    private String toJavaClassName(String smaliClassName){
        return smaliClassName.replace('/','.').replace(";","").substring(1);
    }
    private ClassDef getClassDef(String className){
        String cn = toSmaliClassName(className);
        if(classIndexMap.containsKey(cn)){
            return classDef.get(classIndexMap.get(cn));
        }else{
            return null;
        }
    }
    public static void extractZipFile(String zipFilePath, String filenameInZip, String savePath){
        //java.util.List<String> mylist = new ArrayList<>();
        try (ZipFile zipFile = new ZipFile(zipFilePath)) {
            Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
            while (zipEntries.hasMoreElements()) {
                ZipEntry element = zipEntries.nextElement();
                if(element.getName().equals(filenameInZip)){
                    byte[] bytes = zipFile.getInputStream(element).readAllBytes();
                    Files.write(Paths.get(savePath),bytes);
                    return;
                }
            }
        }catch (Exception ignored){
        }
    }
    void compileAndMerge(String mainDex, String ... strings){

        outDex = mApkFolder + "\\merged.dex";
        try {
            String updateDexPath = mApkFolder + "\\updates.dex";
            SmaliOptions smaliOptions = new SmaliOptions();
            smaliOptions.outputDexFile = updateDexPath;
            smaliOptions.verboseErrors = true;

            String[] files = new String[strings.length];
            for(int i=0;i<strings.length;i++){
                String savePath = mApkFolder + "\\tempClass" + i + ".smali";
                Files.writeString(Paths.get(savePath),strings[i]);
                files[i] = savePath;
            }

            Smali.assemble(smaliOptions,files);

            Dex dexA = new Dex(new File(updateDexPath));
            Dex dexB = new Dex(new File(mainDex));
            DxContext dxContext = new DxContext();
            DexMerger dexMerger = new DexMerger(new Dex[]{dexA,dexB},CollisionPolicy.KEEP_FIRST,dxContext);
            dexMerger.merge().writeTo(new File(outDex));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void compileSmaliCode(String ... strings) throws IOException {
        String updateDexPath = mApkFolder + "\\updates.dex";
        SmaliOptions smaliOptions = new SmaliOptions();
        smaliOptions.apiLevel = this.apiLevel;
        smaliOptions.outputDexFile = updateDexPath;
        smaliOptions.verboseErrors = true;
        Smali.assemble(smaliOptions,strings);
    }
    private void updateDex(String updatedDex,String mainDex,String outDex) throws IOException {
        Dex dexA = new Dex(new File(updatedDex));
        Dex dexB = new Dex(new File(mainDex));
        DxContext dxContext = new DxContext();
        DexMerger dexMerger = new DexMerger(new Dex[]{dexA,dexB},CollisionPolicy.KEEP_FIRST,dxContext);
        dexMerger.merge().writeTo(new File(outDex));
    }
    protected static DexBackedDexFile loadDexFile(@Nonnull String input) {
        int apiLevel = 28; //TODO: autoDetectAPI.
        MultiDexContainer.DexEntry<? extends DexBackedDexFile> dexEntry;
        DexBackedDexFile dexFile;
        File file = new File(input);
        String dexEntryName = null;
        if (file.getPath().length() < input.length()) {
            dexEntryName = input.substring(file.getPath().length() + 1);
        }

        Opcodes opcodes = Opcodes.forApi(apiLevel);

        if (!Strings.isNullOrEmpty(dexEntryName)) {
            boolean exactMatch = false;
            if (dexEntryName.length() > 2 && dexEntryName.charAt(0) == '"' && dexEntryName.charAt(dexEntryName.length() - 1) == '"') {
                dexEntryName = dexEntryName.substring(1, dexEntryName.length() - 1);
                exactMatch = true;
            }

            //String inputEntry = dexEntryName;

            try {

                dexEntry = DexFileFactory.loadDexEntry(file, dexEntryName, exactMatch, opcodes);

                dexFile = dexEntry.getDexFile();
            } catch (IOException var7) {
                throw new RuntimeException(var7);
            }
        } else {
            try {
                MultiDexContainer<? extends DexBackedDexFile> container = DexFileFactory.loadDexContainer(file, opcodes);
                if (container.getDexEntryNames().size() == 1) {
                    dexEntry = container.getEntry((String)container.getDexEntryNames().get(0));

                    assert dexEntry != null;

                    dexFile = (DexBackedDexFile)dexEntry.getDexFile();
                } else {
                    if (container.getDexEntryNames().size() <= 1) {
                        throw new RuntimeException(String.format("\"%s\" has no dex files", input));
                    }

                    dexEntry = container.getEntry("classes.dex");
                    if (dexEntry == null) {
                        dexEntry = container.getEntry((String)container.getDexEntryNames().get(0));
                    }

                    assert dexEntry != null;

                    dexFile = (DexBackedDexFile)dexEntry.getDexFile();
                }
            } catch (IOException var8) {
                throw new RuntimeException(var8);
            }
        }
        return dexFile;
    }
    public class ApkModel implements TreeModel {
        String[] list;
        String mApkPath;
        ApkModel(String path){
            mApkPath = path;
            try(ZipFile zipFile = new ZipFile(path)) {
                list = zipFile.stream().map(ZipEntry::getName).toArray(String[]::new);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        @Override
        public Object getRoot() {
            return mApkPath;
        }

        @Override
        public Object getChild(Object parent, int index) {
            String p = (String) parent;
            if(p.equals(mApkPath)){
                return getList(list,"")[index];
            }else if(p.endsWith(".dex")){
                if(classDef==null) {
                    loadDexFromApk(p);
                }
                return classDef.get(index).getType();
            }else if(p.endsWith("/")){
                return getList(list,p)[index];
            }else{
                return null;
            }
        }

        @Override
        public int getChildCount(Object parent) {
            String p = (String) parent;
            if(p.equals(mApkPath)){
                return getList(list,"").length;
            }else if(p.endsWith(".dex")){
                if(classDef==null) {
                    loadDexFromApk(p);
                }
                return classDef.size();
            }else if(p.endsWith("/")){
                return getList(list,p).length;
            }else{
                return 0;
            }
        }

        @Override
        public boolean isLeaf(Object node) {
            String p = (String) node;
            return !p.equals(mApkPath) && !p.endsWith(".dex") && !p.endsWith("/");
        }

        @Override
        public void valueForPathChanged(TreePath path, Object newValue) {

        }

        @Override
        public int getIndexOfChild(Object parent, Object child) {
            return 0;
        }

        @Override
        public void addTreeModelListener(TreeModelListener l) {

        }

        @Override
        public void removeTreeModelListener(TreeModelListener l) {

        }
    }
    String[] getList(String[] list,String folder){
        List<String> all = new ArrayList<>();
        Set<String> folders = new HashSet<>();
        for(String path:list){
            if(path.startsWith(folder)){
                String newPath = path.substring(folder.length());
                if(newPath.contains("/")){
                    folders.add(folder + newPath.substring(0,newPath.indexOf('/')+1));
                }else{
                    all.add(path);
                    //System.out.println("FILE: " + newPath);
                }
            }
        }
        //System.out.println("FOLDER:" + fol);
        all.addAll(folders);
        return all.toArray(String[]::new);
    }
}
