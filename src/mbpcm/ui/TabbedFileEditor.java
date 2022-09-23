package mbpcm.ui;

import com.formdev.flatlaf.ui.FlatTabbedPaneUI;
import mbpcm.customViews.RModernScrollPane;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.fife.ui.rtextarea.SearchResult;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static javax.swing.Box.createHorizontalStrut;
import static mbpcm.ui.uiUtils.*;

//TODO: Smooth Scroll.
//TODO: BUG: JetBrains Mono Font Not Loading
//TODO: Syntax Highlighting for XML etc.
//TODO: BUG: binary files are not loaded.

public class TabbedFileEditor extends JTabbedPane {
    public HashMap<String,String> syntaxMap = new HashMap<>();
    public Theme theme;
    public Font font;
    private TabbedPaneAction _tabbedPaneAction = null;
    private String hoverTab = "";
    private HashMap<String,RSyntaxTextArea> textAreaStringHashMap = new HashMap<>();
    public TabbedFileEditor(){
        this(null);
    }
    public TabbedFileEditor(TabbedPaneAction tabbedPaneAction){
        UIManager.put("TabbedPane.tabInsets", new Insets(1,8,1,2) );
        UIManager.put("TabbedPane.selectedBackground",new Color(2, 23, 72));
        UIManager.put("TabbedPane.hoverColor",Color.BLACK);
        //UIManager.put("TabbedPane.focusColor",new Color(4, 39, 108));
        _tabbedPaneAction = tabbedPaneAction;
        fillHashMap();
        this.setUI(new FlatTabbedPaneUI() {
            @Override
            protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
                return 23;
            }
        });
        this.addChangeListener(e -> {
            int index = getSelectedIndex();
            if(index>0){
                _tabbedPaneAction.onAction("file_changed", getTitleAt(index));
            }
        });

        this.setBorder(BorderFactory.createEmptyBorder());
        try {
            theme = Theme.load(getClass().getResourceAsStream("/org/fife/ui/rsyntaxtextarea/themes/monokai.xml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        font = new Font("JetBrains Mono Regular",Font.PLAIN,10);
        theme.baseFont = font;
        this.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
    }
    public interface TabbedPaneAction{
        public abstract void onAction(String action,Object data);
    }
    public RSyntaxTextArea getTextAreaByFilePath(String filepath){
        return textAreaStringHashMap.getOrDefault(filepath, null);
    }
    public String getSelectedFilePath(){
        return getTitleAt(getSelectedIndex());
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
        if(!fileExtension.equals("yml")) {
            if (!stringsrc && isBinaryFile(new File(filepath))) {
                try {
                    Runtime.getRuntime().exec("CMD /C \"" + filepath + "\"");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return;
            }
        }
        RSyntaxTextArea rSyntaxTextArea = new RSyntaxTextArea();
        rSyntaxTextArea.setFont(font);

        if(syntaxMap.containsKey(fileExtension)) {
            rSyntaxTextArea.setSyntaxEditingStyle(syntaxMap.get(fileExtension));
        }
        theme.apply(rSyntaxTextArea);
        rSyntaxTextArea.setCurrentLineHighlightColor(Color.BLACK);
        rSyntaxTextArea.setSelectionColor(Color.BLUE);
        rSyntaxTextArea.setMarkAllHighlightColor(new Color(0x0D293E));
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
                                if(_tabbedPaneAction!=null){
                                    _tabbedPaneAction.onAction("file_saved",filepath1);
                                }
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

        rSyntaxTextArea.registerKeyboardAction(e->{
            //get selected text and send to _tabbedPane_action
            String selection = getSelectionType(rSyntaxTextArea.getText(),rSyntaxTextArea.getSelectionStart(),rSyntaxTextArea.getSelectionEnd());
            _tabbedPaneAction.onAction("action_navigate",selection);
        },KeyStroke.getKeyStroke(KeyEvent.VK_B,InputEvent.CTRL_DOWN_MASK),JComponent.WHEN_FOCUSED);

        rSyntaxTextArea.registerKeyboardAction(e->{
            //get selected text and send to _tabbedPane_action
            String selection = getSelectionType(rSyntaxTextArea.getText(),rSyntaxTextArea.getSelectionStart(),rSyntaxTextArea.getSelectionEnd());
            _tabbedPaneAction.onAction("action_rename",selection);
        },KeyStroke.getKeyStroke(KeyEvent.VK_R,InputEvent.CTRL_DOWN_MASK),JComponent.WHEN_FOCUSED);


        rSyntaxTextArea.setFont(font);
        RModernScrollPane rTextScrollPane = new RModernScrollPane(rSyntaxTextArea);
        rTextScrollPane.setLineNumbersEnabled(true);
        JPanel tabContentPanel = new JPanel(new BorderLayout());
        JPanel searchPanel = getSearchPannel(rSyntaxTextArea);
        searchPanel.setVisible(false);

        searchPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE,25));
        searchPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE,25));
        //searchPanel.setBackground(Color.BLACK);
        tabContentPanel.add(searchPanel,BorderLayout.SOUTH);
        tabContentPanel.add(rTextScrollPane,BorderLayout.CENTER);
        //rTextScrollPane.setBorder(BorderFactory.createEmptyBorder());

