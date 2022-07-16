import manoj.ManojTools;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class mod_MainMenu extends super_MenuInterface {

    mod_MainMenu(Editor _mainWin) {
        super(_mainWin);
        JButton convertToJava = getIButton("convert.png");
        convertToJava.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action_save();
            }
        });
        mainWin.toolBar.add(Box.createHorizontalStrut(10));
        mainWin.toolBar.add(convertToJava);
        mainWin.taSmali.registerKeyboardAction(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        action_save();
                        mainWin.setStatusBarTextFlash("File Saved",1);
                    }
                },
                KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK) ,
                JComponent.WHEN_FOCUSED
        );
    }
    public void action_save(){
        String smali = mainWin.taSmali.currentFilePath;
        utils.file_put_contents(smali,mainWin.taSmali.getText());
        if(smali.endsWith(".smali")){
            convertToJava(smali);
        }else{
            mainWin.taJava.setText("");
        }
    }
    public void convertToJava(String smali){
        String dex = "C:\\Users\\MbPCM\\Desktop\\tmp.dex";
        boolean ret = false;
        try {
            ManojTools.smaliToDexFast(smali,dex);
            mainWin.taJava.setData(ManojTools.DexToJava(dex),"java");
        } catch (Exception e) {
            utils.ErrorBox("Compile Error",e.getMessage() + "\nCause:" +  e.getClass() + "\n" + e.getCause());
        }
    }


}