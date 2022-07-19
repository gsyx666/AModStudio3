import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Experiment {

    public static void main(String[] args) {
        new Experiment();

    }
    Experiment(){
      System.out.println(getCurrEmuTime());
    }
    long getCurrEmuTime(){
        String ret = utils.runFastTool(new String[]{"D:\\Program Files\\Nox\\bin\\adb.exe","shell","date"});
        ret = ret.replace(" IST "," ");
        long emuTime = getMillsFromString(ret,"EEE MMM dd HH:mm:ss yyyy");
        String current = new SimpleDateFormat("MM-dd HH:mm:ss.SSS").format(emuTime);
        return getMillsFromString(current,"MM-dd HH:mm:ss.SSS");
    }
    long parseMills(String date){
        try {
            return new SimpleDateFormat("MM-dd HH:mm:ss.SSS").parse(date).getTime();
        } catch (ParseException e) {
            return 0;
        }
    }
    long getMillsFromString(String date,String format){
        try {
            return new SimpleDateFormat(format).parse(date).getTime();
        } catch (ParseException e) {
            return 0;
        }
    }

}