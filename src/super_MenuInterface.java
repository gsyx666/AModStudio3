import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class super_MenuInterface implements ActionListener {
    Editor mainWin;
    super_MenuInterface(Editor _mainWin){
        mainWin = _mainWin;
    }

    public void createMenu(String title, String submenus){
        String[] submenuTitles = submenus.split(",");
        JMenu jMenu = new JMenu(" " + title + " ");
        for (String submenuTitle:submenuTitles) {
            JMenuItem jMenuItem = new JMenuItem(submenuTitle + "    ");
            jMenuItem.addActionListener(this);
            jMenu.add(jMenuItem);
        }
       this.mainWin.menuBar.add(jMenu);
    }
    public void addMenuItems(String mainMenu,String submenus){
        String[] submenuTitles = submenus.split(",");
        int mainMenuIndex = getMenuIndex(mainMenu);
        JMenu jMenu;
        if(mainMenuIndex==-1){
            jMenu = new JMenu(" " + mainMenu + " ");
            mainWin.menuBar.add(jMenu);
        }else{
            jMenu = mainWin.menuBar.getMenu(mainMenuIndex);
        }

        for (String submenuTitle:submenuTitles) {
            JMenuItem jMenuItem = new JMenuItem(submenuTitle + "   ");
            jMenuItem.addActionListener(this);
            jMenu.add(jMenuItem);
        }
    }
    public void addSubMenuItems(String mainMenu,String subMenu,String submenus){
        //1. find MainMenu. if not found then Create.

        //2. find Submenu. if not found then Create.
        //3. create submenu items.

        String[] submenuTitles = submenus.split(",");
        int mainMenuIndex = getMenuIndex(mainMenu);
        JMenu jMainMenu;
        JMenu jSubMenu;
        if(mainMenuIndex==-1){
            jMainMenu = new JMenu(" " + mainMenu + " ");
            jSubMenu = new JMenu(subMenu);
            jMainMenu.add(jSubMenu);
            mainWin.menuBar.add(jMainMenu);
        }else{
            jMainMenu = mainWin.menuBar.getMenu(mainMenuIndex);
            jSubMenu = findSubMenu(subMenu,jMainMenu);
            if(jSubMenu==null){
                jSubMenu = new JMenu(subMenu);
                jMainMenu.add(jSubMenu);
            }
        }

        for (String submenuTitle:submenuTitles) {
            JMenuItem jMenuItem = new JMenuItem(submenuTitle + "   ");
            jMenuItem.addActionListener(this);
            jSubMenu.add(jMenuItem);
        }

    }
    public int getMenuIndex(String name){
        for(int i=0;i<mainWin.menuBar.getMenuCount();i++){
            String menuName = mainWin.menuBar.getMenu(i).getLabel();
            if(menuName!=null) {
                if (menuName.trim().equals(name)){
                    return i;
                }
            }
        }
        return -1;
    }
    public JMenu findSubMenu(String name,JMenu jMenu){
        for (MenuElement j:jMenu.getSubElements()){
            if(j instanceof JMenu){
                if(((JMenu) j).getLabel().equals(name)){
                    return (JMenu)j;
                }
            }
        }
        return null;
    }
    public void onMenuItemClick(String menuName,ActionEvent actionEvent){
            //abstract. just to override.
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        onMenuItemClick(e.getActionCommand().trim(),e);
    }
    public JButton getIButton( String pngres){
        JButton run = new JButton(utils.getImageIconFromRes(pngres));
        run.setBackground(new Color(0,true));
        run.setMaximumSize(new Dimension(20,20));
        run.setBorder(BorderFactory.createRaisedSoftBevelBorder());
        run.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                run.setBackground(new Color(130, 130, 133));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                run.setBackground(new Color(0,true));
            }
        });
        return run;
    }
}
