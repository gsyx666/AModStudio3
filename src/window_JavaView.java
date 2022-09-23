import mbpcm.ManojTools;
import mbpcm.customViews.RModernScrollPane;
import mbpcm.ui.I_Window;
import mbpcm.ui.ManojUI;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;

public class window_JavaView implements I_Window {
    subc_EditorWindow taJava;
    JToggleButton taJavaToggle;
    RTextScrollPane rTextScrollPane;
    Editor editor;
    window_JavaView(Editor editor_){
        editor = editor_;
        taJava = new subc_EditorWindow();
        taJava.setFont(new Font("JetBrains Mono Regular",Font.PLAIN,10));
        rTextScrollPane = new RModernScrollPane(taJava);
        taJava.setEditable(false);
        taJavaToggle = ManojUI.getVerticalButton("Java",true);
        taJavaToggle.setSelected(true);

    }
    @Override
    public JComponent getWindow() {
        return rTextScrollPane;
    }

    @Override
    public JToggleButton getButton() {
        return taJavaToggle;
    }

    @Override
    public String getWindowName() {
        return "java";
    }

    @Override
    public int getPrefPosition() {
        return WindowManager.RIGHT;
    }

    @Override
    public void onSettingChanged(String a, String b, Object c) {
        if(a.equals("file_saved") || a.equals("file_changed")){
            if(b.endsWith(".smali")){
                new toJava(b).start();
                //System.out.println("Converted To Java");
            }else{
                taJava.setText("");
            }
        }

    }
    private class toJava extends Thread{
        private final String filepath;
        toJava(String path){
            filepath = path;
        }
        @Override
        public void run() {
            String dex = utils.getCurrentPath() + "\\tmp.dex";
            try {
                ManojTools.smaliToDexFast(filepath,dex);
                taJava.setData(ManojTools.DexToJava(dex),"java");
            } catch (Exception e) {
                utils.ErrorBox("Compile Error",e.getMessage() + "\nCause:" +  e.getClass() + "\n" + e.getCause());
            }
        }
    }
}
