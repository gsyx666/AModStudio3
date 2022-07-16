import java.awt.event.ActionEvent;

public class mod_defaultMenus extends super_MenuInterface {
    mod_defaultMenus(Editor _mainWin) {
        super(_mainWin);
        createMenu("Help","About");
    }
    @Override
    public void onMenuItemClick(String menuName, ActionEvent actionEvent){
        if(menuName.equals("About")){
            utils.MessageBox("","AMod Studio 1.5\nDeveloper: ManojBhakarPCM\nOpenSource: github.com");
        }
    }
}
