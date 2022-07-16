import java.util.HashMap;

@SuppressWarnings("ALL")
public class mod_adbUtils extends super_MenuInterface {
    Editor main;
    HashMap<String,String> AdbCommands = new HashMap<>();
    mod_adbUtils(Editor _mainWin) {
        super(_mainWin);
        main = _mainWin;
        //$p = package ; $a = activity. $i = parameter1 ; $j = parameter2
        AdbCommands.put("launch","adb shell am start -n $p/.$a");
        //AdbCommands.put("get_activities","aapt dump xmltree <APK> AndroidManifest.xml");
        AdbCommands.put("current_activity","adb shell dumpsys window windows | grep 'mCurrentFocus'");
        AdbCommands.put("activity_record","adb shell dumpsys window windows | grep 'mActivityRecord'");
        AdbCommands.put("send_intent","adb shell am broadcast -a $i");
        AdbCommands.put("start_activity","adb shell am start -a android.intent.action.MAIN -n $p/$a");
        AdbCommands.put("find_app","adb shell pm path <package>");
        AdbCommands.put("start_bypackage","adb shell monkey -p $p 1");
        AdbCommands.put("get_activies","adb shell dumpsys package | grep -i \"$p\" |grep Activity");
        AdbCommands.put("get_activity_only","adb shell dumpsys package | grep -Eo \"^[[:space:]]+[0-9a-f]+[[:space:]]+com.whatsapp/[^[:space:]]+\" | grep -oE \"[^[:space:]]+$\"");
        AdbCommands.put("get_package_list","adb shell pm list packages -f");
        AdbCommands.put("get_main_activity","adb shell \"cmd package resolve-activity --brief com.android.gallery3d | tail -n 1\"");
        AdbCommands.put("uninstall_package","adb uninstall $p");

    }


}
