import mbpcm.customViews.RModernScrollPane;
import mbpcm.ui.I_Window;
import mbpcm.ui.ManojUI;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;

public class window_JavaView implements I_Window {
    subc_EditorWindow taJava;
    JToggleButton taJavaToggle;
    RTextScrollPane rTextScrollPane;
    ManojUI ui;
    window_JavaView(ManojUI ui_){
        ui = ui_;
        taJava = new subc_EditorWindow();
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
        System.out.println(a+ ":" +b);
    }
}
