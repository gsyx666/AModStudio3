import com.formdev.flatlaf.FlatDarkLaf;
import jadx.core.utils.GsonUtils;
import mbpcm.smaliIndexer.Indexer;
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
        new Experiment();
    }
    Experiment(){
        long old = System.currentTimeMillis();
        Indexer indexer = Indexer.getInstance();
        indexer.Init("H:\\pikashow2_d");

        JsonObject cls = getClassObjByClassName("Ltt;", indexer.db);
        if(cls!=null){
            System.out.println(cls.getString("filepath"));
        }
    }
    private JsonObject getClassObjByClassName(String className,JsonArray db){
        int size = db.size();
        int clsNo = -1;
        for(int i=0;i<size;i++) {
            JsonObject smali = db.getJsonObject(i);
            String clsName = smali.getString("name");
            if(clsName.equals(className)){
                clsNo = i;
                break;
            }
        }
        if(clsNo<0){
            System.out.println("Class Not Found in Database");
            return null;
        }
        return db.getJsonObject(clsNo);
    }
}