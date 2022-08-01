package mbpcm.ui;

import javax.swing.*;
import java.awt.*;

public class SmoothIcon implements Icon {
    Image icon;
    public SmoothIcon(Image icon_){
        icon = icon_;
    }
    public SmoothIcon(Image icon_, int width, int height){
        icon = icon_.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }
    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.drawImage(icon,x,y,null);
    }

    @Override
    public int getIconWidth() {
        return icon.getWidth(null);
    }

    @Override
    public int getIconHeight() {
        return icon.getHeight(null);
    }
}