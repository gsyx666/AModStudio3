import mbpcm.ui.I_Window;
import mbpcm.ui.ManojUI;
import mbpcm.ui.TabbedFileEditor;

import javax.swing.*;

public class window_main implements I_Window {
    TabbedFileEditor tabbedFileEditor;
    JToggleButton toggleMain;
    window_main(Editor editor){
        tabbedFileEditor = new TabbedFileEditor(new TabbedFileEditor.TabbedPaneAction() {
            @Override
            public void onAction(String action, Object data) {
                editor.settingChanged(null,action,(String)data,null);
            }
        });
        toggleMain = ManojUI.getVerticalButton("Editor",true);
        toggleMain.setSelected(true);
        toggleMain.setEnabled(false);
        toggleMain.setVisible(false);
    }
    @Override
    public JComponent getWindow() {
        return tabbedFileEditor;
    }

    @Override
    public JToggleButton getButton() {
        return toggleMain;
    }

    @Override
    public String getWindowName() {
        return "Editor";
    }
    @Override
    public int getPrefPosition() {
        return WindowManager.CENTER;
    }

    @Override
    public void onSettingChanged(String a, String b, Object c) {

    }
}
