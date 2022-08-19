import com.android.dex.Dex;
import com.formdev.flatlaf.FlatDarkLaf;
import mbpcm.customViews.FileSystemModel;
import mbpcm.ui.ManojUI;
import mbpcm.ui.SmoothIcon;
import mbpcm.ui.TabbedFileEditor;
import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static java.nio.file.FileSystems.newFileSystem;

public class newEditor {
    ManojUI ui;
    TabbedFileEditor tabbedFileEditor;
    DexEditor dexEditor;
    HashMap<String,String> openedFiles = new HashMap<>();
    public static void main(String[] args) {
        UIManager.put( "TabbedPane.tabInsets", new Insets(1,1,1,1) );
        UIManager.put("TabbedPane.selectedBackground",new Color(2, 23, 72));
        UIManager.put("TabbedPane.hoverColor",Color.BLACK);
        UIManager.put("TabbedPane.focusColor",new Color(190, 0, 74));
        FlatDarkLaf.setup();
         new newEditor();
    }
    void printFilesAndFolders(String[] list,String folder){
        Set<String> folders = new HashSet<>();
        for(String path:list){
            if(path.startsWith(folder)){
                String newPath = path.substring(folder.length());
                if(newPath.contains("/")){
                    folders.add(folder + newPath.substring(0,newPath.indexOf('/')+1));
                }else{
                    System.out.println("FILE: " + newPath);
                }
            }
        }
        for(String fol: folders){
            System.out.println("FOLDER:" + fol);
        }
    }
    public static String getCRC32(String data){
        CRC32 fileCRC32 = new CRC32();
        fileCRC32.update(data.getBytes(StandardCharsets.UTF_8));
        return String.format(Locale.US,"%08X", fileCRC32.getValue());
    }
    newEditor(){
        tabbedFileEditor = new TabbedFileEditor();
        AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory) TokenMakerFactory.getDefaultInstance();
        atmf.putMapping("text/smali", "smaliSyntax");
        ui = new ManojUI();
        JMenuBar menuBar = new JMenuBar();
        ui.f.setJMenuBar(menuBar);
        ui.toolBar.setVisible(false);
        JTree fileTree = new JTree();
        dexEditor = new DexEditor(fileTree);
        fileTree.setBorder(BorderFactory.createEmptyBorder());
        fileTree.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String path = (String) fileTree.getLastSelectedPathComponent();
                    System.out.println(path);
                    if(path.startsWith("L") & path.endsWith(";")){
                        //Class file.
                        String code = dexEditor.getSmaliCode(path);
                        openedFiles.put(path,getCRC32(code));
                        tabbedFileEditor.addFile(path,code);
                    }else{
                        //zip file.

                    }
                }
            }
        });
        fileTree.setCellRenderer(new CustomIconRenderer());
        JMenu jMenu = new JMenu("File");
        JMenuItem openFile = new JMenuItem("Select APK");
        openFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filepath = "";
                filepath = utils.selectFileByDialog("Select APK","H:\\",null);
                if(filepath.equals("")){return;}
                dexEditor.loadAPK(filepath);
            }
        });
        jMenu.add(openFile);
        menuBar.add(jMenu);

        tabbedFileEditor.syntaxMap.put("smali","text/smali");


        JScrollPane jScrollPane = new JScrollPane();
        jScrollPane.setBorder(BorderFactory.createEmptyBorder());
        jScrollPane.setViewportView(fileTree);
        ui.setLeftItem(jScrollPane);
        ui.setCenterItem(tabbedFileEditor);

        tabbedFileEditor.addFile("Welcome","This is AMod Studio v1.5\nAuthor: ManojBhakarPCM");

    }
    static class CustomIconRenderer extends DefaultTreeCellRenderer {
        // @Serial
        //private static final long serialVersionUID = 967937360839244309L;
        static Icon smali = new SmoothIcon(utils.getImageFromRes("fileicons/smali.png"),16,16) ;
        static Icon yaml = new SmoothIcon(utils.getImageFromRes("fileicons/yml.png"),16,16) ;
        static Icon xml = new SmoothIcon(utils.getImageFromRes("fileicons/xml.png"),16,16) ;
        static Icon image = new SmoothIcon(utils.getImageFromRes("fileicons/image.png"),16,16) ;
        public CustomIconRenderer(){
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            if(value instanceof String name){
                int index = name.lastIndexOf('.');
                if(index>0) {
                    switch (name.substring(index)){
                        case ".xml" -> setIcon(xml);
                        case ".yml" -> setIcon(yaml);
                        case ".smali" -> setIcon(smali);
                        case ".png" -> setIcon(image);
                    }
                }
                if(name.equals("classes.dex")){
                    setText("src");
                }else if(name.endsWith("/")){
                    String newName = name.substring(0,name.lastIndexOf("/"));
                    if(newName.contains("/")){
                        setText(newName.substring(newName.lastIndexOf("/")+1));
                    }else {
                        setText(newName);
                    }
                }else if(name.contains("/") && (!name.endsWith(";"))){
                    setText(name.substring(name.lastIndexOf("/")+1));
                }else if(name.startsWith("L") && name.endsWith(";") && name.contains("/")){
                    String newName = name.substring(1,name.length()-1).replace('/','.');
                    setText(newName);
                }
            }
            return this;
        }
    }
}
