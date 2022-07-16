package mbpcm.ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicLabelUI;
import javax.swing.plaf.metal.MetalToggleButtonUI;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

public class ManojUI {


    public JPanel statusBar,center,contents;
    public JPanel bottomBar,leftBar,rightBar;
    public JSplitPane spV12,spV23,spH12,spH23;
    public JPanel southScrollPane,centerScrollPane,rightScrollPane,leftScrollPane,northScrollPane;
    public JToolBar toolBar;
    public JMenuBar menuBar;
    public JFrame f;
    public ManojUI() {
        f = new JFrame("AMod Studio v1.5");
        f.setSize(1200, 800);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        InitComponents(f);

        f.add(toolBar,BorderLayout.NORTH);
        f.setJMenuBar(menuBar);

        f.add(statusBar, BorderLayout.SOUTH);
        f.add(center,BorderLayout.CENTER);

        center.add(bottomBar,BorderLayout.SOUTH);
        center.add(rightBar,BorderLayout.EAST);
        center.add(leftBar,BorderLayout.WEST);
        center.add(contents,BorderLayout.CENTER);

        spV12.setBottomComponent(spV23);
        spV23.setTopComponent(spH12);
        spH12.setRightComponent(spH23);

        contents.add(spV12);

        f.setVisible(true);

    }
    private void InitComponents(JFrame f){
        menuBar = new JMenuBar();
        toolBar = new JToolBar();
        statusBar = createHorizontalBarRoot(f.getWidth());
        center = new JPanel(new BorderLayout());

        bottomBar = createHorizontalBar(center.getWidth());
        leftBar = createVerticalBar(center.getHeight());
        rightBar = createVerticalBar(center.getHeight());

        contents = new JPanel(new BorderLayout());

        spV12 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        spV23 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        spH12 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        spH23 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

    }
    public void setCenterItem(JComponent component){
        spH23.setLeftComponent(component);
    }
    public void setBottomItem(JComponent component){
        spV23.setBottomComponent(component);
    }
    public void setLeftItem(JComponent component){
        spH12.setLeftComponent(component);
    }
    public void setRightItem(JComponent component){
        spH23.setRightComponent(component);
    }
    public static JPanel createVerticalBar(int height){
        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(BorderFactory.createMatteBorder(0,1,0,1,new Color(50,50,50)));
        statusPanel.add(getVerticalButton("Manoj"));
        statusPanel.setPreferredSize(new Dimension(22, height));
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        return statusPanel;
    }


    public static JPanel createHorizontalBar(int width){
        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(BorderFactory.createLineBorder(new Color(50,50,50),1));
        statusPanel.add(new JLabel("this is Horizontal Bar...."));
        statusPanel.setPreferredSize(new Dimension(width, 22));
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        return statusPanel;
    }

    public static JPanel createHorizontalBarRoot(int width){
        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(BorderFactory.createMatteBorder(0,1,1,1,new Color(50,50,50)));
        statusPanel.add(new JLabel("      this is Horizontal Bar...."));
        statusPanel.setPreferredSize(new Dimension(width, 22));
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        return statusPanel;
    }



    public static JToggleButton getVerticalButton(String text){
        Font font = new javax.swing.plaf.FontUIResource("Calibri Light",Font.PLAIN,12);
        JToggleButton button = new JToggleButton();
        button.setUI(new MetalToggleButtonUI() {
            @Override
            protected Color getSelectColor() {
                return new Color(40, 40, 40);
            }
        });
        button.setBackground(Color.getColor("#3C3F41"));
        button.setBorder(BorderFactory.createEmptyBorder(10,10,10,5));
        TextIcon t1 = new TextIcon(button, text, TextIcon.Layout.HORIZONTAL);
        RotatedIcon r1 = new RotatedIcon(t1, RotatedIcon.Rotate.UP);
        button.setIcon(r1);
        button.setFont(font);
        return button;
    }
    public void listAllComponentsIn(Container parent)
    {
        for (Component c : parent.getComponents()) {
            System.out.println(c.getName());
            if (c instanceof Container) {
                listAllComponentsIn((Container) c);
            }
        }
    }

    public static class TextIcon implements Icon, PropertyChangeListener {
        public enum Layout {
            HORIZONTAL,
            VERTICAL;
        }

