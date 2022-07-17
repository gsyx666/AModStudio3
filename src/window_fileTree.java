import mbpcm.ui.I_Window;
import mbpcm.ui.ManojUI;
import mbpcm.ui.TabbedFileEditor;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class window_fileTree implements I_Window {
    JToggleButton fileTreeToggle;
    JTree fileTree;
    TabbedFileEditor tabbedFileEditor;
    JScrollPane spFileTree;
    window_fileTree(TabbedFileEditor _tabbedFileEditor){
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
