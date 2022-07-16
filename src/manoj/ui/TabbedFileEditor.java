package manoj.ui;

import com.formdev.flatlaf.ui.FlatTabbedPaneUI;
import manoj.customViews.ModernScrollPane;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

//TODO: Smooth Scroll.
//TODO: Fonts
//TODO: three dots.
//TODO: Find and Replace
//TODO: Upper Pane saprator disable.
//TODO: Syntax Highlighting for XML etc.
//TODO: BUG: binary files are not loaded.
//TODO: Icon for filetype.

public class TabbedFileEditor extends JTabbedPane {
    public HashMap<String,String> syntaxMap = new HashMap<>();
    public Theme theme;

    public TabbedFileEditor(){
        fillHashMap();
        this.setUI(new FlatTabbedPaneUI() {
            @Override
            protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
                return 24; // manipulate this number however you please.
            }
        });
        this.setBorder(BorderFactory.createEmptyBorder());
        try {
            theme = Theme.load(getClass().getResourceAsStream("/org/fife/ui/rsyntaxtextarea/themes/monokai.xml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void addFile(String filepath){
        addFile_(filepath,null,false);
    }
    public void addFile(String id, String content){
        addFile_(id,content,true);
    }
    private void addFile_(String filepath,String filecontent,boolean stringsrc){
        int index = this.indexOfTab(filepath);
        if(index > -1){
            this.setSelectedIndex(index);
            return;
        }
        String filename = Paths.get(filepath).getFileName().toString();
        String fileExtension = getExtension(filepath).toLowerCase();
        RSyntaxTextArea rSyntaxTextArea = new RSyntaxTextArea();
        rSyntaxTextArea.setBackground(new Color(42, 42, 42));
        rSyntaxTextArea.setForeground(Color.WHITE);
        rSyntaxTextArea.setSelectionColor(Color.BLUE);
        rSyntaxTextArea.setCurrentLineHighlightColor(Color.BLACK);
        if(syntaxMap.containsKey(fileExtension)) {
            rSyntaxTextArea.setSyntaxEditingStyle(syntaxMap.get(fileExtension));
        }
        theme.apply(rSyntaxTextArea);
        if(stringsrc) {
            rSyntaxTextArea.setText(filecontent);
        }else{
            rSyntaxTextArea.setText(getFilesAsString(filepath));
            rSyntaxTextArea.putClientProperty("filepath",filepath);
            rSyntaxTextArea.registerKeyboardAction(
                    e -> {
                        Object obj = e.getSource();
                        if (obj instanceof RSyntaxTextArea rSyntaxTextArea1) {
                            String filepath1 = (String) rSyntaxTextArea1.getClientProperty("filepath");
                            try {
                                Files.writeString(Paths.get(filepath1),rSyntaxTextArea1.getText());
                                System.out.println("File Saved: " + filepath1);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    },
                    KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK) ,
                    JComponent.WHEN_FOCUSED
            );
        }
        ModernScrollPane rTextScrollPane = new ModernScrollPane(rSyntaxTextArea);
        //rTextScrollPane.getVerticalScrollBar().setUnitIncrement(20);
        rTextScrollPane.setBorder(BorderFactory.createEmptyBorder());
        this.add(filepath,rTextScrollPane);
        index = this.indexOfTab(filepath);

        JPanel pnlTab = new JPanel();
        pnlTab.setOpaque(false);

        JLabel lblTitle = new JLabel(filename);
        pnlTab.add(lblTitle);

        JButton btnClose = new JButton("x");
        btnClose.setBorder(BorderFactory.createEmptyBorder());
        btnClose.putClientProperty("id",filepath);

        btnClose.addActionListener(e -> {
            Object obj = e.getSource();
            if (obj instanceof JButton jButton) {
                String strID = (String) jButton.getClientProperty("id");
                this.removeTabAt(this.indexOfTab(strID));
            }
        });
        pnlTab.add(btnClose);

        this.setTabComponentAt(index, pnlTab);
        this.setSelectedIndex(index);
    }
    private String getFilesAsString(String filepath){
        try {
            return Files.readString(Paths.get(filepath), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "";
        }
    }
    private void fillHashMap(){
        syntaxMap.put("txt","text/plain");
        syntaxMap.put("asm","text/asm");
        syntaxMap.put("c","text/c");
        syntaxMap.put("h","text/c");
        syntaxMap.put("cpp","text/cpp");
        syntaxMap.put("cs","text/cs");
        syntaxMap.put("css","text/css");
        syntaxMap.put("csv","text/csv");
        syntaxMap.put("d","text/d");
        syntaxMap.put("dtd","text/dtd");
        syntaxMap.put("html","text/html");
        syntaxMap.put("ini","text/ini");
        syntaxMap.put("java","text/java");
        syntaxMap.put("js","text/javascript");
        syntaxMap.put("json","text/json");
        syntaxMap.put("php","text/php");
        syntaxMap.put("vb","text/vb");
        syntaxMap.put("bat","text/bat");
        syntaxMap.put("xml","text/xml");
        syntaxMap.put("yml","text/yaml");
        syntaxMap.put("py","text/python");
        syntaxMap.put("tcl","text/tcl");
        syntaxMap.put("lua","text/lua");
        syntaxMap.put("mk","text/makefile");


        syntaxMap.put("actionscript","text/actionscript");
        syntaxMap.put("asm6502","text/asm6502");
        syntaxMap.put("bbcode","text/bbcode");
        syntaxMap.put("clojure","text/clojure");
        syntaxMap.put("dockerfile","text/dockerfile");
        syntaxMap.put("dart","text/dart");
        syntaxMap.put("delphi","text/delphi");
        syntaxMap.put("fortran","text/fortran");
        syntaxMap.put("golang","text/golang");
        syntaxMap.put("groovy","text/groovy");
        syntaxMap.put("hosts","text/hosts");
        syntaxMap.put("htaccess","text/htaccess");
        syntaxMap.put("jshintrc","text/jshintrc");
        syntaxMap.put("jsp","text/jsp");
        syntaxMap.put("kotlin","text/kotlin");
        syntaxMap.put("latex","text/latex");
        syntaxMap.put("less","text/less");
        syntaxMap.put("lisp","text/lisp");
        syntaxMap.put("markdown","text/markdown");
        syntaxMap.put("mxml","text/mxml");
        syntaxMap.put("nsis","text/nsis");
        syntaxMap.put("perl","text/perl");
        syntaxMap.put("properties","text/properties");
        syntaxMap.put("ruby","text/ruby");
        syntaxMap.put("sas","text/sas");
        syntaxMap.put("scala","text/scala");
        syntaxMap.put("sql","text/sql");
        syntaxMap.put("typescript","text/typescript");
        syntaxMap.put("unix","text/unix");

    }
    private String getExtension(String fileName){
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            return fileName.substring(i+1);
        }else{ //extensionless file.
            return fileName.substring(fileName.lastIndexOf("\\") +1 );
        }
    }

}
