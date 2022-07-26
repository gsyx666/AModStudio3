package mbpcm.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;


public class IButton extends JButton implements MouseListener
{
    Image image_ = null;
    String text_= null;
    String res_ = "";
    int paddingH = 6;
    int paddingV = 5;
    int gap = 4;
    int hoverDarkness = 20; //Normal + ....
    int pressDarkness = 20;
    int iWidth = 0;
    int iHeight = 0;
    int textHeight=0;
    int textWidth=0;
    int bWidth = 24;
    int bHeight = 24;
    int arcWidth = 7;
    int arcHeight = 7;
    Color backgroundNormal = UIManager.getColor("Panel.background");
    Color hover = new Color(backgroundNormal.getRed()+ hoverDarkness,backgroundNormal.getGreen()+ hoverDarkness,backgroundNormal.getBlue()+ hoverDarkness);
    Color pressed = new Color(hover.getRed()+ pressDarkness,hover.getGreen()+ pressDarkness,hover.getBlue()+ pressDarkness);
    public IButton(Image image, String text)
    {
        if(text!=null){if(text.equals("")){text_=null;}} //Empty = null}
        text_ = text;
        image_ = image;
        calculateWH();

        setContentAreaFilled(false);
        setFocusPainted(false);
        addMouseListener(this);
        setVisible(true);
        this.setMaximumSize(new Dimension(bWidth,bHeight));
        this.setBackground(backgroundNormal); //new Color(0,true)
    }
    public IButton(Image res){
        this(res,null);
    }
    public void mouseClicked(MouseEvent e) {this.setBackground(hover);}
    public void mouseEntered(MouseEvent e) {this.setBackground(hover);}
    public void mouseExited(MouseEvent e) {this.setBackground(backgroundNormal);}
    public void mousePressed(MouseEvent e) {this.setBackground(pressed);}
    public void mouseReleased(MouseEvent e) {this.setBackground(backgroundNormal);}

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int width = getWidth();
        int height = getHeight();
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, width-1, height-1, arcWidth, arcHeight);//paint background
        if(image_!=null){
            g2.drawImage(image_, paddingH, (bHeight - iHeight) / 2, null);
        }
        if(text_!=null){
            g2.setColor(getForeground());
            g2.drawString(text_,paddingH + gap + iWidth,paddingV + (int)(textHeight*0.75)); //TODO: why 0.78 is magical?
        }
    }
    private void calculateWH(){
        int W = 0;int H=0;

        if(image_!=null){
            iWidth = image_.getWidth(null);
            iHeight = image_.getHeight(null);
        }
        if(text_!=null) {
            AffineTransform affinetransform = new AffineTransform();
            FontRenderContext frc = new FontRenderContext(affinetransform, true, true);
            Rectangle2D sb = this.getFont().getStringBounds(text_, frc);
            textWidth = (int) (sb.getWidth());
            textHeight = (int) (sb.getHeight());
        }
        bHeight = Math.max(iHeight,textHeight) + paddingV*2;
        bWidth = iWidth + textWidth + paddingH*2;
        if(image_!=null && text_!=null){
            bWidth += gap;
        }
        if(image_==null & text_!=null){
            bWidth += paddingH;
        }

    }
}