        rSyntaxTextArea.registerKeyboardAction(
                e -> {
                    Object obj = e.getSource();
                    if (obj instanceof RSyntaxTextArea) {
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

        //----------------------------------------
        final JPopupMenu popupmenu = new JPopupMenu("Edit");
        JMenuItem cut = new JMenuItem("Close Others");
        cut.addActionListener(e -> {
            while(getTabCount()>1) {
                for (int i = 0; i < getTabCount(); i++) {
                    if (!getTitleAt(i).equals(hoverTab)) {
                        removeTabAt(i);
                    }
                }
            }
        });
        JMenuItem copy = new JMenuItem("Close All");
        copy.addActionListener(e -> removeAll());
        popupmenu.add(cut); popupmenu.add(copy);

        JPanel pnlTab = new JPanel();
        pnlTab.putClientProperty("id",filepath);
        pnlTab.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                JPanel panel = (JPanel) e.getSource();
                String title = (String) panel.getClientProperty("id");
                setSelectedIndex(indexOfTab(title));
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                JPanel panel = (JPanel) e.getSource();
                hoverTab = (String) panel.getClientProperty("id");
                System.out.println(hoverTab);
            }
        });
        pnlTab.setComponentPopupMenu(popupmenu);
        pnlTab.setLayout(new BoxLayout(pnlTab,BoxLayout.X_AXIS));
        pnlTab.setOpaque(false);
        pnlTab.setBackground(Color.lightGray);

        JLabel lblTitle = new JLabel(filename);
        Icon icon = getFileIcon(filename);
        if(icon!=null){
            lblTitle.setIcon(getFileIcon(filename));
        }


        CloseButton btnClose = new CloseButton();
        btnClose.putClientProperty("id",filepath);

