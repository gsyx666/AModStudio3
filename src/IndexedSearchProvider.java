import com.formdev.flatlaf.FlatDarculaLaf;
import mbpcm.ui.I_Window;

import javax.json.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static javax.swing.Box.createHorizontalStrut;
import static javax.swing.Box.createVerticalStrut;
import static mbpcm.ui.uiUtils.getJToggleButton;

public class IndexedSearchProvider implements I_Window {
    //TODO: find good Location for indexJSON data.
    //TODO: createIndex on start if not exists
    //TODO: on file_save event - update indexData. only if file structure is changed.

    SearchOptions searchOptions;
    String folder = "";
    String whatt = "";
    JsonArray db;
    JToggleButton longs;
    JToggleButton Apis;
    JToggleButton strings;
    JToggleButton methods;
    JToggleButton fields;
    JToggleButton interfaces;
    JToggleButton src;
    JToggleButton superclass;
    JToggleButton className;
    JPanel results = new JPanel();
    JFrame f;
    Editor editor_;
    public static void main(String[] args){
        FlatDarculaLaf.setup();
        //new IndexedSearchProvider();
    }
    IndexedSearchProvider(Editor editor){
        editor_ = editor;
        //createUI();
        //whatt = "firebase";
        //folder = "H:\\splitApks\\Garuda [v4.0.7]-split\\base\\smali_classes2\\in\\gov\\eci\\garuda";
        //System.out.println("DONE");
        //f.setVisible(true);
    }
    void loadJSONDatabase(String database){
        InputStream fis = null;
        try {
            fis = new FileInputStream(database);
            JsonReader reader = Json.createReader(fis);
            db = reader.readArray();
            reader.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    SearchOptions createFromUI(){
        SearchOptions  searchOptions = new SearchOptions();
        searchOptions.folder = folder;
        searchOptions.apis = Apis.isSelected();
        searchOptions.interfaces = interfaces.isSelected();
        searchOptions.methods = methods.isSelected();
        searchOptions.fields = fields.isSelected();
        searchOptions.longs = longs.isSelected();
        searchOptions.superclass = superclass.isSelected();
        searchOptions.strings = strings.isSelected();
        searchOptions.src = src.isSelected();
        searchOptions.classNames = className.isSelected();
        return searchOptions;
    }
    void createUI(){
        f = new JFrame("AMod Studio3 Search");
        f.getContentPane().setLayout(new BoxLayout(f.getContentPane(),BoxLayout.Y_AXIS));
        Rectangle gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        //Rectangle winSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        double width = gd.getWidth()*1/2;
        double height = gd.getHeight()*1/2;
        f.setSize((int)width, (int)height);
        //f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel searchPanel = getSearchPannel();
        searchPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE,25));
        searchPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE,25));
        results.setLayout(new BoxLayout(results,BoxLayout.Y_AXIS));

        f.add(searchPanel);
        JScrollPane jScrollPane = new JScrollPane(results);
        jScrollPane.getVerticalScrollBar().setUnitIncrement(20);
        f.add(jScrollPane);
        f.setLocationRelativeTo(null); //center screen
        f.setVisible(true);
        f.setState(JFrame.MAXIMIZED_BOTH); //start Maximized
    }
    private void addResults(String filepath, String restofdata){
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());
        //jPanel.setBackground(Color.BLACK);
        JLabel jl_filepath = new JLabel(filepath);jl_filepath.setForeground(Color.GRAY);
        JLabel jl_data = new JLabel(restofdata);jl_data.setForeground(Color.WHITE);

        jPanel.add(jl_filepath,BorderLayout.NORTH);
        jPanel.add(jl_data,BorderLayout.CENTER);
        jPanel.setBackground(Color.BLACK);
        jPanel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                editor_.settingChanged(null,"file_opened",jl_filepath.getText(),null);
            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });
        results.add(jPanel);
        results.add(createVerticalStrut(10));

        f.setVisible(true);
    }
    private JPanel getSearchPannel(){
        Color themeColor = new Color(40, 40, 80);
        JPanel jPanel = new JPanel(new BorderLayout());
        JPanel searchBox = new JPanel();
        JComboBox<String> directOptions = new JComboBox<>();
        directOptions.addItem("All");
        directOptions.addItem("PackageUse");
        directOptions.addItem("Strings");
        directOptions.addItem("MethodName");

        jPanel.setLayout(new BoxLayout(jPanel,BoxLayout.X_AXIS));
        searchBox.setLayout(new BoxLayout(searchBox,BoxLayout.X_AXIS));
        searchBox.setBackground(themeColor);

        Color backColor = searchBox.getBackground();

        fields = getJToggleButton("f");
        fields.setBackground(backColor);

        methods = getJToggleButton("( )");
        methods.setBackground(backColor);

        strings = getJToggleButton("\"\"");
        strings.setBackground(backColor);

        longs = getJToggleButton("0x");
        longs.setBackground(backColor);

        Apis = getJToggleButton("API");
        Apis.setBackground(backColor);


        className = getJToggleButton("class");
        Apis.setBackground(backColor);

        superclass = getJToggleButton("super");
        Apis.setBackground(backColor);

        interfaces = getJToggleButton("I");
        Apis.setBackground(backColor);

        src = getJToggleButton("src");
        Apis.setBackground(backColor);

        JTextField searchTextBox = new JTextField();
        searchTextBox.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {warn();}
            public void removeUpdate(DocumentEvent e) {warn();}
            public void insertUpdate(DocumentEvent e) {warn();}
            public void warn() {
                if(searchTextBox.getText().length()>2) {
                    search(searchTextBox.getText(), createFromUI(), 50);
                }else{
                    results.removeAll();
                    results.repaint();
                }
            }
        });
        searchTextBox.addActionListener(e -> {

        });

        searchTextBox.setName("txtSearch");
        searchTextBox.setFocusable(true);
        searchTextBox.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        searchTextBox.setBackground(backColor);

        searchBox.add(searchTextBox);
        searchBox.add(fields);
        searchBox.add(methods);
        searchBox.add(strings);
        searchBox.add(longs);
        searchBox.add(Apis);

        searchBox.add(createHorizontalStrut(10));

        searchBox.add(className);
        searchBox.add(superclass);
        searchBox.add(interfaces);
        searchBox.add(src);
        searchBox.add(createHorizontalStrut(10));
        //searchBox.add(directOptions);
        JButton jButton = new JButton("Clear");
        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                results.removeAll();
                results.repaint();
            }
        });
        searchBox.add(jButton);
        jPanel.add(searchBox,BorderLayout.WEST);
        return jPanel;
    }
    public void search(String what,SearchOptions searchOptions,int limit){
        results.removeAll();
        results.repaint();

        if(db==null){
            loadJSONDatabase("H:\\smaliDatabase.txt");
        }
        int size = db.size();
        int count = 0;

        for(int i=0;i<size;i++){
            if(count>limit){return;}
            JsonObject smali = db.getJsonObject(i);
            String filepath = smali.getString("filepath");
            String methodName = "";
            if(!filepath.startsWith(searchOptions.folder)){
                continue;
            }
            String out = "";
            boolean found = false;
            if(searchOptions.classNames){
                String className = smali.getString("name");
                if(className.contains(what)){
                    out += "CLASS_NAME : " + className + "<br>";
                }
            }
            if(searchOptions.superclass){
                String className = smali.getString("super");
                if(className.contains(what)){
                    out += "SUPER_CLASS : " + className + "<br>";
                }
            }
            if(searchOptions.src){
                String className = smali.getString("src");
                if(className.contains(what)){
                    out += "SUPER_CLASS : " + className + "<br>";
                }
            }
            if(searchOptions.fields){
                JsonArray fields = smali.getJsonArray("fields");
                for(int j=0;j<fields.size();j++){
                    String f = fields.getJsonObject(j).getString("name");
                    if(f.contains(what)){
                        out += "FIELD : " + f + "<br>";
                    }
                }
            }
            if(searchOptions.interfaces){
                JsonArray fields = smali.getJsonArray("implements");
                for (javax.json.JsonValue field : fields) {
                    String f = field.toString();
                    if (f.contains(what)) {
                        out += "IMPLEMENTS : " + f + "<br>";
                    }
                }
            }
            if(searchOptions.methods || searchOptions.apis || searchOptions.longs || searchOptions.strings){
                JsonArray methods = smali.getJsonArray("methods");
                for(int j=0;j<methods.size();j++){
                    JsonObject method = methods.getJsonObject(j);
                        String f = method.getString("name");
                        methodName = "METHOD : " + f + "<br>";
                        if(searchOptions.methods){
                           if(f.contains(what)){
                               methodName = "";
                               out+= "METHOD_NAME : " + f + "<br>";
                           }
                        }
                    if(searchOptions.strings) {
                        JsonArray strings = method.getJsonArray("strings");
                        for(int k=0;k<strings.size();k++){
                            String s = strings.getString(k);
                            if(s.contains(what)){
                                out += "STRING : " + s + "<br>";
                            }
                        }
                    }
                    if(searchOptions.apis) {
                        JsonArray strings = method.getJsonArray("apis");
                        for(int k=0;k<strings.size();k++){
                            String s = strings.getString(k);
                            if(s.contains(what)){
                                out += "API : " + s + "<br>";
                            }
                        }
                    }
                    if(searchOptions.longs) {
                        JsonArray strings = method.getJsonArray("longs");
                        for(int k=0;k<strings.size();k++){
                            String s = strings.getString(k);
                            if(s.contains(what)){
                                out += "LONG : " + s + "<br>";
                            }
                        }
                    }
                }
            }
            if(!out.equals("")) {
                count ++;
                addResults(filepath,  "<html>"+  methodName + out + "</html>");
                //System.out.println("\n\n\n\n-----------------------------\n" + filepath + "\n-----------------------------\n" + out);
            }
        }
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
        if(a.equals("search_win_show")){
            folder = b;
            createUI();
            f.setTitle(b);
        }
    }

    private class SearchOptions{
        public String folder = "";
        public boolean fields = false;
        public boolean methods = false;
        public boolean strings = false;
        public boolean longs = false;
        public boolean apis = false;
        public boolean classNames = false;
        public boolean superclass = false;
        public boolean interfaces = false;
        public boolean src = false;
    }
}
