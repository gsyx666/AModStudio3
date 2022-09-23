import mbpcm.ui.I_Window;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class plugin_smaliHelper implements I_Window {
    Editor editor_;
    boolean lock = false;
    JComboBox<String> methods = new JComboBox<>();
    HashMap<String,Integer> nameLocationMap = new HashMap<>();
    plugin_smaliHelper(Editor editor){
        editor_ = editor;
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
        if(a.equals("file_changed") || a.equals("file_saved")){
            toolbar_addFunctionList(b);
        }else if(a.equals("init")){
            methods.setMaximumSize(new Dimension(200,30));
            editor_.toolBar.add(methods);
            methods.addActionListener(e -> {
                if(!lock) {
                    String sel_item = (String) methods.getSelectedItem();
                    int pos = nameLocationMap.get(sel_item);
                    //System.out.println("Action Performed");
                    editor_.settingChanged("tabbed_editor_goto_line", null, pos);
                }
            });
        }else{
            //System.out.println("UNKNOWN ACTION" + a);
        }
    }
    void toolbar_addFunctionList(String filepath){
        lock = true;
        nameLocationMap.clear();
        methods.removeAllItems();
        try {
            String fileContent = Files.readString(Paths.get(filepath));
            //System.out.println("Function Browser Called");
            Pattern pattern = Pattern.compile(".method\\s+(.*)\\s+(.*?)\\((.*?)\\)(L(.*?);|[A-Z]{1})");
            Matcher matcher = pattern.matcher(fileContent);
            while(matcher.find()){
                String method = getUniqueName(matcher.group(2));
                int loc = matcher.start(2);
                //System.out.println(loc + "   " + method);
                nameLocationMap.put(method,loc);
                methods.addItem(method);
            }
            lock = false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    String getUniqueName(String name){
        String newName = name;
        int counter = 1;
        while(nameLocationMap.containsKey(newName)){
            newName = name + counter;
            counter ++;
        }
        return newName;
    }
}
