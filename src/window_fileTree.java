import mbpcm.customViews.FileSystemModel;
import mbpcm.customViews.ModernScrollPane;
import mbpcm.ui.I_Window;
import mbpcm.ui.ManojUI;
import mbpcm.ui.SmoothIcon;
import mbpcm.ui.TabbedFileEditor;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.Serial;
import java.net.MalformedURLException;
import java.net.URL;

public class window_fileTree implements I_Window {
    Editor editor;
    JToggleButton fileTreeToggle;
    JTree fileTree;
    TabbedFileEditor tabbedFileEditor;
    ModernScrollPane spFileTree;
    static Icon smali = new SmoothIcon(utils.getImageFromRes("fileicons/smali.png"),16,16) ;
    static Icon yaml = new SmoothIcon(utils.getImageFromRes("fileicons/yml.png"),16,16) ;
    static Icon xml = new SmoothIcon(utils.getImageFromRes("fileicons/xml.png"),16,16) ;
    static Icon image = new SmoothIcon(utils.getImageFromRes("fileicons/image.png"),16,16) ;
    window_fileTree(Editor editor1, TabbedFileEditor _tabbedFileEditor){
        editor = editor1;
        tabbedFileEditor = _tabbedFileEditor;
        fileTree = new JTree();
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

        fileTreeToggle = ManojUI.getVerticalButton("Project",true);
        fileTreeToggle.setSelected(true);
    }
    @Override
    public JComponent getWindow() {
        return spFileTree;
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
        }
    }
    static class CustomIconRenderer extends DefaultTreeCellRenderer {
        @Serial
        private static final long serialVersionUID = 967937360839244309L;

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

}
