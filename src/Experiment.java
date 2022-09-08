import com.formdev.flatlaf.FlatDarkLaf;
import jadx.core.utils.GsonUtils;
import org.yaml.snakeyaml.Yaml;

import javax.json.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.awt.Component.LEFT_ALIGNMENT;
import static javax.swing.Box.createHorizontalStrut;
import static javax.swing.Box.createVerticalStrut;
import static mbpcm.ui.uiUtils.getJToggleButton;

public class Experiment {
    JPanel resultPanel = new JPanel();
    JFrame f;
    public static void main(String[] args) {
        FlatDarkLaf.setup();
        new Experiment();
    }
    Experiment(){

        f = new JFrame("AMod Studio3 Search");
        f.getContentPane().setLayout(new BoxLayout(f.getContentPane(),BoxLayout.Y_AXIS));
        Rectangle gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        //Rectangle winSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        double width = gd.getWidth()*1/2;
        double height = gd.getHeight()*1/2;
        f.setSize((int)width, (int)height);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel searchBox = getSearchPannel();
        searchBox.setMaximumSize(new Dimension(Integer.MAX_VALUE,25));
        f.add(searchBox,BorderLayout.NORTH);
        resultPanel.setLayout(new BoxLayout(resultPanel,BoxLayout.Y_AXIS));
        f.add(new JScrollPane(resultPanel));
        f.setLocationRelativeTo(null); //center screen
        f.setVisible(true);
        f.setState(JFrame.MAXIMIZED_BOTH); //start Maximized
        for(int i=0;i<10;i++){
            //add_result("New But LONG LONG TEXT...");
        }
    }
    private void add_result(String s){
        JPanel jPanel = new JPanel(new BorderLayout());
        JLabel jLabel = new JLabel();
        jLabel.setText(s);
        jPanel.add(jLabel);
        jPanel.setBackground(Color.BLUE);
        resultPanel.add(jPanel,LEFT_ALIGNMENT);
        resultPanel.add(createVerticalStrut(10));
        f.setVisible(true);
    }
    private JPanel getSearchPannel(){
        JToggleButton longs;
        JToggleButton Apis;
        JToggleButton strings;
        JToggleButton methods;
        JToggleButton fields;
        JToggleButton interfaces;
        JToggleButton src;
        JToggleButton superclass;
        JToggleButton className;
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
                add_result("hellow there");
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
                resultPanel.removeAll();
                resultPanel.repaint();
            }
        });
        searchBox.add(jButton);
        jPanel.add(searchBox,BorderLayout.WEST);
        return jPanel;
    }


}