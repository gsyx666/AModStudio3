import mbpcm.ui.I_Window;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.*;

public class plugin_projectHelper implements I_Window {
    Editor editor_;
    String basepath;
    JButton focus;
    Set<String> packageSet = new HashSet<>();
    java.util.List<String> list;
    boolean lock = false;
    JComboBox<String> pkgs;
    HashMap<String,Integer> nameLocationMap = new HashMap<>();
    plugin_projectHelper(Editor editor){
        editor_ = editor;
        pkgs = new JComboBox<>();
        pkgs.setMaximumSize(new Dimension(200,30));
        focus = new JButton("F");
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
        if(a.equals("project_loaded")){
            packageSet.clear();
            pkgs.removeAllItems();
            basepath = b;
            //System.out.println("Project Loaded *******************");
            listPackages(b + "\\smali");
            listPackages(b + "\\smali_classes2");
            list = new ArrayList<String>(packageSet);
            Collections.sort(list);
            for(String f: list){
                pkgs.addItem(f);
            }
            //toolbar_addFunctionList(b);
        }else if(a.equals("init")){
            editor_.toolBar.add(pkgs);
            pkgs.addActionListener(e -> {
                if(!lock) {
                    String sel_item = (String) pkgs.getSelectedItem();
                    String pkgFullPath = basepath + "\\smali\\" + sel_item.replace(".","\\");
                    if(!new File(pkgFullPath).exists()){
                        pkgFullPath = basepath + "\\smali_classes2\\" + sel_item.replace(".","\\");
                    }
                    editor_.settingChanged("select_tree_path", pkgFullPath, null);
                }
            });
        }else{
            //System.out.println("UNKNOWN ACTION" + a);
        }
    }
    public void listPackages(String basepath){
        File dir = new File(basepath);
        File[] firstLevelDirs = dir.listFiles(File::isDirectory);
        for(File f:firstLevelDirs){
            if(f.listFiles(File::isDirectory).length!=0 && !f.getName().equals("android") && !f.getName().equals("androidx")){
                listf(f,f.getName(),0);
            }
        }
    }
    public void listf(File directory,String prefix,int level) {
        File[] fList = directory.listFiles();
        if(fList != null) {
            for (File file : fList) {
                if (file.isDirectory() && level < 2) {
                    if (file.listFiles(File::isFile).length != 0) {
                        packageSet.add(prefix + "." + file.getName());
                    }
                    listf(file, prefix + "." + file.getName(), level + 1);
                }else{
                    packageSet.add(prefix); //drop it.
                }
            }
        }
    }

}
