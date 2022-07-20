import mbpcm.ui.ManojUI;

import javax.swing.*;
import java.util.HashMap;

public class WindowManager {
    public static final int CENTER = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;
    public static final int BOTTOM = 4;
    public static final int UP = 5;
    HashMap<String,JComponent> leftWinMap = new HashMap<>();
    HashMap<String,JComponent> rightWinMap = new HashMap<>();
    HashMap<String,JComponent> centerWinMap = new HashMap<>();
    HashMap<String,JComponent> bottomWinMap = new HashMap<>();
    HashMap<String,JToggleButton> idToggleMap = new HashMap<>();
    HashMap<String,Integer> idPaneMap = new HashMap<>();
    ManojUI ui;
    WindowManager(ManojUI ui_){
        ui = ui_;
    }
    public void addWindow(String id, JComponent window,JToggleButton toggleButton,int pos){
        switch (pos){
            case CENTER -> {
                centerWinMap.put(id,window);
            }
            case LEFT -> {
                leftWinMap.put(id,window);
            }
            case RIGHT -> {
                rightWinMap.put(id,window);
            }
            case BOTTOM -> {
                bottomWinMap.put(id,window);
            }
        }
        idPaneMap.put(id,pos);
        idToggleMap.put(id,toggleButton);
        toggleButton.putClientProperty("id",id);
        toggleButton.addActionListener(e -> {
            JToggleButton thisButton = ((JToggleButton) e.getSource());
            String idd =(String)thisButton.getClientProperty("id");
            int pos1 = idPaneMap.get(idd);
            showHideWindow(idd, pos1, thisButton.isSelected());
        });
        ui.leftBar.add(toggleButton);
    }
    void showHideWindow(String id,int pane,boolean show){
        switch (pane){
            case LEFT -> ui.setLeftItem(leftWinMap.get(id));
            case CENTER -> ui.setCenterItem(centerWinMap.get(id));
            case RIGHT -> ui.setRightItem(rightWinMap.get(id));
            case BOTTOM -> ui.setBottomItem(bottomWinMap.get(id));
        }
    }
}
