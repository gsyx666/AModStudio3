import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class utils {
    static String getFileDetails(File file) {
        if (file == null)
            return "";
        return "Name: " + file.getName() + "\n" + "Path: " + file.getPath() + "\n" + "Size: " + file.length() + "\n";
    }
    static void initTheme(){

        UIManager.put( "control", new Color(29, 29, 29) );
        UIManager.put( "info", new Color(37, 36, 36) );
        UIManager.put( "nimbusBase", new Color(39, 39, 40) );
        UIManager.put( "nimbusAlertYellow", new Color( 248, 187, 0) );
        UIManager.put( "nimbusDisabledText", new Color(54, 54, 54) );
        UIManager.put( "nimbusFocus", new Color(115,164,209) );
        UIManager.put( "nimbusGreen", new Color(176,179,50) );
        UIManager.put( "nimbusInfoBlue", new Color( 66, 139, 221) );
        UIManager.put( "nimbusLightBackground", new Color(30, 30, 31) );
        UIManager.put( "nimbusOrange", new Color(191,98,4) );
        UIManager.put( "nimbusRed", new Color(169,46,34) );
        UIManager.put( "nimbusSelectedText", new Color(7, 83, 246) );
        UIManager.put( "nimbusSelectionBackground", new Color( 104, 93, 156) );
        UIManager.put( "text", new Color(255, 255, 255) );
        UIManager.put("JFrame.activeTitleBackground", Color.red);
        UIManager.put("MenuBar.background", new Color(39, 39, 40));
        UIManager.put("Menu.foreground",Color.WHITE);
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    //UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        setUIFont();
    }
    public static void setUIFont (/*javax.swing.plaf.FontUIResource f*/){
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get (key);
            //if(value instanceof  String)
            if(key.toString().contains("menu")||key.toString().contains("Menu"))
                System.out.println(key + "        >>>       " + value);
            //if (value instanceof javax.swing.plaf.FontUIResource)
                //UIManager.put (key, f);
        }
    }
    public static String removeLastChar(String str,Character chr){
            if (str != null && str.length() > 0 && str.charAt(str.length() - 1) == chr) {
                str = str.substring(0, str.length() - 1);
            }
            return str;
    }
    public static String getSavePathByDialog(String title,String initPath,String filter){
        JFileChooser chooser = new JFileChooser(initPath);
        chooser.setDialogTitle(title);
        //filter = "MS WORD FILE(docx)|MS EXCEL FILE(xls)|PDF DOCUMENT(pdf)";
        if(filter!=null){   //make filters.
            String[] filters = filter.split("\\|");
            for(String f:filters){
                String[] singleFilter = f.split("\\(");
                String fName = singleFilter[0];
                String fExt = removeLastChar(singleFilter[1],')');
                chooser.addChoosableFileFilter(new FileNameExtensionFilter(fName,fExt));
            }

        }
        int r = chooser.showSaveDialog(null);
        if (r == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile().getAbsolutePath();
        }
        return "";
    }
    public static String selectFileByDialog(String title,String initPath,String filter){
        JFileChooser chooser = new JFileChooser(initPath);
        chooser.setDialogTitle(title);
        //filter = "MS WORD FILE(docx)|MS EXCEL FILE(xls)|PDF DOCUMENT(pdf)";
        if(filter!=null){   //make filters.
            String[] filters = filter.split("\\|");
            for(String f:filters){
                String[] singleFilter = f.split("\\(");
                String fName = singleFilter[0];
                String fExt = removeLastChar(singleFilter[1],')');
                chooser.addChoosableFileFilter(new FileNameExtensionFilter(fName,fExt));
            }

        }
        int r = chooser.showOpenDialog(null);
        if (r == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile().getAbsolutePath();
        }
        return "";
    }
    public static void file_put_contents(String filepath,String data){
        File fi = new File(filepath);
        try {
            FileWriter wr = new FileWriter(fi, false);
            BufferedWriter w = new BufferedWriter(wr);
            w.write(data);
            w.flush();
            w.close();
            wr.close();
        } catch (Exception evt) {
            System.out.println(evt.getMessage());
        }
    }

    public static String file_get_contents(String filepath){
        File fi = new File(filepath);
        try {
            String s1 = "", sl = "";
            FileReader fr = new FileReader(fi);
            BufferedReader br = new BufferedReader(fr);
            sl = br.readLine();
            while ((s1 = br.readLine()) != null) {
                sl = sl + "\n" + s1;
            }
            br.close();
            fr.close();
            return sl;
        } catch (Exception evt) {
            System.out.println(evt.getMessage());
            return "";
        }
    }
    public static void file_append(String filepath,String line) {
        try {
            Writer output = new BufferedWriter(new FileWriter(filepath, true));  //clears file every time
            output.append(line);
            output.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void saveFileWithDialog(String text){
        JFileChooser j = new JFileChooser("f:");
        int r = j.showSaveDialog(null);
        if (r == JFileChooser.APPROVE_OPTION) {
            String path = j.getSelectedFile().getAbsolutePath();
           file_put_contents(path,text);
        }
    }


    public static String openFileWithDialog(){
            JFileChooser j = new JFileChooser("C:");
            int r = j.showOpenDialog(null);
            if (r == JFileChooser.APPROVE_OPTION) {
                return file_get_contents(j.getSelectedFile().getAbsolutePath());
            }else{
                return "";
            }
    }

    public static String selectFolder(String title,String initdir){
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File(initdir));
        chooser.setDialogTitle(title);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        //
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            System.out.println("getCurrentDirectory(): " +  chooser.getCurrentDirectory());
            System.out.println("getSelectedFile() : " +  chooser.getSelectedFile());
            return chooser.getSelectedFile().getAbsolutePath();
        }
        else {
            System.out.println("No Selection ");
            return "";
        }
    }

    static void MessageBox(String title,String info){
        JOptionPane.showMessageDialog(null, info, title, JOptionPane.INFORMATION_MESSAGE);
    }
    static void MessageBox(String info){
        JOptionPane.showMessageDialog(null, info, "Info", JOptionPane.INFORMATION_MESSAGE);
    }
    static void ErrorBox(String title,String info){
        JOptionPane.showMessageDialog(null, info, title, JOptionPane.ERROR_MESSAGE);
    }
    static String getCurrentPath(){
        try {
            return new File(".").getCanonicalPath();
        } catch (IOException e) {
            //throw new RuntimeException(e);
            return "";
        }
    }
    static String getJavaPath(){

        String tmp1 = System.getProperty("java.home") + "\\bin\\java.exe";
        String tmp2 = System.getProperty("sun.boot.library.path") + "\\java.exe";
        String tmp3 = System.getProperty("java.library.path")+ "\\java.exe";
        if(new File(tmp1).exists()) {
            return tmp1;
        }else if(new File(tmp2).exists()){
            return tmp2;
        }else if(new File(tmp3).exists()) {
            return tmp3;
        }else{
            String[] paths = System.getenv("PATH").split(";");
            for(String path:paths){
                if(new File(path + "\\java.exe").exists()){
                    return path + "\\java.exe";
                }
            }
        }
        return "";
    }
    static void openExplorerSelectFile(String filepath){
        try {
            Runtime.getRuntime().exec("explorer.exe /select," + filepath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static String getUniquePath(String path,String extension){
        int i = 1;
        String newPath = path + extension;
        if(new File(newPath).exists()) {
            while (new File(newPath).exists()){
                newPath = path + "_" + i + extension;
                i++;
            }
        }
        return newPath;
    }
    static String removeExtension(String path){
        String filename;
        String foldrpath;
        String filenameWithoutExtension;
        if(path.equals("")){return "";}
        if(path.contains("\\")){    // direct substring method give wrong result for "a.b.c.d\e.f.g\supersu"
            filename = path.substring(path.lastIndexOf("\\"));
            foldrpath = path.substring(0, path.lastIndexOf('\\'));;
            if(filename.contains(".")){
                filenameWithoutExtension = filename.substring(0, filename.lastIndexOf('.'));
            }else{
                filenameWithoutExtension = filename;
            }
            return foldrpath + filenameWithoutExtension;
        }else{
            return path.substring(0, path.lastIndexOf('.'));
        }
    }
    static String getFolder(String path){
        if(path.contains("\\")) {
            String folder = path.substring(0, path.lastIndexOf('\\'));
            return folder;
        }else{
            return path;
        }
    }

    public static PrivateKey getPK8_privateKey(String filepath) throws Exception {
        byte[] keyBytes = Files.readAllBytes(Paths.get(filepath));
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }
    public static X509Certificate getPem_publicCertificate(String filepath) throws Exception {
        CertificateFactory fact = CertificateFactory.getInstance("X.509");
        FileInputStream is = new FileInputStream (filepath);
        X509Certificate cer = (X509Certificate) fact.generateCertificate(is);
        is.close();
        return cer;
    }
    public static String runFastTool(String[] args) {
        String logs = "";
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(args);
            final Process proc = processBuilder.start();
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            String s;
            while ((s = stdInput.readLine()) != null) {
                logs += s + "\n";
            }
            while ((s = stdError.readLine()) != null) {
                logs += s + "\n";
            }
            return logs;
        } catch (Exception e) {
            return "Exception: " + e.getMessage();
        }
    }
    public static Image getImageFromRes(String name){
        try {
            return ImageIO.read(Objects.requireNonNull(utils.class.getClassLoader().getResourceAsStream(name)));
        } catch (IOException e) {
            return null;
        }
    }
    public static ImageIcon getImageIconFromRes(String name){
        try {
            return new ImageIcon(ImageIO.read(Objects.requireNonNull(utils.class.getResourceAsStream(name))));
        } catch (IOException e) {
            return null;
        }
    }
    public static void LocateFileInExplorer(String filepath){
        try {
            Runtime.getRuntime().exec("explorer.exe /select," + filepath);
        } catch (IOException e) {
            System.out.println("Cannot Locate File:" + filepath );
            e.printStackTrace();
        }
    }
    public static String[] getZipFileList(String filepath){
       java.util.List<String> mylist = new ArrayList<>();
        try (ZipFile zipFile = new ZipFile(filepath)) {
            Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
            while (zipEntries.hasMoreElements()) {
                String fileName = zipEntries.nextElement().getName();
                mylist.add(fileName);
                //System.out.println(fileName);
            }
            String[] out = new String[mylist.size()];
            return mylist.toArray(out);
        }catch (Exception e){
            return null;
        }
    }
    public static String readZipTextFile(String filepath,String filename){
        //java.util.List<String> mylist = new ArrayList<>();
        try (ZipFile zipFile = new ZipFile(filepath)) {
            Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
            while (zipEntries.hasMoreElements()) {
                ZipEntry element = zipEntries.nextElement();
                if(element.getName().equals(filename)){
                     return new BufferedReader(new InputStreamReader(zipFile.getInputStream(element))).lines().collect(Collectors.joining("\n"));
                }
            }
            return null;
        }catch (Exception e){
            return null;
        }
    }
    public static boolean extractZipFile(String zipFilePath,String filenameInZip,String savePath){
        //java.util.List<String> mylist = new ArrayList<>();
        try (ZipFile zipFile = new ZipFile(zipFilePath)) {
            Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
            while (zipEntries.hasMoreElements()) {
                ZipEntry element = zipEntries.nextElement();
                if(element.getName().equals(filenameInZip)){
                    createFileFromInputStream(zipFile.getInputStream(element),savePath);
                    return true;
                }
            }
            return false; //File Not Found
        }catch (Exception e){
            return false; //Not a valid zip file.
        }
    }
    public static String getStringFromInputStream(InputStream inputStream){
        return new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));
    }
    public static void createFileFromInputStream(InputStream inputStream,String filepath){
        try {
            FileUtils.copyInputStreamToFile(inputStream, new File(filepath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static String[] showSelectionDialog(String[] in){
        JList<String> list = new JList<>(in);
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        final JComponent[] inputs = new JComponent[] {
                new JLabel("Select Apks to Decompile"),
                list
        };
        int result = JOptionPane.showConfirmDialog(null, inputs, "Split Apk Selector", JOptionPane.DEFAULT_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            return list.getSelectedValuesList().toArray(new String[0]);
        } else {
            return null;
        }
    }
    public static JFrame createBasicWindow(String title, double heightfactor, double widthfactor){
        JFrame f = new JFrame(title);
        f.getContentPane().setLayout(new BoxLayout(f.getContentPane(),BoxLayout.Y_AXIS));
        Rectangle gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        double width = gd.getWidth()*widthfactor;
        double height = gd.getHeight()*heightfactor;
        f.setSize((int)width, (int)height);
        f.setLocationRelativeTo(null); //center screen
        //f.setVisible(true);
        f.setState(JFrame.MAXIMIZED_BOTH); //start Maximized
        return f;
    }
    public static String ClassPathToFilePath(String projpath,String classPath){
        classPath = utils.removeLastChar(classPath,';');
        classPath = classPath.substring(1);
        classPath = classPath.replace("/","\\");
        String fullpath = projpath + "\\smali\\" + classPath + ".smali";
        if(new File(fullpath).exists()){
            return fullpath;
        }else{
            for(int n=2;n<20;n++) {
                String smaliPath = projpath + "\\smali_classes" + n;
                if(!new File(smaliPath).exists()){
                    return null; //no such class exists.
                }else {
                    fullpath = smaliPath + "\\" + classPath + ".smali";
                    if(new File(fullpath).exists()){
                        return fullpath;
                    }
                }
            }
        }
        System.out.println("class doesnt exists in 20 dex files.. is apk is that huge ?");
        return null; //limit crossed. (will never happened)
    }
}
