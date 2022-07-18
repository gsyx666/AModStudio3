import mbpcm.ui.I_Window;
import mbpcm.ui.ManojUI;
import mbpcm.ui.TabbedFileEditor;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class window_fileTree implements I_Window {
    ManojUI ui;
    JToggleButton fileTreeToggle;
    JTree fileTree;
    TabbedFileEditor tabbedFileEditor;
    JScrollPane spFileTree;
    window_fileTree(ManojUI _ui, TabbedFileEditor _tabbedFileEditor){
        ui = _ui;
        tabbedFileEditor = _tabbedFileEditor;
        fileTree = new JTree();
        spFileTree = new JScrollPane();
        spFileTree.setBorder(BorderFactory.createEmptyBorder());
        spFileTree.setViewportView(fileTree);
        fileTree.setEditable(false);
        fileTree.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    File file = (File) fileTree.getLastSelectedPathComponent();
                    if(file!=null && file.isFile()) {
                        tabbedFileEditor.addFile(file.getAbsolutePath());
                    }
                }
            }
        });

        fileTreeToggle = ManojUI.getVerticalButton("Project",true);
        fileTreeToggle.addActionListener(e -> {
            if(!fileTreeToggle.isSelected()){
                fileTreeToggle.putClientProperty("dPos",ui.getLeftPane().getDividerLocation());
                //ui.getLeftPane().setDividerSize(0);
            }else{
                SwingUtilities.invokeLater(() -> {
                    ui.getLeftPane().setDividerLocation((int)fileTreeToggle.getClientProperty("dPos"));
                    //ui.getLeftPane().setDividerSize(3);
                    ui.f.setVisible(true);
                });
            }
            spFileTree.setVisible(fileTreeToggle.isSelected());
        });
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
}