        private JComponent component;
        private Layout layout;
        private String text;
        private Font font;
        private Color foreground;
        private int padding;
        private int iconWidth;
        private int iconHeight;
        private String[] strings;
        private int[] stringWidths;
        public TextIcon(JComponent component, String text) {
            this(component, text, Layout.HORIZONTAL);
        }
        public TextIcon(JComponent component, String text, Layout layout) {
            this.component = component;
            this.layout = layout;
            setText( text );

            component.addPropertyChangeListener("font", this);
        }
        public Layout getLayout() {
            return layout;
        }
        public String getText() {
            return text;
        }
        public void setText(String text) {
            this.text = text;
            calculateIconDimensions();
        }
        public Font getFont() {
            if (font == null)
                return component.getFont();
            else
                return font;
        }
        public void setFont(Font font) {
            this.font = font;
            calculateIconDimensions();
        }
        public Color getForeground() {
            if (foreground == null)
                return component.getForeground();
            else
                return foreground;
        }
        public void setForeground(Color foreground) {
            this.foreground = foreground;
            component.repaint();
        }
        public int getPadding() {
            return padding;
        }
        public void setPadding(int padding) {
            this.padding = padding;
            calculateIconDimensions();
        }

        private void calculateIconDimensions() {
            Font font = getFont();
            FontMetrics fm = component.getFontMetrics( font );
            if (layout == Layout.HORIZONTAL) {
                iconWidth = fm.stringWidth( text ) + (padding * 2);
                iconHeight = fm.getHeight();
            }
            else if (layout == Layout.VERTICAL) {
                int maxWidth = 0;
                strings = new String[text.length()];
                stringWidths = new int[text.length()];
                for (int i = 0; i < text.length(); i++) {
                    strings[i] = text.substring(i, i + 1);
                    stringWidths[i] = fm.stringWidth( strings[i] );
                    maxWidth = Math.max(maxWidth, stringWidths[i]);
                }
                iconWidth = maxWidth + ((fm.getLeading() + 2) * 2);
                iconHeight = (fm.getHeight() - fm.getDescent()) * text.length();
                iconHeight += padding * 2;
            }
            component.revalidate();
        }
        @Override
        public int getIconWidth() {
            return iconWidth;
        }
        @Override
        public int getIconHeight() {
            return iconHeight;
        }
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D)g.create();
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Map map = (Map)(toolkit.getDesktopProperty("awt.font.desktophints"));
            if (map != null) {
                g2.addRenderingHints(map);
            }
            else
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON );

            g2.setFont( getFont() );
            g2.setColor( getForeground() );
            FontMetrics fm = g2.getFontMetrics();

            if (layout == Layout.HORIZONTAL) {
                g2.translate(x, y +	fm.getAscent());
                g2.drawString(text, padding, 0);
            }
            else if (layout == Layout.VERTICAL) {
                int offsetY = fm.getAscent() - fm.getDescent() + padding;
                int incrementY = fm.getHeight() - fm.getDescent();
                for (int i = 0; i < text.length(); i++) {
                    int offsetX = Math.round((getIconWidth() - stringWidths[i]) / 2.0f);
                    g2.drawString(strings[i], x + offsetX, y + offsetY);
                    offsetY += incrementY;
                }
            }
            g2.dispose();
        }
        public void propertyChange(PropertyChangeEvent e) {
            if (font == null)
                calculateIconDimensions();
        }
    }

    /**
     *  The manoj.ui.ManojUI.RotatedIcon allows you to change the orientation of an Icon by
     *  rotating the Icon before it is painted. This class supports the following
     *  orientations:
     *
     * <ul>
     * <li>DOWN - rotated 90 degrees
     * <li>UP (default) - rotated -90 degrees
     * <li>UPSIDE_DOWN - rotated 180 degrees
     * <li>ABOUT_CENTER - the icon is rotated by the specified degrees about its center.
     * </ul>
     */
    public static class RotatedIcon implements Icon
    {
        public enum Rotate
        {
            DOWN,
            UP,
            UPSIDE_DOWN,
            ABOUT_CENTER;
        }

        private Icon icon;

        private Rotate rotate;

        private double degrees;
        private boolean circularIcon;

        /**
         *  Convenience constructor to create a manoj.ui.ManojUI.RotatedIcon that is rotated DOWN.
         *
         *  @param icon  the Icon to rotate
         */
        public RotatedIcon(Icon icon)
        {
            this(icon, Rotate.UP);
        }

        /**
         *  Create a manoj.ui.ManojUI.RotatedIcon
         *
         *  @param icon	the Icon to rotate
         *  @param rotate  the direction of rotation
         */
        public RotatedIcon(Icon icon, Rotate rotate)
        {
            this.icon = icon;
            this.rotate = rotate;
        }

        /**
         *  Create a manoj.ui.ManojUI.RotatedIcon. The icon will rotate about its center. This
         *  constructor will automatically set the Rotate enum to ABOUT_CENTER.
         *
         *  @param icon	the Icon to rotate
         *  @param degrees   the degrees of rotation
         */
        public RotatedIcon(Icon icon, double degrees)
        {
            this(icon, degrees, false);
        }

        /**
         *  Create a manoj.ui.ManojUI.RotatedIcon. The icon will rotate about its center. This
         *  constructor will automatically set the Rotate enum to ABOUT_CENTER.
         *
         *  @param icon	the Icon to rotate
         *  @param degrees   the degrees of rotation
         *  @param circularIcon treat the icon as circular so its size doesn't change
         */
        public RotatedIcon(Icon icon, double degrees, boolean circularIcon)
        {
            this(icon, Rotate.ABOUT_CENTER);
            setDegrees( degrees );
            setCircularIcon( circularIcon );
        }

        /**
         *  Gets the Icon to be rotated
         *
         *  @return the Icon to be rotated
         */
        public Icon getIcon()
        {
            return icon;
        }

        /**
         *  Gets the Rotate enum which indicates the direction of rotation
         *
         *  @return the Rotate enum
         */
        public Rotate getRotate()
        {
            return rotate;
        }

        /**
         *  Gets the degrees of rotation. Only used for Rotate.ABOUT_CENTER.
         *
         *  @return the degrees of rotation
         */
        public double getDegrees()
        {
            return degrees;
        }

        /**
         *  Set the degrees of rotation. Only used for Rotate.ABOUT_CENTER.
         *  This method only sets the degress of rotation, it will not cause
         *  the Icon to be repainted. You must invoke repaint() on any
         *  component using this icon for it to be repainted.
         *
         *  @param degrees the degrees of rotation
         */
        public void setDegrees(double degrees)
        {
            this.degrees = degrees;
        }

        /**
         *  Is the image circular or rectangular? Only used for Rotate.ABOUT_CENTER.
         *  When true, the icon width/height will not change as the Icon is rotated.
         *
         *  @return true for a circular Icon, false otherwise
         */
        public boolean isCircularIcon()
        {
            return circularIcon;
        }

        /**
         *  Set the Icon as circular or rectangular. Only used for Rotate.ABOUT_CENTER.
         *  When true, the icon width/height will not change as the Icon is rotated.
         *
         *  @param circularIcon true for a circular Icon, false otherwise
         */
        public void setCircularIcon(boolean circularIcon)
        {
            this.circularIcon = circularIcon;
        }

    //
    //  Implement the Icon Interface
    //

        /**
         *  Gets the width of this icon.
         *
         *  @return the width of the icon in pixels.
         */
        @Override
        public int getIconWidth()
        {
            if (rotate == Rotate.ABOUT_CENTER)
            {
                if (circularIcon)
                    return icon.getIconWidth();
                else
                {
                    double radians = Math.toRadians( degrees );
                    double sin = Math.abs( Math.sin( radians ) );
                    double cos = Math.abs( Math.cos( radians ) );
                    int width = (int)Math.floor(icon.getIconWidth() * cos + icon.getIconHeight() * sin);
                    return width;
                }
            }
            else if (rotate == Rotate.UPSIDE_DOWN)
                return icon.getIconWidth();
            else
                return icon.getIconHeight();
        }

        /**
         *  Gets the height of this icon.
         *
         *  @return the height of the icon in pixels.
         */
        @Override
        public int getIconHeight()
        {
            if (rotate == Rotate.ABOUT_CENTER)
            {
                if (circularIcon)
                    return icon.getIconHeight();
                else
                {
                    double radians = Math.toRadians( degrees );
                    double sin = Math.abs( Math.sin( radians ) );
                    double cos = Math.abs( Math.cos( radians ) );
                    int height = (int)Math.floor(icon.getIconHeight() * cos + icon.getIconWidth() * sin);
                    return height;
                }
            }
            else if (rotate == Rotate.UPSIDE_DOWN)
                return icon.getIconHeight();
            else
                return icon.getIconWidth();
        }

        /**
         *  Paint the icons of this compound icon at the specified location
         *
         *  @param c The component on which the icon is painted
         *  @param g the graphics context
         *  @param x the X coordinate of the icon's top-left corner
         *  @param y the Y coordinate of the icon's top-left corner
         */
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y)
        {
            Graphics2D g2 = (Graphics2D)g.create();

            int cWidth = icon.getIconWidth() / 2;
            int cHeight = icon.getIconHeight() / 2;
            int xAdjustment = (icon.getIconWidth() % 2) == 0 ? 0 : -1;
            int yAdjustment = (icon.getIconHeight() % 2) == 0 ? 0 : -1;

            if (rotate == Rotate.DOWN)
            {
                g2.translate(x + cHeight, y + cWidth);
                g2.rotate( Math.toRadians( 90 ) );
                icon.paintIcon(c, g2,  -cWidth, yAdjustment - cHeight);
            }
            else if (rotate == Rotate.UP)
            {
                g2.translate(x + cHeight, y + cWidth);
                g2.rotate( Math.toRadians( -90 ) );
                icon.paintIcon(c, g2,  xAdjustment - cWidth, -cHeight);
            }
            else if (rotate == Rotate.UPSIDE_DOWN)
            {
                g2.translate(x + cWidth, y + cHeight);
                g2.rotate( Math.toRadians( 180 ) );
                icon.paintIcon(c, g2, xAdjustment - cWidth, yAdjustment - cHeight);
            }
            else if (rotate == Rotate.ABOUT_CENTER)
            {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setClip(x, y, getIconWidth(), getIconHeight());
                g2.translate((getIconWidth() - icon.getIconWidth()) / 2, (getIconHeight() - icon.getIconHeight()) / 2);
                g2.rotate(Math.toRadians(degrees), x + cWidth, y + cHeight);
                icon.paintIcon(c, g2, x, y);
            }

            g2.dispose();
        }
    }

    /**
     *  The manoj.ui.ManojUI.CompoundIcon will paint two, or more, Icons as a single Icon. The
     *  Icons are painted in the order in which they are added.
     *
     *  The Icons are layed out on the specified axis:
     * <ul>
     * <li>X-Axis (horizontally)
     * <li>Y-Axis (vertically)
     * <li>Z-Axis (stacked)
     * </ul>
     *
     */
    public static class CompoundIcon implements Icon
    {
        public enum Axis
        {
            X_AXIS,
            Y_AXIS,
            Z_AXIS;
        }

        public final static float TOP = 0.0f;
        public final static float LEFT = 0.0f;
        public final static float CENTER = 0.5f;
        public final static float BOTTOM = 1.0f;
        public final static float RIGHT = 1.0f;

        private Icon[] icons;

        private Axis axis;

        private int gap;

        private float alignmentX = CENTER;
        private float alignmentY = CENTER;

        /**
         *  Convenience contructor for creating a manoj.ui.ManojUI.CompoundIcon where the
         *  icons are layed out on on the X-AXIS, the gap is 0 and the
         *  X/Y alignments will default to CENTER.
         *
         *  @param icons  the Icons to be painted as part of the manoj.ui.ManojUI.CompoundIcon
         */
        public CompoundIcon(Icon... icons)
        {
            this(Axis.X_AXIS, icons);
        }

        /**
         *  Convenience contructor for creating a manoj.ui.ManojUI.CompoundIcon where the
         *  gap is 0 and the X/Y alignments will default to CENTER.
         *
         *  @param axis   the axis used to lay out the icons for painting.
         *                Must be one of the Axis enums: X_AXIS, Y_AXIS, Z_Axis.
         *  @param icons  the Icons to be painted as part of the manoj.ui.ManojUI.CompoundIcon
         */
        public CompoundIcon(Axis axis, Icon... icons)
        {
            this(axis, 0, icons);
        }

        /**
         *  Convenience contructor for creating a manoj.ui.ManojUI.CompoundIcon where the
         *  X/Y alignments will default to CENTER.
         *
         *  @param axis   the axis used to lay out the icons for painting
         *                Must be one of the Axis enums: X_AXIS, Y_AXIS, Z_Axis.
         *  @param gap    the gap between the icons
         *  @param icons  the Icons to be painted as part of the manoj.ui.ManojUI.CompoundIcon
         */
        public CompoundIcon(Axis axis, int gap, Icon... icons)
        {
            this(axis, gap, CENTER, CENTER, icons);
        }

        /**
         *  Create a manoj.ui.ManojUI.CompoundIcon specifying all the properties.
         *
         *  @param axis        the axis used to lay out the icons for painting
         *                     Must be one of the Axis enums: X_AXIS, Y_AXIS, Z_Axis.
         *  @param gap         the gap between the icons
         *  @param alignmentX  the X alignment of the icons. Common values are
         *                     LEFT, CENTER, RIGHT. Can be any value between 0.0 and 1.0
         *  @param alignmentY  the Y alignment of the icons. Common values are
         *                     TOP, CENTER, BOTTOM. Can be any value between 0.0 and 1.0
         *  @param icons       the Icons to be painted as part of the manoj.ui.ManojUI.CompoundIcon
         */
        public CompoundIcon(Axis axis, int gap, float alignmentX, float alignmentY, Icon... icons)
        {
            this.axis = axis;
            this.gap = gap;
            this.alignmentX = alignmentX > 1.0f ? 1.0f : alignmentX < 0.0f ? 0.0f : alignmentX;
            this.alignmentY = alignmentY > 1.0f ? 1.0f : alignmentY < 0.0f ? 0.0f : alignmentY;

            for (int i = 0; i < icons.length; i++)
            {
                if (icons[i] == null)
                {
                    String message = "Icon (" + i + ") cannot be null";
                    throw new IllegalArgumentException( message );
                }
            }

            this.icons = icons;
        }

        /**
         *  Get the Axis along which each icon is painted.
         *
         *  @return the Axis
         */
        public Axis getAxis()
        {
            return axis;
        }

        /**
         *  Get the gap between each icon
         *
         *  @return the gap in pixels
         */
        public int getGap()
        {
            return gap;
        }

        /**
         *  Get the alignment of the icon on the x-axis
         *
         *  @return the alignment
         */
        public float getAlignmentX()
        {
            return alignmentX;
        }

        /**
         *  Get the alignment of the icon on the y-axis
         *
         *  @return the alignment
         */
        public float getAlignmentY()
        {
            return alignmentY;
        }

        /**
         *  Get the number of Icons contained in this manoj.ui.ManojUI.CompoundIcon.
         *
         *  @return the total number of Icons
         */
        public int getIconCount()
        {
            return icons.length;
        }

        /**
         *  Get the Icon at the specified index.
         *
         *  @param index  the index of the Icon to be returned
         *  @return  the Icon at the specifed index
         *  @exception IndexOutOfBoundsException  if the index is out of range
         */
        public Icon getIcon(int index)
        {
            return icons[ index ];
        }
    //
    //  Implement the Icon Interface
    //
        /**
         *  Gets the width of this icon.
         *
         *  @return the width of the icon in pixels.
         */
        @Override
        public int getIconWidth()
        {
            int width = 0;

            //  Add the width of all Icons while also including the gap

            if (axis == Axis.X_AXIS)
            {
                width += (icons.length - 1) * gap;

                for (Icon icon : icons)
                    width += icon.getIconWidth();
            }
            else  //  Just find the maximum width
            {
                for (Icon icon : icons)
                    width = Math.max(width, icon.getIconWidth());
            }

            return width;
        }

        /**
         *  Gets the height of this icon.
         *
         *  @return the height of the icon in pixels.
         */
        @Override
        public int getIconHeight()
        {
            int height = 0;

            //  Add the height of all Icons while also including the gap

            if (axis == Axis.Y_AXIS)
            {
                height += (icons.length - 1) * gap;

                for (Icon icon : icons)
                    height += icon.getIconHeight();
            }
            else  //  Just find the maximum height
            {
                for (Icon icon : icons)
                    height = Math.max(height, icon.getIconHeight());
            }

            return height;
        }

        /**
         *  Paint the icons of this compound icon at the specified location
         *
         *  @param c The component on which the icon is painted
         *  @param g the graphics context
         *  @param x the X coordinate of the icon's top-left corner
         *  @param y the Y coordinate of the icon's top-left corner
         */
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y)
        {
            if (axis == Axis.X_AXIS)
            {
                int height = getIconHeight();

                for (Icon icon : icons)
                {
                    int iconY = getOffset(height, icon.getIconHeight(), alignmentY);
                    icon.paintIcon(c, g, x, y + iconY);
                    x += icon.getIconWidth() + gap;
                }
            }
            else if (axis == Axis.Y_AXIS)
            {
                int width = getIconWidth();

                for (Icon icon : icons)
                {
                    int iconX = getOffset(width, icon.getIconWidth(), alignmentX);
                    icon.paintIcon(c, g, x + iconX, y);
                    y += icon.getIconHeight() + gap;
                }
            }
            else // must be Z_AXIS
            {
                int width = getIconWidth();
                int height = getIconHeight();

                for (Icon icon : icons)
                {
                    int iconX = getOffset(width, icon.getIconWidth(), alignmentX);
                    int iconY = getOffset(height, icon.getIconHeight(), alignmentY);
                    icon.paintIcon(c, g, x + iconX, y + iconY);
                }
            }
        }

        /*
         *  When the icon value is smaller than the maximum value of all icons the
         *  icon needs to be aligned appropriately. Calculate the offset to be used
         *  when painting the icon to achieve the proper alignment.
         */
        private int getOffset(int maxValue, int iconValue, float alignment)
        {
            float offset = (maxValue - iconValue) * alignment;
            return Math.round(offset);
        }
    }

    /**
     * This is the template for Classes.
     *
     *
     * @since carbon 1.0
     * @author Greg Hinkle, January 2002
     * @version $Revision: 1.4 $($Author: dvoet $ / $Date: 2003/05/05 21:21:27 $)
     * @copyright 2002 Sapient
     */

    public static class VerticalLabelUI extends BasicLabelUI {
        static {
            labelUI = new VerticalLabelUI(false);
        }

        protected boolean clockwise;


        public VerticalLabelUI(boolean clockwise) {
            super();
            this.clockwise = clockwise;
        }


        public Dimension getPreferredSize(JComponent c) {
            Dimension dim = super.getPreferredSize(c);
            return new Dimension( dim.height, dim.width );
        }

        private static Rectangle paintIconR = new Rectangle();
        private static Rectangle paintTextR = new Rectangle();
        private static Rectangle paintViewR = new Rectangle();
        private static Insets paintViewInsets = new Insets(0, 0, 0, 0);

        public void paint(Graphics g, JComponent c) {

            JLabel label = (JLabel)c;
            String text = label.getText();
            Icon icon = (label.isEnabled()) ? label.getIcon() : label.getDisabledIcon();

            if ((icon == null) && (text == null)) {
                return;
            }

            FontMetrics fm = g.getFontMetrics();
            paintViewInsets = c.getInsets(paintViewInsets);

            paintViewR.x = paintViewInsets.left;
            paintViewR.y = paintViewInsets.top;

            // Use inverted height & width
            paintViewR.height = c.getWidth() - (paintViewInsets.left + paintViewInsets.right);
            paintViewR.width = c.getHeight() - (paintViewInsets.top + paintViewInsets.bottom);

            paintIconR.x = paintIconR.y = paintIconR.width = paintIconR.height = 0;
            paintTextR.x = paintTextR.y = paintTextR.width = paintTextR.height = 0;

            String clippedText =
                    layoutCL(label, fm, text, icon, paintViewR, paintIconR, paintTextR);

            Graphics2D g2 = (Graphics2D) g;
            AffineTransform tr = g2.getTransform();
            if (clockwise) {
                g2.rotate( Math.PI / 2 );
                g2.translate( 0, - c.getWidth() );
            } else {
                g2.rotate( - Math.PI / 2 );
                g2.translate( - c.getHeight(), 0 );
            }

            if (icon != null) {
                icon.paintIcon(c, g, paintIconR.x, paintIconR.y);
            }

            if (text != null) {
                int textX = paintTextR.x;
                int textY = paintTextR.y + fm.getAscent();

                if (label.isEnabled()) {
                    paintEnabledText(label, g, clippedText, textX, textY);
                } else {
                    paintDisabledText(label, g, clippedText, textX, textY);
                }
            }

            g2.setTransform( tr );
        }
    }
}