package mbpcm.ui;

import com.formdev.flatlaf.ui.FlatTabbedPaneUI;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.fife.ui.rtextarea.SearchResult;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import static javax.swing.Box.createHorizontalStrut;
import static mbpcm.ui.uiUtils.*;

//TODO: Smooth Scroll.
//TODO: BUG: JetBrains Mono Font Not Loading
//TODO: three dots.
//TODO: Find and Replace
//TODO: Upper Pane saprator disable.
//TODO: Syntax Highlighting for XML etc.
//TODO: BUG: binary files are not loaded.
//TODO: Icon for filetype.

public class TabbedFileEditor extends JTabbedPane {
    public HashMap<String,String> syntaxMap = new HashMap<>();
    public Theme theme;
    public Font font;
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
        font = new Font("JetBrains Mono",Font.PLAIN,14);
        theme.baseFont = font;
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



        if(syntaxMap.containsKey(fileExtension)) {
            rSyntaxTextArea.setSyntaxEditingStyle(syntaxMap.get(fileExtension));
        }
        theme.apply(rSyntaxTextArea);
        rSyntaxTextArea.setCurrentLineHighlightColor(Color.BLACK);
        //rSyntaxTextArea.setMarkOccurrencesColor(Color.blue);
        rSyntaxTextArea.setSelectionColor(Color.BLUE);
        rSyntaxTextArea.setMarkAllHighlightColor(new Color(0x237F8A));
        //rSyntaxTextArea.setSelectedTextColor(Color.BLUE);
        //rSyntaxTextArea.setUseSelectedTextColor(true);
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
        rSyntaxTextArea.setFont(font);
        RTextScrollPane rTextScrollPane = new RTextScrollPane(rSyntaxTextArea);
        JPanel tabContentPanel = new JPanel(new BorderLayout());
        JPanel searchPanel = getSearchPannel(rSyntaxTextArea);
        //searchPanel.setVisible(false);

        searchPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE,25));
        searchPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE,25));
        //searchPanel.setBackground(Color.BLACK);
        tabContentPanel.add(searchPanel,BorderLayout.SOUTH);
        tabContentPanel.add(rTextScrollPane,BorderLayout.CENTER);
        rTextScrollPane.setBorder(BorderFactory.createEmptyBorder());

        rSyntaxTextArea.registerKeyboardAction(
                e -> {
                    Object obj = e.getSource();
                    if (obj instanceof RSyntaxTextArea rSyntaxTextArea1) {
                        searchPanel.setVisible(true);
                        JTextField jTextField = (JTextField) getComponentByName(searchPanel,"txtSearch");
                        if(jTextField!=null){
                            jTextField.requestFocusInWindow();
                            jTextField.setText((String)rSyntaxTextArea.getClientProperty("lastS"));
                            jTextField.selectAll();
                        }
                    }
                },
                KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK) ,
                JComponent.WHEN_FOCUSED
        );


        this.add(filepath,tabContentPanel);

        index = this.indexOfTab(filepath);

        JPanel pnlTab = new JPanel();
        pnlTab.setOpaque(false);

        JLabel lblTitle = new JLabel(filename);
        pnlTab.add(lblTitle);

        JButton btnClose = new JButton("x");
        btnClose.setBorder(new uiUtils.RoundedBorder2(UIManager.getColor("Panel.background"),1,15));
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

    private String getExtension(String fileName){
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            return fileName.substring(i+1);
        }else{ //extensionless file.
            return fileName.substring(fileName.lastIndexOf("\\") +1 );
        }
    }
    private JPanel getSearchPannel(RSyntaxTextArea rt){
        Color themeColor = new Color(69,73,74);
        JPanel jPanel = new JPanel(new BorderLayout());
        JPanel searchBox = new JPanel();
        JPanel resultBox = new JPanel();

        //jPanel.setLayout(new BoxLayout(jPanel,BoxLayout.X_AXIS));
        searchBox.setLayout(new BoxLayout(searchBox,BoxLayout.X_AXIS));
        resultBox.setLayout(new BoxLayout(resultBox,BoxLayout.X_AXIS));
        searchBox.setPreferredSize(new Dimension(300,Integer.MAX_VALUE));
        searchBox.setMaximumSize(new Dimension(300,Integer.MAX_VALUE));
        searchBox.setBackground(themeColor);
        JLabel resultLabel = new JLabel("0 results");
        // Search Box JPanel
        Color backColor = searchBox.getBackground();
        JToggleButton Cc = getJToggleButton("Cc");Cc.setBackground(backColor);
        JToggleButton W = getJToggleButton("W");W.setBackground(backColor);
        JToggleButton regex = getJToggleButton(" . * ");regex.setBackground(backColor);
        JTextField searchTextBox = new JTextField();
        // Listen for changes in the text
        searchTextBox.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {warn();}
            public void removeUpdate(DocumentEvent e) {warn();}
            public void insertUpdate(DocumentEvent e) {warn();}
            public void warn() {
                rt.setCaretPosition(0);
                rt.putClientProperty("curr",0);
               resultLabel.setText(findText(rt,searchTextBox.getText(),Cc.isSelected(),regex.isSelected(),W.isSelected(),true));

            }
        });
        searchTextBox.setName("txtSearch");
        searchTextBox.setFocusable(true);
        searchTextBox.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        searchTextBox.setBackground(backColor);

        searchBox.add(searchTextBox);
        searchBox.add(Cc);
        searchBox.add(W);
        searchBox.add(regex);
        jPanel.add(searchBox,BorderLayout.WEST);

        //Result Box JPanel


        resultLabel.setMinimumSize(new Dimension(50,Integer.MAX_VALUE));

        JButton sUp = getJButton("↑");
        sUp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               resultLabel.setText(findText(rt,searchTextBox.getText(),Cc.isSelected(),regex.isSelected(),W.isSelected(),false));
            }
        });
        JButton sDw = getJButton("↓");
        sDw.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resultLabel.setText(findText(rt,searchTextBox.getText(),Cc.isSelected(),regex.isSelected(),W.isSelected(),true));
            }
        });
        JButton filter = getJButton("Filter");
        resultBox.add(createHorizontalStrut(20));
        resultBox.add(resultLabel);
        resultBox.add(sDw);
        resultBox.add(sUp);
        resultBox.add(filter);
        jPanel.add(resultBox,BorderLayout.CENTER);

        //Close Button
        JButton close = new JButton("x");
        close.setBorder(BorderFactory.createEmptyBorder());
        close.setBackground(jPanel.getBackground());
        close.setForeground(new Color(0xB0B0F8));
        //close.addActionListener(e -> jPanel.setVisible(false));
        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jPanel.setVisible(false);
                rt.putClientProperty("lastS",searchTextBox.getText());
                searchTextBox.setText("");
            }
        });
        jPanel.add(close,BorderLayout.EAST);


        return jPanel;
    }
    public static String findText(RSyntaxTextArea rt,String what,boolean matchCase,boolean regex,boolean wholeWord,boolean forward){
        SearchContext context = new SearchContext();
        context.setSearchFor(what);
        context.setMatchCase(matchCase);
        context.setRegularExpression(regex);
        context.setSearchForward(forward);
        context.setWholeWord(wholeWord);
        SearchResult searchResult = SearchEngine.find(rt, context);

        int curr = (int)rt.getClientProperty("curr");
        if(forward) {
            if(curr<searchResult.getMarkedCount())
                curr++;
        }else{
            if(curr>1)
                curr--;
        }
        rt.putClientProperty("curr",curr);

        return curr + "/" + searchResult.getMarkedCount() + " results";
        //System.out.println(searchResult.getMarkedCount());
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



}
