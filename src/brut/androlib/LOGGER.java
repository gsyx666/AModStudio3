package brut.androlib;

import java.util.logging.Level;

public class LOGGER {
    public static void severe(String msg) {System.out.println("SEVERE : " + msg);}
    public static void warning(String msg) {System.out.println("WARNING : " + msg);}
    public static void info(String msg) {
        System.out.println("INFO : " + msg);
    }
    public static void config(String msg) {
        System.out.println("CONFIG : " + msg);
    }
    public static void fine(String msg) {
        System.out.println("OK : " + msg);
    }
    public static void finer(String msg) {
        System.out.println("OK : " + msg);
    }
    public static void finest(String msg) {
        System.out.println("OK : " + msg);
    }
    public static void log(Level level,String msg,Throwable throwable) {
        System.out.println("OK : " + msg);
    }
}
