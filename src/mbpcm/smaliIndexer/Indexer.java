package mbpcm.smaliIndexer;

import com.formdev.flatlaf.FlatDarculaLaf;

import javax.json.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class Indexer {
    public String basepath;
    public JsonArray db;
    public String dbPath;
    JFrame f;
    JTextPane label;
    private static volatile Indexer instance = null;
    public static Indexer getInstance() {
        if (instance == null) {
            synchronized(Indexer.class) {
                if (instance == null) {
                    instance = new Indexer();
                }
            }
        }
        return instance;
    }
    private Indexer() {
    }
    public void Init(String basepath){
        this.basepath = basepath;
        dbPath = this.basepath + "\\searchIndex.txt";
        if(new File(dbPath).exists()){
            loadJSONDatabase(dbPath);
        }else{
            RecreateDatabase(this.basepath);
        }
    }
    void loadJSONDatabase(String database){
        if(dbPath.equals(database) && db!=null){
            System.out.println("Database Already Loaded");
            return;
        }
        if(!new File(database).exists()){
            System.out.println("Database still not exists... something went terribly wrong.. :(");
            return;
        }
        dbPath = database;
        InputStream fis;
        try {
            fis = new FileInputStream(database);
            JsonReader reader = Json.createReader(fis);
            db = reader.readArray();
            reader.close();
            System.out.println("Database Loaded : " + db.size() + " Classes Total");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public void RecreateDatabase(String basepath){
        FlatDarculaLaf.setup();
        showProgressbar();
        label.setText("Indexing....\n" + basepath);
        CompletableFuture.runAsync(() -> {
            walkAll(basepath);
            loadJSONDatabase(dbPath);
        });
    }
    public void walkAll(String basepath){
        long oldTime  = System.currentTimeMillis();
        AtomicInteger total = new AtomicInteger();
        JsonArrayBuilder database = Json.createArrayBuilder();
        try {
            Files.walk(Paths.get(basepath))
                    .filter(p -> p.toString().endsWith(".smali"))
                    .forEach(p ->{
                        total.getAndIncrement();
                        //label.setText(p.toString());
                        database.add(new smaliParser(p.toString()).c.build());
                    });
            Files.writeString(Paths.get(basepath + "\\searchIndex.txt"),database.build().toString());
            System.out.println("DONE : total = " + total + " TakenTime: " + (System.currentTimeMillis()-oldTime)/1000 + " sec");
            f.setVisible(false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    void showProgressbar(){
        if(f==null){
            //bugExperiment();
            f = new JFrame("AMod Studio3 Indexing...");
            f.getContentPane().setLayout(new BoxLayout(f.getContentPane(),BoxLayout.Y_AXIS));
            Rectangle gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
            //Rectangle winSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
            double width = gd.getWidth()*1/2;
            double height = gd.getHeight()*1/10;
            f.setSize((int)width, (int)height);
            //f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            //f.setUndecorated(true);

            label = new JTextPane();

            label.setEnabled(false);
            JProgressBar jProgressBar = new JProgressBar();
            jProgressBar.setIndeterminate(true);
            f.add(label,BorderLayout.CENTER);
            f.add(jProgressBar,BorderLayout.SOUTH);
            f.setLocationRelativeTo(null); //center screen
            f.setVisible(true);
            f.setState(JFrame.MAXIMIZED_BOTH); //start Maximized
        }else{
            f.setVisible(true);
        }
    }
    public void updateDatabase(HashMap<Integer,String> affectedList){
        List<String> updatedFilePathList = new ArrayList<>();
        JsonArrayBuilder builder = Json.createArrayBuilder();

        // if old db contains filepath which also exists in affectedList: that means they need to be updated.
        int dbSize = db.size();
        for(int i=0;i<dbSize;i++){
            JsonObject jo = db.getJsonObject(i);
            String filepath = jo.getString("filepath");
            if(affectedList.containsValue(filepath)){
                builder.add(new smaliParser(filepath).c.build());
                updatedFilePathList.add(filepath); // after it we will not use this to add new files.
            }else{
                builder.add(jo);
            }
        }

        // add as new files : which did not match in database : from hashMap
        for(Map.Entry<Integer, String> s: affectedList.entrySet()){
            String filepath = s.getValue();
            if(!updatedFilePathList.contains(filepath)){
                builder.add(new smaliParser(filepath).c.build());
            }
        }
        //rewrite database.
        try {
            db = builder.build();
            Files.writeString(Paths.get(dbPath),db.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
