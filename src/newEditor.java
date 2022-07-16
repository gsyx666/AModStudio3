import com.formdev.flatlaf.FlatDarkLaf;
import manoj.customViews.FileSystemModel;
import manoj.ui.ManojUI;
import manoj.ui.TabbedFileEditor;
import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class newEditor {
    ManojUI ui;
    TabbedFileEditor tabbedFileEditor;
    public static void main(String[] args) {
        UIManager.put( "TabbedPane.tabInsets", new Insets(1,1,1,1) );
        UIManager.put("TabbedPane.selectedBackground",new Color(2, 23, 72));
        UIManager.put("TabbedPane.hoverColor",Color.BLACK);
        UIManager.put("TabbedPane.focusColor",new Color(190, 0, 74));
        FlatDarkLaf.setup();
         new newEditor();
    }


    newEditor(){
        AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory) TokenMakerFactory.getDefaultInstance();
        atmf.putMapping("text/smali", "smaliSyntax");
        ui = new ManojUI();
        tabbedFileEditor = new TabbedFileEditor();
        tabbedFileEditor.syntaxMap.put("smali","text/smali");
        JTree fileTree = new JTree();
        fileTree.setBorder(BorderFactory.createEmptyBorder());
        fileTree.setModel(new FileSystemModel(new File("H:\\")));
        fileTree.addTreeSelectionListener(event -> {
            File file = (File) fileTree.getLastSelectedPathComponent();
            if(file!=null && file.isFile()) {
                tabbedFileEditor.addFile(file.getAbsolutePath());
            }
        });
        JScrollPane jScrollPane = new JScrollPane();
        jScrollPane.setBorder(BorderFactory.createEmptyBorder());
        jScrollPane.setViewportView(fileTree);
        ui.setLeftItem(jScrollPane);
        ui.setCenterItem(tabbedFileEditor);

        tabbedFileEditor.addFile("Welcome","This is AMod Studio v1.5\nAuthor: ManojBhakarPCM");

    }

}
