package mbpcm.ui;

import javax.swing.*;

public interface I_Window {
    public abstract JComponent getWindow();
    public abstract JToggleButton getButton();

    public abstract String getWindowName();

    public abstract int getPrefPosition();

    public abstract void onSettingChanged(String a,String b,Object c);
}
