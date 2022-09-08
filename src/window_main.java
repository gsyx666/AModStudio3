import mbpcm.ui.I_Window;
import mbpcm.ui.ManojUI;
import mbpcm.ui.TabbedFileEditor;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class window_main implements I_Window {
    TabbedFileEditor tabbedFileEditor;
    JToggleButton toggleMain;
    JButton focus;
    window_main(Editor editor){
        tabbedFileEditor = new TabbedFileEditor((action, data) -> {
            editor.settingChanged(null,action,(String)data,null);
            editor.vars.put(action,(String) data);
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
        if(a.equals("file_opened")){
            tabbedFileEditor.addFile(b);
        }else if(a.equals("tabbed_editor_goto_line")){
            String filepath = tabbedFileEditor.getSelectedFilePath();
            RSyntaxTextArea rSyntaxTextArea = tabbedFileEditor.getTextAreaByFilePath(filepath);
            rSyntaxTextArea.setCaretPosition((int)c);
        }else if(a.equals("open_file_and_goto_line")){
            int line = (int)c;
            tabbedFileEditor.addFile(b);
            String filepath = tabbedFileEditor.getSelectedFilePath();
            RSyntaxTextArea rSyntaxTextArea = tabbedFileEditor.getTextAreaByFilePath(filepath);
            rSyntaxTextArea.setCaretPosition((int)c);
        }
    }
}
