import mbpcm.ui.I_Window;
import mbpcm.ui.ManojUI;
import mbpcm.ui.TabbedFileEditor;

import javax.swing.*;

public class window_main implements I_Window {
    TabbedFileEditor tabbedFileEditor;
    JToggleButton toggleMain;
    window_main(){
        tabbedFileEditor = new TabbedFileEditor();
        toggleMain = ManojUI.getVerticalButton("Editor",true);
        toggleMain.setSelected(true);
        toggleMain.setEnabled(false);
        toggleMain.addActionListener(e -> tabbedFileEditor.setVisible(toggleMain.isSelected()));

    }
    @Override
    public JComponent getWindow() {
        return tabbedFileEditor;
    }

    @Override
    public JToggleButton getButton() {
        return toggleMain;
    }
}
