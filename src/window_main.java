import mbpcm.ui.I_Window;
import mbpcm.ui.ManojUI;
import mbpcm.ui.TabbedFileEditor;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;

public class window_main implements I_Window {
    TabbedFileEditor tabbedFileEditor;
    JToggleButton toggleMain;
    JButton focus;
    Editor editor_;
    window_main(Editor editor){
        editor_ = editor;
        tabbedFileEditor = new TabbedFileEditor((action, data) -> {
            editor.settingChanged(action,(String)data,null);
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
        }else if(a.equals("rsta_get_selected_text")){
            String filepath = tabbedFileEditor.getSelectedFilePath();
            RSyntaxTextArea rSyntaxTextArea = tabbedFileEditor.getTextAreaByFilePath(filepath);
            int[] selectionPositions = new int[2];
            selectionPositions[0] = rSyntaxTextArea.getSelectionStart();
            selectionPositions[1] = rSyntaxTextArea.getSelectionEnd();
            editor_.settingChanged("rsta_selected_text",filepath,selectionPositions);
        }else if(a.equals("reload_files")){
            String[] changedFiles = (String[])c;
            tabbedFileEditor.reloadFiles(changedFiles);
            String filepath = tabbedFileEditor.getSelectedFilePath();
            // if contains
            editor_.settingChanged("file_changed",filepath,null);
        }else if(a.equals("action_navigate")){
            String classPath;
            if(b.contains("->")){
                String[] ar = b.split("->");
                classPath = ar[0];
                String method = ar[1];
            }else{
                classPath = b;
            }
            String fpath = utils.ClassPathToFilePath(editor_.vars.get("project"),classPath);
            if(fpath!=null){
                editor_.settingChanged("file_opened",fpath,null);
            }else{
                System.out.println("path not found for :" + b);
            }
        }
    }
}