        btnClose.addActionListener(e -> {
            Object obj = e.getSource();
            if (obj instanceof JButton jButton) {
                String strID = (String) jButton.getClientProperty("id");
                this.removeTabAt(this.indexOfTab(strID));
            }
        });
        pnlTab.setToolTipText(filepath);
        pnlTab.add(lblTitle);
        pnlTab.add(createHorizontalStrut(5));
        pnlTab.add(btnClose);
        this.setTabComponentAt(index, pnlTab);
        //----------------------------------------
        this.setSelectedIndex(index);
        if(!stringsrc){
            textAreaStringHashMap.put(filepath,rSyntaxTextArea); //add to hash map.
        }
    }
    public void reloadFiles(String[] ar){
        String data;
        for(String s:ar){
            if(textAreaStringHashMap.containsKey(s)){
                try {
                    data = Files.readString(Paths.get(s));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                textAreaStringHashMap.get(s).setText(data);
            }
        }
    }
    private Icon getFileIcon(String name){
        int index = name.lastIndexOf('.');
        String iconpath ;
        if(index>0) {
            switch (name.substring(index)){
                case ".xml" -> iconpath = "../../fileicons/xml.png";
                case ".yml" -> iconpath = "../../fileicons/yml.png";
                case ".smali" -> iconpath = "../../fileicons/smali.png";
                default -> iconpath ="";
            }
        }else {
            iconpath="";
        }
        try {
            if(!iconpath.equals("")) {
                InputStream in = this.getClass().getResourceAsStream(iconpath);
                if(in==null){return null;}
                Image image = ImageIO.read(in);
                return new SmoothIcon(image,16,16);
            }else{
                return null;
            }
        } catch (IOException e) {
            return null;
        }
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
        rt.putClientProperty("curr",0);
        Color themeColor = new Color(40, 40, 80);
        JPanel jPanel = new JPanel(new BorderLayout());
        JPanel searchBox = new JPanel();
        JPanel resultBox = new JPanel();
        JPanel replaceBox = new JPanel();

        //jPanel.setLayout(new BoxLayout(jPanel,BoxLayout.X_AXIS));
        searchBox.setLayout(new BoxLayout(searchBox,BoxLayout.X_AXIS));
        resultBox.setLayout(new BoxLayout(resultBox,BoxLayout.X_AXIS));
        replaceBox.setLayout(new BoxLayout(replaceBox,BoxLayout.X_AXIS));
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
        searchTextBox.addActionListener(e -> {
            rt.setCaretPosition(0);
            rt.putClientProperty("curr",0);
            resultLabel.setText(findText(rt,searchTextBox.getText(),Cc.isSelected(),regex.isSelected(),W.isSelected(),true));

        });
        Cc.addActionListener(e -> {
            rt.setCaretPosition(0);
            rt.putClientProperty("curr",0);
            resultLabel.setText(findText(rt,searchTextBox.getText(),Cc.isSelected(),regex.isSelected(),W.isSelected(),true));

        });
        W.addActionListener(e -> {
            rt.setCaretPosition(0);
            rt.putClientProperty("curr",0);
            resultLabel.setText(findText(rt,searchTextBox.getText(),Cc.isSelected(),regex.isSelected(),W.isSelected(),true));

        });
        regex.addActionListener(e -> {
            rt.setCaretPosition(0);
            rt.putClientProperty("curr",0);
            resultLabel.setText(findText(rt,searchTextBox.getText(),Cc.isSelected(),regex.isSelected(),W.isSelected(),true));
            W.setEnabled(!regex.isSelected());
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
        sUp.addActionListener(e -> resultLabel.setText(findText(rt,searchTextBox.getText(),Cc.isSelected(),regex.isSelected(),W.isSelected(),false)));
        JButton sDw = getJButton("↓");
        sDw.addActionListener(e -> resultLabel.setText(findText(rt,searchTextBox.getText(),Cc.isSelected(),regex.isSelected(),W.isSelected(),true)));
        JButton filter = getJButton("Filter");
        resultBox.add(createHorizontalStrut(20));
        resultBox.add(resultLabel);
        resultBox.add(sDw);
        resultBox.add(sUp);
        resultBox.add(filter);
        jPanel.add(resultBox,BorderLayout.CENTER);

        //Replace Box.
        JTextField inputReplace = new JTextField();
        inputReplace.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        JButton replaceSingle = getJButton("Replace");
        replaceSingle.addActionListener(e -> resultLabel.setText(replaceText(rt,searchTextBox.getText(),inputReplace.getText(),Cc.isSelected(),regex.isSelected(),W.isSelected(),true)));
        JButton replaceAll = getJButton("ReplaceAll");
        replaceAll.addActionListener(e -> resultLabel.setText(replaceAll(rt,searchTextBox.getText(),inputReplace.getText(),Cc.isSelected(),regex.isSelected(),W.isSelected(),true)));
        replaceBox.add(inputReplace);
        replaceBox.add(replaceSingle);
        replaceBox.add(replaceAll);


        CloseButton close = new CloseButton();
        close.addActionListener(e -> {
            jPanel.setVisible(false);
            rt.putClientProperty("lastS",searchTextBox.getText());
            searchTextBox.setText("");
        });
        replaceBox.add(close);
        replaceBox.setPreferredSize(new Dimension(400,Integer.MAX_VALUE));
        replaceBox.setMaximumSize(new Dimension(400,Integer.MAX_VALUE));
        replaceBox.setBackground(themeColor);
        jPanel.add(replaceBox,BorderLayout.EAST);


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

        return curr + "/" + searchResult.getMarkedCount();
        //System.out.println(searchResult.getMarkedCount());
    }
    public static String replaceText(RSyntaxTextArea rt,String what,String replaceWith,boolean matchCase,boolean regex,boolean wholeWord,boolean forward){
        SearchContext context = new SearchContext();
        context.setSearchFor(what);
        context.setMatchCase(matchCase);
        context.setRegularExpression(regex);
        context.setSearchForward(forward);
        context.setWholeWord(wholeWord);
        context.setReplaceWith(replaceWith);
        SearchResult searchResult = SearchEngine.replace(rt, context);

        return searchResult.getMarkedCount() + "";
        //System.out.println(searchResult.getMarkedCount());
    }
    public static String replaceAll(RSyntaxTextArea rt,String what,String replaceWith,boolean matchCase,boolean regex,boolean wholeWord,boolean forward){
        SearchContext context = new SearchContext();
        context.setSearchFor(what);
        context.setMatchCase(matchCase);
        context.setRegularExpression(regex);
        context.setSearchForward(forward);
        context.setWholeWord(wholeWord);
        context.setReplaceWith(replaceWith);
        SearchResult searchResult = SearchEngine.replaceAll(rt, context);

        return searchResult.getMarkedCount() + "";
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
    boolean isBinaryFile(File f) {
        String type = null;
        try {
            type = Files.probeContentType(f.toPath());
        } catch (IOException e) {
            return false;
        }
        if (type == null) {
            return false;
        } else return !type.startsWith("text");
    }
    private String getSelectionType(String data,int sel_start,int sel_end){
        //get the line number. and check if multiline not selected. + check if selection length > 0
        int chrNo = 0;
        String selectedText = "";
        selectedText = data.substring(sel_start,sel_end);
        System.out.println("Selected text : " + selectedText );
        String[] lines = data.split("\r\n");
        for (String line : lines) {
            chrNo += line.length() + 2;
            if (chrNo > sel_start) {
                //this is the line. let's confirm.
                if (!line.contains(selectedText)) {
                    System.out.println("Algorithm failed : getSelectionType or Multiline text");
                    System.out.println(line);
                    return "";
                }
                //FIND CLASS NAME OF CURRENT FILE
                Pattern pattern_api = Pattern.compile("\\.class(.*)\\s+(L.*;)");
                Matcher matcher_api = pattern_api.matcher(data);
                String className = "";
                while (matcher_api.find()) {
                    className = matcher_api.group(2);
                }

                //FIND IF DEFINITION IS ON CLICKED LINE
                if (line.trim().startsWith(".method")) {
                    return className + "->" + selectedText + "(";
                } else if (line.trim().startsWith(".field")) {
                    return className + "->" + selectedText + ":";
                } else if (line.trim().startsWith(".class")) {
                    return className;
                }

                //just find space|comma to left, and find new line|(|: to the right for get text between it.
                int S = 0;
                int LineStart = chrNo - line.length() - 2;
                for (int i = sel_start; i > LineStart; i--) {
                    char c = data.charAt(i);
                    if (c == ' ' || c == ',') {
                        S = i;
                        break;
                    }
                }
                int E = 0;
                for (int i = sel_end; i < chrNo; i++) {
                    char c = data.charAt(i);
                    if (c == '\r' || c == '(' || c == ':') {
                        E = i;
                        break;
                    }
                }
                String text = data.substring(S+1, E+1);
                System.out.println(text);
                return text;
            }
        }
        return "";
    }
}
