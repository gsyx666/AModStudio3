import mbpcm.ui.ManojUI;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.prefs.Preferences;

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
    HashMap<String,String> lastAdded = new HashMap<>();
    List<JToggleButton> leftButtonList = new ArrayList<>();
    List<JToggleButton> rightButtonList = new ArrayList<>();
    List<JToggleButton> bottomButtonList = new ArrayList<>();
    List<JToggleButton> centerButtonList = new ArrayList<>();
    ManojUI ui;
    int leftPanePos = 0;
    int rightPanePos = 0;
    int bottomPanePos = 0;
    WindowManager(ManojUI ui_){
        ui = ui_;
    }
    public void addWindow(String id, JComponent window,JToggleButton toggleButton,int pos){
        getHashMapByPos(pos).put(id,window);
        lastAdded.put(""+pos,id);
        idPaneMap.put(id,pos);
        idToggleMap.put(id,toggleButton);
        toggleButton.putClientProperty("id",id);
        toggleButton.addActionListener(e -> {
            JToggleButton thisButton = ((JToggleButton) e.getSource());
            String idd =(String)thisButton.getClientProperty("id");
            int pos1 = idPaneMap.get(idd);
            showHideWindow2(idd, pos1, thisButton.isSelected());
        });
        toggleButton.setName(id);
        getButtonListByPos(pos).add(toggleButton);
        ui.leftBar.add(toggleButton);
    }
    List<JToggleButton> getButtonListByPos(int pos){
        switch (pos){
            case LEFT -> {return leftButtonList;}
            case RIGHT -> {return rightButtonList;}
            case BOTTOM -> {return bottomButtonList;}
            case CENTER -> {return centerButtonList;}
        }
        return null;
    }
    public void finishAdding(){

        //IMPORTANT!
        //1. first set bounds  2. both loadLocation and getLocation will needed to correctly set divider loc.
        ui.getRightPane().loadLocation();
        ui.getLeftPane().loadLocation();
        ui.getBottomPane().loadLocation();

        leftPanePos = ui.getLeftPane().getDividerSavedLocation();
        rightPanePos = ui.getRightPane().getDividerSavedLocation();
        bottomPanePos = ui.getBottomPane().getDividerSavedLocation();

        ui.getRightPane().setDividerLocation(rightPanePos);
        ui.getLeftPane().setDividerLocation(leftPanePos);
        ui.getBottomPane().setDividerLocation(bottomPanePos);


        System.out.println("LEFT: " + leftPanePos + " RIGHT: " + rightPanePos + " BOTTOM:" + bottomPanePos);
        //then set windows.s
        setInitialViews(LEFT);
        setInitialViews(RIGHT);
        setInitialViews(CENTER);
        setInitialViews(BOTTOM);
    }
    void setInitialViews(int pos){
        //RULE: if last saved found, set last saved, otherwise save last added.
        //check: if there is something added in desired pane or it is just empty?
        JComponent component;
        Preferences preferences = Preferences.userNodeForPackage(this.getClass());
        String lastID =  preferences.get("lastCompo" + pos,"none");
        HashMap<String,JComponent> hashMap = getHashMapByPos(pos);

        // Selecting Component To Load.

        if(hashMap.size()>0){
            if(!lastID.equals("none")){
                if(hashMap.containsKey(lastID)){
                    component = hashMap.get(lastID);
                }else{
                    //load last added.
                    component = hashMap.get(lastAdded.get(""+pos));
                }
            }else{ //none. last saved is not working. load last added.
                component = hashMap.get(lastAdded.get(""+pos));
            }
        }else{
            System.out.println("No View Is Loaded For This Side :" + pos);
            return;
        }

        // Loading the Component:

        setViewItem(component,pos);

    }
    void showHideWindow2(String id,int pane,boolean show) {
        HashMap<String,JComponent> hashMap = getHashMapByPos(pane);
        hashMap.get(id).setVisible(show);
        Preferences preferences = Preferences.userNodeForPackage(this.getClass());
        if(show){
            //setDivider
            switch (pane){
                case LEFT -> ui.getLeftPane().setDividerLocation((int) ui.getLeftPane().getClientProperty("poss"));
                case RIGHT -> ui.getRightPane().setDividerLocation((int) ui.getRightPane().getClientProperty("poss"));
                case BOTTOM -> ui.getBottomPane().setDividerLocation((int) ui.getBottomPane().getClientProperty("poss"));
            }
            //set All other button unselected.
            List<JToggleButton> list = getButtonListByPos(pane);
            for(JToggleButton button: list){
                if(!button.getName().equals(id)){
                    button.setSelected(false);
                }
            }
            //setViewItem.
            setViewItem(hashMap.get(id),pane);
        }else{
            //Hide Divider
            switch (pane){
                case LEFT -> {
                    ui.getLeftPane().putClientProperty("poss",ui.getLeftPane().getDividerLocation());
                    ui.getLeftPane().setDividerLocation(0);
                }
                case RIGHT -> {
                    ui.getRightPane().putClientProperty("poss",ui.getRightPane().getDividerLocation());
                    ui.getRightPane().setDividerLocation(Integer.MAX_VALUE);
                }
                case BOTTOM -> {
                    ui.getBottomPane().putClientProperty("poss",ui.getBottomPane().getDividerLocation());
                    ui.getBottomPane().setDividerLocation(Integer.MAX_VALUE);
                }
            }
        }
    }


    void setViewItem(JComponent j,int pos){
        switch (pos) {
            case LEFT -> ui.setLeftItem(j);
            case CENTER -> ui.setCenterItem(j);
            case RIGHT -> ui.setRightItem(j);
            case BOTTOM -> ui.setBottomItem(j);
        }
    }
    HashMap<String,JComponent> getHashMapByPos(int pos){
        switch (pos){
            case LEFT -> {return leftWinMap;}
            case RIGHT -> {return rightWinMap;}
            case CENTER -> {return centerWinMap;}
            case BOTTOM -> {return bottomWinMap;}
        }
        return null;
    }
}
