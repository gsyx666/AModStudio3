package mbpcm.ui;

import com.formdev.flatlaf.ui.FlatToggleButtonUI;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

public class uiUtils {
    static class RoundedBorder2 extends LineBorder {

        private final int radius;
        RoundedBorder2(Color c, int thickness, int radius) {
            super(c, thickness, true);
            this.radius = radius;
        }
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            // adapted code of LineBorder class
            if ((this.thickness > 0) && (g instanceof Graphics2D g2d)) {
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                Color oldColor = g2d.getColor();
                g2d.setColor(this.lineColor);

                Shape outer;
                Shape inner;

                int offs = this.thickness;
                int size = offs + offs;
                outer = new RoundRectangle2D.Float(x, y, width, height, 0, 0);
                inner = new RoundRectangle2D.Float(x + offs, y + offs, width - size, height - size, radius, radius);
                Path2D path = new Path2D.Float(Path2D.WIND_EVEN_ODD);
                path.append(outer, false);
                path.append(inner, false);
                g2d.fill(path);
                g2d.setColor(oldColor);
            }
        }
    }
    private static class RoundedBorder implements Border {
        private int radius;
        RoundedBorder(int radius) {this.radius = radius;}
        public Insets getBorderInsets(Component c) {return new Insets(this.radius+1, this.radius+1, this.radius+2, this.radius);}
        public boolean isBorderOpaque() {return true;}
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {g.drawRoundRect(x, y, width-1, height-1, radius, radius);}
    }
    public static JPanel getCloseButton(){
        JPanel jPanel = new JPanel();
        JButton closeBtn = new JButton("x");
        //closeBtn.setMaximumSize(new Dimension(10,10));
        //closeBtn.setPreferredSize(new Dimension(10,10));
        closeBtn.setBorder(new uiUtils.RoundedBorder2(jPanel.getBackground(),1,15));
        //closeBtn.setBorder(new RoundedBorder(5));
        jPanel.add(closeBtn);
        return jPanel;
    }
    public static JButton getJButton(String text){
        JButton jButton = new JButton(text);
        jButton.setBorder(BorderFactory.createEmptyBorder(6,5,6,5));
        jButton.setBackground(UIManager.getColor("Panel.background"));
        return jButton;
    }
    public static JToggleButton getJToggleButton(String text){
        JToggleButton jButton = new JToggleButton(text);
        jButton.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
        //jButton.setBackground(UIManager.getColor("Panel.background"));
        jButton.setMargin(new Insets(0,5,0,5));;
        return jButton;
    }
    public static Component getComponentByName(Container parent,String name) {
        java.util.List<Component> clist = new ArrayList<>();
        listAllComponentsIn(parent,clist);
        for (Component c : clist) {
            System.out.println(c.getName());
            String s = c.getName();
            if(s!=null){
                if(s.equals(name)){
                    return c;
                }
            }
        }
        return null;
    }
    public static void listAllComponentsIn(Container parent,java.util.List<Component> components)
    {
        for (Component c : parent.getComponents()) {
            components.add(c);
            if (c instanceof Container) {
                listAllComponentsIn((Container) c,components);
            }
        }
    }
}
