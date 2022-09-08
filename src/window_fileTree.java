import mbpcm.customViews.FileSystemModel;
import mbpcm.customViews.ModernScrollPane;
import mbpcm.ui.I_Window;
import mbpcm.ui.ManojUI;
import mbpcm.ui.SmoothIcon;
import mbpcm.ui.TabbedFileEditor;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class window_fileTree implements I_Window {
    Editor editor;
    JToggleButton fileTreeToggle;
    JTree fileTree;
    TabbedFileEditor tabbedFileEditor;
    ModernScrollPane spFileTree;
    JPanel mainPanel = new JPanel(new BorderLayout());
    JPanel topbar = new JPanel();
    static Icon smali = new SmoothIcon(utils.getImageFromRes("fileicons/smali.png"),16,16) ;
    static Icon yaml = new SmoothIcon(utils.getImageFromRes("fileicons/yml.png"),16,16) ;
    static Icon xml = new SmoothIcon(utils.getImageFromRes("fileicons/xml.png"),16,16) ;
    static Icon image = new SmoothIcon(utils.getImageFromRes("fileicons/image.png"),16,16) ;
    window_fileTree(Editor editor1, TabbedFileEditor _tabbedFileEditor){
        editor = editor1;
        tabbedFileEditor = _tabbedFileEditor;
        fileTree = new JTree();
        JPopupMenu contextMenu = new JPopupMenu();
        JMenuItem menuItemSearch = new JMenuItem("Search Here");
        JMenuItem menuItemExplore = new JMenuItem("Open in Explore");
        menuItemSearch.addActionListener(e -> {
            editor.settingChanged(this,"search_win_show",getLastSelectedPath(),null);
            System.out.println(getLastSelectedPath());
        });

        contextMenu.add(menuItemSearch);
        contextMenu.add(menuItemExplore);

        fileTree.setComponentPopupMenu(contextMenu);
        fileTree.setBackground(new Color(60,63,65));
        spFileTree = new ModernScrollPane(fileTree);
        spFileTree.setBorder(BorderFactory.createEmptyBorder());
        //spFileTree.setViewportView(fileTree);
        fileTree.setEditable(false);
        fileTree.setCellRenderer(new CustomIconRenderer());
        fileTree.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    File file = (File) fileTree.getLastSelectedPathComponent();
                    if(file!=null && file.isFile()) {
                        //tabbedFileEditor.addFile(file.getAbsolutePath());
                        editor.settingChanged(null,"file_opened",file.getAbsolutePath(),null);
                    }
                }
            }
        });
        fileTree.addTreeSelectionListener(e -> {

        });
        fileTreeToggle = ManojUI.getVerticalButton("Project",true);
        fileTreeToggle.setSelected(true);
        mainPanel.add(topbar,BorderLayout.NORTH);
        mainPanel.add(spFileTree,BorderLayout.CENTER);

    }
    String getLastSelectedPath(){
        TreePath selectedPath = fileTree.getSelectionPath();
        if (selectedPath != null) {
            File selectedNode = ((File)selectedPath.getLastPathComponent());
            return selectedNode.getAbsolutePath();
        }
        return "";
    }
    @Override
    public JComponent getWindow() {
        return mainPanel;
    }

    @Override
    public JToggleButton getButton() {
        return fileTreeToggle;
    }

    @Override
    public String getWindowName() {
        return "fileTree";
    }

    @Override
    public int getPrefPosition() {
        return WindowManager.LEFT;
    }

    @Override
    public void onSettingChanged(String a, String b, Object c) {
        if(a.equals("app_decompiled")){
            fileTree.setModel(new FileSystemModel(new File(b)));
            editor.ui.f.setTitle(editor.getVersion() + " : " + b);
        }else if(a.equals("select_tree_path")){
            gotoPath(b);
        }else if(a.equals("project_loaded")){
           fileTree.setModel(new FileSystemModel(new File(b)));
           loadProjectAndHighlight(b,c);
        }else if(a.equals("file_changed")){
            gotoPath(b);
        }
    }
    void loadProjectAndHighlight(String b,Object c){
        String[] data = (String[]) c;
        String packageName = data[0];
        String mainClass = data[1];
        String mainClassPath = b + "\\smali\\" + mainClass.replace(".","\\") + ".smali";
        if(!new File(mainClassPath).exists()){
            mainClassPath = b + "\\smali_classes2\\" +  mainClass.replace(".","\\") + ".smali";
        }
        gotoPath(mainClassPath); // highlighted the tree.
        //System.out.println(mainClassPath);
        editor.settingChanged(null,"file_opened",mainClassPath,null); //opened in editor.

    }
    static class CustomIconRenderer extends DefaultTreeCellRenderer {
       // @Serial
        //private static final long serialVersionUID = 967937360839244309L;

        public CustomIconRenderer(){
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            if(value instanceof File){
                String name = ((File) value).getName();
                int index = name.lastIndexOf('.');
                if(index>0) {
                    switch (name.substring(index)){
                        case ".xml" -> setIcon(xml);
                        case ".yml" -> setIcon(yaml);
                        case ".smali" -> setIcon(smali);
                        case ".png" -> setIcon(image);
                    }
                }
            }
            return this;
        }
    }

    void gotoPath(String path){
        String rootpath = fileTree.getModel().getRoot().toString();
        java.util.List<File> filePaths = new ArrayList<>();
        filePaths.add(new File(rootpath));
        String newpath = path.replace(rootpath + "\\","");
        String[] ar = newpath.split("\\\\");

        String totalPath = rootpath;
        for(String p:ar){
            totalPath = totalPath + "\\" + p;
            filePaths.add(new File(totalPath));
            System.out.println(totalPath);
        }

        //File[] testpath = new File[]{(File)fileTree.getModel().getRoot(),new File("H:\\splitApks\\Garuda [v4.0.7]-split\\base\\smali"),new File("H:\\splitApks\\Garuda [v4.0.7]-split\\base\\smali\\android"),new File("H:\\splitApks\\Garuda [v4.0.7]-split\\base\\smali\\android\\support")};
        File[] arr = new File[filePaths.size()];
        filePaths.toArray(arr);
        fileTree.clearSelection();
        fileTree.scrollPathToVisible(new TreePath(arr));
        fileTree.addSelectionPath(new TreePath(arr));
        TreePath pathh = fileTree.getSelectionPath();
        if (pathh == null) return;
        Rectangle bounds = fileTree.getPathBounds(pathh);
        int oldHeight = bounds.height;
        // set the height to the visible height to force the node to top
        bounds.height = fileTree.getVisibleRect().height;
        bounds.y = bounds.y - oldHeight*5;
        fileTree.scrollRectToVisible(bounds);

    }

}
