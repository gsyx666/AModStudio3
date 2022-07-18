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
        taJavaToggle.addActionListener(e -> {
            if(!taJavaToggle.isSelected()){
                taJavaToggle.putClientProperty("dPos",ui.getRightPane().getDividerLocation());
                //ui.getRightPane().setDividerSize(0);
            }else{
                SwingUtilities.invokeLater(() -> {
                    ui.getRightPane().setDividerLocation((int)taJavaToggle.getClientProperty("dPos"));
                    //ui.getRightPane().setDividerSize(3);
                    ui.f.setVisible(true);
                });
            }
            rTextScrollPane.setVisible(taJavaToggle.isSelected());
            ui.f.setVisible(true);
        });
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
}
