package mbpcm.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;


public class CloseButton extends JButton implements MouseListener
{
    public String text_= "x";
    public int gap = 4;
    public int textHeight=0;
    public int textWidth=0;
    public int bWidth = 15;
    public int bHeight = 15;
    public int arcWidth = 15;
    public int arcHeight = 15;
    boolean normal = true;
    Color foreground = new Color(0xFF656565, true);
    public CloseButton()
    {
        calculateWH();
        this.setPreferredSize(new Dimension(bWidth,bHeight));
        this.setMinimumSize(new Dimension(bWidth,bHeight));
        setContentAreaFilled(false);
        setFocusPainted(false);
        addMouseListener(this);
        setVisible(true);
        this.setMaximumSize(new Dimension(bWidth,bHeight));
    }
    public void mouseClicked(MouseEvent e) {normal = false;}
    public void mouseEntered(MouseEvent e) {normal = false;}
    public void mouseExited(MouseEvent e) {normal = true;}
    public void mousePressed(MouseEvent e) {normal = false;}
    public void mouseReleased(MouseEvent e) {normal = false;}

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        //g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        if(!normal) {
            g2.setColor(Color.GRAY);
            g2.fillRoundRect(1, 1, bWidth-3, bHeight-3, arcWidth, arcHeight);//paint background
            g2.setColor(Color.BLACK);
        }else{
            g2.setColor(foreground);
        }
        g2.drawString(text_,gap,(int)(textHeight*0.75)); //TODO: why 0.78 is magical?
    }
    public void calculateWH(){
            AffineTransform affinetransform = new AffineTransform();
            FontRenderContext frc = new FontRenderContext(affinetransform, true, true);
            Rectangle2D sb = this.getFont().getStringBounds(text_, frc);
            textWidth = (int) (sb.getWidth());
            textHeight = (int) (sb.getHeight());
    }
}
