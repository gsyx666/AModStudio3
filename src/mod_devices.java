import mbpcm.ui.I_Window;
import mbpcm.ui.SmoothIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class mod_devices implements I_Window {
    Editor editor;
    JComboBox<String> devices;
    HashMap<String,String> AndroidDevices = new HashMap<>();
    mod_devices(Editor editor_){
        editor = editor_;
    }
    @Override
    public JComponent getWindow() {
        return null;
    }

    @Override
    public JToggleButton getButton() {
        return null;
    }

    @Override
    public String getWindowName() {
        return null;
    }

    @Override
    public int getPrefPosition() {
        return 0;
    }

    @Override
    public void onSettingChanged(String a, String b, Object c) {
        if(a.equals("init")){
            devices = new JComboBox<String>();
            devices.setMaximumSize(new Dimension(200,30));
            //editor.toolBar.add(Box.createHorizontalGlue());
            devices.addActionListener (new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if(devices.getItemCount()>0) {
                        String selectedItem = Objects.requireNonNull(devices.getSelectedItem()).toString();
                        editor.vars.put("device",AndroidDevices.get(selectedItem));
                        editor.settingChanged("device_selected",AndroidDevices.get(selectedItem), selectedItem);
                    }
                }
            });
            editor.toolBar.add(devices);
            JButton refreshDevices = new JButton();
            refreshDevices.setIcon(new SmoothIcon(utils.getImageFromRes("icons8-synchronize-12.png")));
            refreshDevices.addActionListener(e -> getDevices());
            editor.toolBar.add(refreshDevices);
            getDevices();
        }
    }
    public void getDevices(){
        CompletableFuture.runAsync(()->{
            devices.removeAllItems();
            reconnectNox();
            refreshAndroidDevices();
            for (HashMap.Entry<String, String> set : AndroidDevices.entrySet()) {
                devices.addItem(set.getKey());
            }
            if(devices.getItemCount()==1){
                devices.setSelectedIndex(0);
            }
        });
    }
    void refreshAndroidDevices(){
        AndroidDevices.clear();
        String adbpath = editor.vars.get("adb.exe");
        String results = utils.runFastTool(new String[]{adbpath,"devices"});
        System.out.println(results);
        List<String> deviceList = new ArrayList<String>();
        String[] lines = results.split("\n");

        for(String line:lines){
            if (line.endsWith("device")) {
                deviceList.add(line.split("\\t")[0]);
            }
        }

        for (String device : deviceList) {
            results = utils.runFastTool(new String[]{adbpath, "-s" ,device ,"shell","getprop","ro.product.model"});
            AndroidDevices.put(results,device);
        }
    }
    void reconnectNox(){
        String adbpath = editor.vars.get("adb.exe");
        String results = utils.runFastTool(new String[]{adbpath, "connect" ,"127.0.0.1:62001"});
        System.out.println(results);
    }
}
