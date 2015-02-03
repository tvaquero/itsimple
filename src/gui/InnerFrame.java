/*** 
* itSIMPLE: Integrated Tool Software Interface for Modeling PLanning Environments
* 
* Copyright (C) 2007,2008 Universidade de Sao Paulo
* 

* This file is part of itSIMPLE.
*
* itSIMPLE is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version. Other licenses might be available
* upon written agreement.
* 
* itSIMPLE is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with itSIMPLE.  If not, see <http://www.gnu.org/licenses/>.
* 
* Authors:	Tiago S. Vaquero, 
*			Victor Romero.
**/

package src.gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.RootPaneContainer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MouseInputAdapter;

public class InnerFrame extends JPanel implements RootPaneContainer
{
  /**
	 * 
	 */
	private static final long serialVersionUID = -2055541883521748081L;
private static ImageIcon ICONIZE_BUTTON_ICON = new ImageIcon("resources/images/state.png");
  private static ImageIcon RESTORE_BUTTON_ICON = new ImageIcon("resources/images/eRight.png");
  private static ImageIcon CLOSE_BUTTON_ICON = new ImageIcon("resources/images/close.png");
  private static ImageIcon MAXIMIZE_BUTTON_ICON = new ImageIcon("resources/images/zoomIN.png");
  private static ImageIcon MINIMIZE_BUTTON_ICON = new ImageIcon("resources/images/zoomOUT.png");
  private static ImageIcon PRESS_CLOSE_BUTTON_ICON = new ImageIcon("pressclose.gif");
  private static ImageIcon PRESS_RESTORE_BUTTON_ICON = new ImageIcon("pressrestore.gif");
  private static ImageIcon PRESS_ICONIZE_BUTTON_ICON = new ImageIcon("pressiconize.gif");
  private static ImageIcon PRESS_MAXIMIZE_BUTTON_ICON = new ImageIcon("pressmaximize.gif");
  private static ImageIcon PRESS_MINIMIZE_BUTTON_ICON = new ImageIcon("pressminimize.gif");
  private static ImageIcon DEFAULT_FRAME_ICON = new ImageIcon("default.gif");
  private static int BORDER_THICKNESS = 4;
  private static int WIDTH = 200;
  private static int HEIGHT = 200;
  private static int TITLE_BAR_HEIGHT = 25;
  private static int FRAME_ICON_PADDING = 2;
  private static int ICONIZED_WIDTH = 150;
  private static Color TITLE_BAR_BG_COLOR = new Color(108,190,116);
  private static Color BORDER_COLOR = new Color(8,90,16);

  private int m_titleBarHeight = TITLE_BAR_HEIGHT;
  private int m_width = WIDTH;
  private int m_height = HEIGHT;
  //private int m_iconizedWidth = ICONIZED_WIDTH;
  private int m_x;
  private int m_y;

  private String m_title;
  private JLabel m_titleLabel;
  private JLabel m_iconLabel;

  private boolean m_iconified;
  private boolean m_maximized;

  private boolean m_iconizeable;
  private boolean m_resizeable;
  private boolean m_closeable;
  private boolean m_maximizeable;
 
  // only false when maximized
  private boolean m_draggable = true;

  private JRootPane m_rootPane;

  // used to wrap m_titlePanel and m_rootPane
  private JPanel m_frameContentPanel;

  private JPanel m_titlePanel; 
  private JPanel m_contentPanel;
  private JPanel m_buttonPanel; 
  private JPanel m_buttonWrapperPanel;

  private InnerFrameButton m_iconize;
  private InnerFrameButton m_close;
  private InnerFrameButton m_maximize;

  private ImageIcon m_frameIcon = DEFAULT_FRAME_ICON;

  private NorthResizeEdge m_northResizer;
  private SouthResizeEdge m_southResizer;
  private EastResizeEdge m_eastResizer;
  private WestResizeEdge m_westResizer;

  public InnerFrame() {
    this("");
  }

  public InnerFrame(String title) {
    this(title, null);
  }

  public InnerFrame(String title, ImageIcon frameIcon) {
    this(title, frameIcon, true, true, true, true);
  }

  public InnerFrame(String title, ImageIcon frameIcon,
   boolean resizeable, boolean iconizeable, 
   boolean maximizeable, boolean closeable) {
    super.setLayout(new BorderLayout());
    attachNorthResizeEdge();
    attachSouthResizeEdge();
    attachEastResizeEdge();
    attachWestResizeEdge();
    populateInnerFrame();

    setTitle(title);
    setResizeable(resizeable);
    setIconizeable(iconizeable);
    setCloseable(closeable);
    setMaximizeable(maximizeable);
    if (frameIcon != null)
      setFrameIcon(frameIcon);
  }

  protected void populateInnerFrame() {
    m_rootPane = new JRootPane();
    m_frameContentPanel = new JPanel();
    m_frameContentPanel.setLayout(new BorderLayout());
    createTitleBar();
    m_contentPanel = new JPanel(new BorderLayout());
    m_rootPane.setContentPane(m_contentPanel);
    m_frameContentPanel.add(m_titlePanel, BorderLayout.NORTH);
    m_frameContentPanel.add(m_rootPane, BorderLayout.CENTER);
    setupCapturePanel();
    super.add(m_frameContentPanel, BorderLayout.CENTER);
  }

  protected void setupCapturePanel() {
    CapturePanel mouseTrap = new CapturePanel();
    m_rootPane.getLayeredPane().add(mouseTrap, 
      new Integer(Integer.MIN_VALUE));
    mouseTrap.setBounds(0,0,10000,10000);
  } 

  // don't allow this in root pane containers 
  public Component add(Component c) {
    return null;
  }

  // don't allow this in root pane containers 
  public void setLayout(LayoutManager mgr) {
  }

  public JMenuBar getJMenuBar() {
    return m_rootPane.getJMenuBar();
  }

  public JRootPane getRootPane() {
    return m_rootPane;
  }

  public Container getContentPane() {
    return m_rootPane.getContentPane();
  }

  public Component getGlassPane() {
    return m_rootPane.getGlassPane();
  }

  public JLayeredPane getLayeredPane() {
    return m_rootPane.getLayeredPane();
  }

  public void setJMenuBar(JMenuBar menu) {
    m_rootPane.setJMenuBar(menu);
  }

  public void setContentPane(Container content) {
    m_rootPane.setContentPane(content);
  }

  public void setGlassPane(Component glass) {
    m_rootPane.setGlassPane(glass);
  }

  public void setLayeredPane(JLayeredPane layered) {
    m_rootPane.setLayeredPane(layered);
  }

  public void toFront() {
    if (getParent() instanceof JLayeredPane)
      ((JLayeredPane) getParent()).moveToFront(this);
  }

  public void close() {
    if (getParent() instanceof JLayeredPane) {
      JLayeredPane jlp = (JLayeredPane) getParent();
      jlp.remove(InnerFrame.this);
      jlp.repaint();
    }
  }

  public boolean isIconizeable() {
    return m_iconizeable;
  }

  public void setIconizeable(boolean b) {
    m_iconizeable = b;
    m_iconize.setVisible(b);
    m_titlePanel.revalidate();
  }

  public boolean isCloseable() {
    return m_closeable;
  }

  public void setCloseable(boolean b) {
    m_closeable = b;
    m_close.setVisible(b);
    m_titlePanel.revalidate();
  }

  public boolean isMaximizeable() {
    return m_maximizeable;
  }

  public void setMaximizeable(boolean b) {
    m_maximizeable = b;
    m_maximize.setVisible(b);
    m_titlePanel.revalidate();
  }

  public boolean isIconified() {
    return m_iconified;
  }

  public void setIconified(boolean b) {
    m_iconified = b;
    if (b) {
      if (isMaximized())
        setMaximized(false);
      toFront();
      m_width = getWidth();     // remember width
      m_height = getHeight();   // remember height
      setBounds(getX(), getY(), ICONIZED_WIDTH, 
        m_titleBarHeight + 2*BORDER_THICKNESS);
      m_iconize.setIcon(RESTORE_BUTTON_ICON);
      m_iconize.setPressedIcon(PRESS_RESTORE_BUTTON_ICON);
      setResizeable(false);
    }
    else {
      toFront();
      setBounds(getX(), getY(), m_width, m_height);
      m_iconize.setIcon(ICONIZE_BUTTON_ICON);
      m_iconize.setPressedIcon(PRESS_ICONIZE_BUTTON_ICON);
      setResizeable(true);
    }
    revalidate();
  }

  public boolean isMaximized() {
    return m_maximized;
  }

  public void setMaximized(boolean b) {
    m_maximized = b;
    if (b)
    {
      if (isIconified())
        setIconified(false);
      toFront();
      m_width = getWidth();     // remember width
      m_height = getHeight();   // remember height
      m_x = getX();             // remember x
      m_y = getY();             // remember y
      setBounds(0, 0, getParent().getWidth(), getParent().getHeight());
      m_maximize.setIcon(MINIMIZE_BUTTON_ICON);
      m_maximize.setPressedIcon(PRESS_MINIMIZE_BUTTON_ICON);
      setResizeable(false);
      setDraggable(false);
    }
    else {
      toFront();
      setBounds(m_x, m_y, m_width, m_height);
      m_maximize.setIcon(MAXIMIZE_BUTTON_ICON);
      m_maximize.setPressedIcon(PRESS_MAXIMIZE_BUTTON_ICON);
      setResizeable(true);
      setDraggable(true);
    }
    revalidate();
  } 

  ////////////////////////////////////////////
  //////////////// Title Bar /////////////////
  ////////////////////////////////////////////

  public void setFrameIcon(ImageIcon fi) {
    m_frameIcon = fi;

    if (fi != null) {
      if (m_frameIcon.getIconHeight() > TITLE_BAR_HEIGHT)
        setTitleBarHeight(m_frameIcon.getIconHeight() + 2*FRAME_ICON_PADDING);
      m_iconLabel.setIcon(m_frameIcon);
    }
    else setTitleBarHeight(TITLE_BAR_HEIGHT);
    revalidate();
  }
 
  public ImageIcon getFrameIcon() {
    return m_frameIcon;
  }

  public void setTitle(String s) {
    m_title = s;
    m_titleLabel.setText(s);
    m_titlePanel.repaint();
  }

  public String getTitle() {
    return m_title;
  }

  public void setTitleBarHeight(int h) {
    m_titleBarHeight = h;
  }

  public int getTitleBarHeight() {
    return m_titleBarHeight;
  }

  public boolean isDraggable() {
    return m_draggable;
  }
  
  private void setDraggable(boolean b) {
    m_draggable = b;
  }
 
  // create the title bar: m_titlePanel
  protected void createTitleBar() {
    m_titlePanel = new JPanel() {
      /**
		 * 
		 */
		private static final long serialVersionUID = -948066492361091787L;

	public Dimension getPreferredSize() {
        return new Dimension(InnerFrame.this.getWidth(), 
          m_titleBarHeight);
      }
    };
    m_titlePanel.setLayout(new BorderLayout());
    m_titlePanel.setOpaque(true);
    m_titlePanel.setBackground(TITLE_BAR_BG_COLOR);

    m_titleLabel = new JLabel();
    m_titleLabel.setForeground(Color.black);

    m_close = new InnerFrameButton(CLOSE_BUTTON_ICON);
    m_close.setPressedIcon(PRESS_CLOSE_BUTTON_ICON);
    m_close.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        InnerFrame.this.close();
      }
    });

    m_maximize = new InnerFrameButton(MAXIMIZE_BUTTON_ICON);
    m_maximize.setPressedIcon(PRESS_MAXIMIZE_BUTTON_ICON);
    m_maximize.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        InnerFrame.this.setMaximized(!InnerFrame.this.isMaximized());
      }
    });

    m_iconize = new InnerFrameButton(ICONIZE_BUTTON_ICON);
    m_iconize.setPressedIcon(PRESS_ICONIZE_BUTTON_ICON);
    m_iconize.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        InnerFrame.this.setIconified(!InnerFrame.this.isIconified());
      }
    });

    m_buttonWrapperPanel = new JPanel();
    m_buttonWrapperPanel.setOpaque(false);
    m_buttonPanel = new JPanel(new GridLayout(1,3));
    m_buttonPanel.setOpaque(false);
    m_buttonPanel.add(m_iconize);
    m_buttonPanel.add(m_maximize);
    m_buttonPanel.add(m_close);
    m_buttonPanel.setAlignmentX(0.5f);
    m_buttonPanel.setAlignmentY(0.5f);
    m_buttonWrapperPanel.add(m_buttonPanel);

    m_iconLabel = new JLabel();
    m_iconLabel.setBorder(new EmptyBorder(
      FRAME_ICON_PADDING, FRAME_ICON_PADDING,
      FRAME_ICON_PADDING, FRAME_ICON_PADDING));
    if (m_frameIcon != null)
      m_iconLabel.setIcon(m_frameIcon);

    m_titlePanel.add(m_titleLabel, BorderLayout.CENTER);
    m_titlePanel.add(m_buttonWrapperPanel, BorderLayout.EAST);
    m_titlePanel.add(m_iconLabel, BorderLayout.WEST);

    InnerFrameTitleBarMouseAdapter iftbma = 
      new InnerFrameTitleBarMouseAdapter(this);
    m_titlePanel.addMouseListener(iftbma);
    m_titlePanel.addMouseMotionListener(iftbma);
  }

  // title bar mouse adapter for frame dragging
  class InnerFrameTitleBarMouseAdapter 
  extends MouseInputAdapter
  {
    InnerFrame m_if;
    int m_XDifference, m_YDifference;
    boolean m_dragging;

    public InnerFrameTitleBarMouseAdapter(InnerFrame inf) {
      m_if = inf;
    }

    // don't allow dragging outside of parent
    public void mouseDragged(MouseEvent e) {
      int ex = e.getX();
      int ey = e.getY();
      int x = m_if.getX();
      int y = m_if.getY();
      int w = m_if.getParent().getWidth();
      int h = m_if.getParent().getHeight();
      if (m_dragging & m_if.isDraggable()) {
        if ((ey + y > 0 && ey + y < h) && (ex + x > 0 && ex + x < w))
          m_if.setLocation(ex-m_XDifference + x, ey-m_YDifference + y);
        else if (!(ey + y > 0 && ey + y < h) && (ex + x > 0 && ex + x < w)) {
          if (!(ey + y > 0) && ey + y < h)
            m_if.setLocation(ex-m_XDifference + x, 0-m_YDifference);
          else if (ey + y > 0 && !(ey + y < h))
            m_if.setLocation(ex-m_XDifference + x, h-m_YDifference);
        }
        else if ((ey + y > 0 && ey + y < h) && !(ex + x > 0 && ex + x < w)) {
          if (!(ex + x > 0) && ex + x < w)
            m_if.setLocation(0-m_XDifference, ey-m_YDifference + y);
          else if (ex + x > 0 && !(ex + x < w)) 
            m_if.setLocation(w-m_XDifference, ey-m_YDifference + y);
        }
        else if (!(ey + y > 0) && ey + y < h && !(ex + x > 0) && ex + x < w) 
          m_if.setLocation(0-m_XDifference, 0-m_YDifference);
        else if (!(ey + y > 0) && ey + y < h && ex + x > 0 && !(ex + x < w)) 
          m_if.setLocation(w-m_XDifference, 0-m_YDifference);
        else if (ey + y > 0 && !(ey + y < h) && !(ex + x > 0) && ex + x < w) 
          m_if.setLocation(0-m_XDifference, h-m_YDifference);
        else if (ey + y > 0 && !(ey + y < h) && ex + x > 0 && !(ex + x < w)) 
          m_if.setLocation(w-m_XDifference, h-m_YDifference);
      }
    } 

    public void mousePressed(MouseEvent e) {  
      m_if.toFront();
      m_XDifference = e.getX();
      m_YDifference = e.getY();
      m_dragging = true;
    }

    public void mouseReleased(MouseEvent e) {
      m_dragging = false;
    }
  }
  
  // custom button class for title bar
  class InnerFrameButton extends JButton 
  {
    /**
	 * 
	 */
	private static final long serialVersionUID = 2630578175294290708L;
	Dimension m_dim;

    public InnerFrameButton(ImageIcon ii) {
      super(ii);
      m_dim = new Dimension(ii.getIconWidth(), ii.getIconHeight());
      setOpaque(false);
      setContentAreaFilled(false);
      setBorder(null);
    }

    public Dimension getPreferredSize() {
      return m_dim;
    }

    public Dimension getMinimumSize() {
      return m_dim;
    }

    public Dimension getMaximumSize() {
      return m_dim;
    }
  }

  ///////////////////////////////////////////////
  /////////// Mouse Event Capturing /////////////
  ///////////////////////////////////////////////

  class CapturePanel extends JPanel 
  {
    /**
	 * 
	 */
	private static final long serialVersionUID = 3318207365877407128L;

	public CapturePanel() {
      MouseInputAdapter mia = new MouseInputAdapter() {};
      addMouseListener(mia);
      addMouseMotionListener(mia);
    }
  }

  ///////////////////////////////////////////////
  //////////////// Resizability /////////////////
  ///////////////////////////////////////////////

  public boolean isResizeable() {
    return m_resizeable;
  }

  public void setResizeable(boolean b) {
    if (!b && m_resizeable == true) {
      m_northResizer.removeMouseListener(m_northResizer);
      m_northResizer.removeMouseMotionListener(m_northResizer);
      m_southResizer.removeMouseListener(m_southResizer);
      m_southResizer.removeMouseMotionListener(m_southResizer);
      m_eastResizer.removeMouseListener(m_eastResizer);
      m_eastResizer.removeMouseMotionListener(m_eastResizer);
      m_westResizer.removeMouseListener(m_westResizer);
      m_westResizer.removeMouseMotionListener(m_westResizer);
    }
    else if (b && m_resizeable == false) {
      m_northResizer.addMouseListener(m_northResizer);
      m_northResizer.addMouseMotionListener(m_northResizer);
      m_southResizer.addMouseListener(m_southResizer);
      m_southResizer.addMouseMotionListener(m_southResizer);
      m_eastResizer.addMouseListener(m_eastResizer);
      m_eastResizer.addMouseMotionListener(m_eastResizer);
      m_westResizer.addMouseListener(m_westResizer);
      m_westResizer.addMouseMotionListener(m_westResizer);
    }
    m_resizeable = b;
  }

  protected void attachNorthResizeEdge() {
    m_northResizer = new NorthResizeEdge(this);
    super.add(m_northResizer, BorderLayout.NORTH);
  }

  protected void attachSouthResizeEdge() {
    m_southResizer = new SouthResizeEdge(this);
    super.add(m_southResizer, BorderLayout.SOUTH);
  }

  protected void attachEastResizeEdge() {
    m_eastResizer = new EastResizeEdge(this);
    super.add(m_eastResizer, BorderLayout.EAST);
  }

  protected void attachWestResizeEdge() {
    m_westResizer = new WestResizeEdge(this);
    super.add(m_westResizer, BorderLayout.WEST);
  }

  class EastResizeEdge extends JPanel
  implements MouseListener, MouseMotionListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = -2372192134491782701L;
	private int WIDTH = BORDER_THICKNESS;
    private int MIN_WIDTH = ICONIZED_WIDTH;
    private boolean m_dragging;
    private JComponent m_resizeComponent;
  
    protected EastResizeEdge(JComponent c) {
      m_resizeComponent = c;
      setOpaque(true);
      setBackground(BORDER_COLOR);
    }

    public Dimension getPreferredSize() {
      return new Dimension(WIDTH, m_resizeComponent.getHeight());
    }

    public void mouseClicked(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {
      m_dragging = false;
    }

    public void mouseDragged(MouseEvent e) {
      if (m_resizeComponent.getWidth() + e.getX() >= MIN_WIDTH)
        m_resizeComponent.setBounds(m_resizeComponent.getX(), 
          m_resizeComponent.getY(), 
          m_resizeComponent.getWidth() + e.getX(),
          m_resizeComponent.getHeight());
      else
        m_resizeComponent.setBounds(m_resizeComponent.getX(), 
          m_resizeComponent.getY(), 
          MIN_WIDTH, m_resizeComponent.getHeight());
      m_resizeComponent.validate();
    }

    public void mouseEntered(MouseEvent e) {
      if (!m_dragging)
        setCursor(Cursor.getPredefinedCursor(
          Cursor.E_RESIZE_CURSOR));
    }
    
    public void mouseExited(MouseEvent e) {
      if (!m_dragging)
        setCursor(Cursor.getPredefinedCursor(
          Cursor.DEFAULT_CURSOR));
    }
  
    public void mousePressed(MouseEvent e) {
      toFront();
      m_dragging = true;
    }  
  }

  class WestResizeEdge extends JPanel
  implements MouseListener, MouseMotionListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = -8451514910533583780L;
	private int WIDTH = BORDER_THICKNESS;
    private int MIN_WIDTH = ICONIZED_WIDTH;
    private int m_dragX, m_rightX;
    private boolean m_dragging;
    private JComponent m_resizeComponent;
  
    protected WestResizeEdge(JComponent c) {
      m_resizeComponent = c;
      setOpaque(true);
      setBackground(BORDER_COLOR);
    }

    public Dimension getPreferredSize() {
      return new Dimension(WIDTH, m_resizeComponent.getHeight());
    }

    public void mouseClicked(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}
  
    public void mouseReleased(MouseEvent e) {
      m_dragging = false;
    }

    public void mouseDragged(MouseEvent e) {
      if (m_resizeComponent.getWidth()-
       (e.getX()-m_dragX) >= MIN_WIDTH)
        m_resizeComponent.setBounds(
          m_resizeComponent.getX() + (e.getX()-m_dragX), 
          m_resizeComponent.getY(), 
          m_resizeComponent.getWidth()-(e.getX()-m_dragX),
          m_resizeComponent.getHeight());
      else
        if (m_resizeComponent.getX() + MIN_WIDTH < m_rightX)
          m_resizeComponent.setBounds(m_rightX-MIN_WIDTH, 
            m_resizeComponent.getY(), 
            MIN_WIDTH, m_resizeComponent.getHeight());
        else
          m_resizeComponent.setBounds(m_resizeComponent.getX(), 
            m_resizeComponent.getY(), 
            MIN_WIDTH, m_resizeComponent.getHeight());
      m_resizeComponent.validate();
    }
  
    public void mouseEntered(MouseEvent e) {
      if (!m_dragging)
        setCursor(Cursor.getPredefinedCursor(
          Cursor.W_RESIZE_CURSOR));
    }
    
    public void mouseExited(MouseEvent e) {
      if (!m_dragging)
        setCursor(Cursor.getPredefinedCursor(
          Cursor.DEFAULT_CURSOR));
    }
    
    public void mousePressed(MouseEvent e) {
      toFront();
      m_rightX = m_resizeComponent.getX() + 
        m_resizeComponent.getWidth();
      m_dragging = true;
      m_dragX = e.getX();
    }  
  }
  
  class NorthResizeEdge extends JPanel
  implements MouseListener, MouseMotionListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = -2179877452401194523L;
	private static final int NORTH = 0;
    private static final int NORTHEAST = 1;
    private static final int NORTHWEST = 2;
    private int CORNER = 10;
    private int HEIGHT = BORDER_THICKNESS;
    private int MIN_WIDTH = ICONIZED_WIDTH;
    private int MIN_HEIGHT = TITLE_BAR_HEIGHT+(2*HEIGHT);
    private int /*m_width, */m_dragX, m_dragY, m_rightX, m_lowerY;
    private boolean m_dragging;
    private JComponent m_resizeComponent;
    private int m_mode;
    
    protected NorthResizeEdge(JComponent c) {
      m_resizeComponent = c;
      setOpaque(true);
      setBackground(BORDER_COLOR);
    }

    public Dimension getPreferredSize() {
      return new Dimension(m_resizeComponent.getWidth(), HEIGHT);
    }

    public void mouseClicked(MouseEvent e) {}

    public void mouseMoved(MouseEvent e) {
      if (!m_dragging) {
        if (e.getX() < CORNER) {
          setCursor(Cursor.getPredefinedCursor(
            Cursor.NW_RESIZE_CURSOR));
        }
        else if(e.getX() > getWidth()-CORNER) {
          setCursor(Cursor.getPredefinedCursor(
            Cursor.NE_RESIZE_CURSOR));
        }
        else {
          setCursor(Cursor.getPredefinedCursor(
            Cursor.N_RESIZE_CURSOR));
        }
      }
    }

    public void mouseReleased(MouseEvent e) {
      m_dragging = false;
    }

    public void mouseDragged(MouseEvent e) {
      int h = m_resizeComponent.getHeight();
      int w = m_resizeComponent.getWidth();
      int x = m_resizeComponent.getX();
      int y = m_resizeComponent.getY();
      int ex = e.getX();
      int ey = e.getY();
      switch (m_mode) {
        case NORTH:
          if (h-(ey-m_dragY) >= MIN_HEIGHT)
            m_resizeComponent.setBounds(x, y + (ey-m_dragY), 
              w, h-(ey-m_dragY));
          else
              m_resizeComponent.setBounds(x, 
                m_lowerY-MIN_HEIGHT, w, MIN_HEIGHT);
          break;
        case NORTHEAST:
          if (h-(ey-m_dragY) >= MIN_HEIGHT
          && w + (ex-(getWidth()-CORNER)) >= MIN_WIDTH)
            m_resizeComponent.setBounds(x, 
              y + (ey-m_dragY), w + (ex-(getWidth()-CORNER)),
                h-(ey-m_dragY));
          else if (h-(ey-m_dragY) >= MIN_HEIGHT
          && !(w + (ex-(getWidth()-CORNER)) >= MIN_WIDTH))
            m_resizeComponent.setBounds(x, 
              y + (ey-m_dragY), MIN_WIDTH, h-(ey-m_dragY));
          else if (!(h-(ey-m_dragY) >= MIN_HEIGHT)
          && w + (ex-(getWidth()-CORNER)) >= MIN_WIDTH)
            m_resizeComponent.setBounds(x, 
              m_lowerY-MIN_HEIGHT, w + (ex-(getWidth()-CORNER)), 
                MIN_HEIGHT);
          else
            m_resizeComponent.setBounds(x, 
              m_lowerY-MIN_HEIGHT, MIN_WIDTH, MIN_HEIGHT);
          break;
        case NORTHWEST:
          if (h-(ey-m_dragY) >= MIN_HEIGHT
          && w-(ex-m_dragX) >= MIN_WIDTH)
            m_resizeComponent.setBounds(x + (ex-m_dragX), 
              y + (ey-m_dragY), w-(ex-m_dragX),
                h-(ey-m_dragY));
          else if (h-(ey-m_dragY) >= MIN_HEIGHT
          && !(w-(ex-m_dragX) >= MIN_WIDTH)) {
            if (x + MIN_WIDTH < m_rightX) 
              m_resizeComponent.setBounds(m_rightX-MIN_WIDTH, 
                y + (ey-m_dragY), MIN_WIDTH, h-(ey-m_dragY));
            else
              m_resizeComponent.setBounds(x, 
                y + (ey-m_dragY), w, h-(ey-m_dragY));
          } 
          else if (!(h-(ey-m_dragY) >= MIN_HEIGHT)
          && w-(ex-m_dragX) >= MIN_WIDTH) 
            m_resizeComponent.setBounds(x + (ex-m_dragX), 
              m_lowerY-MIN_HEIGHT, w-(ex-m_dragX), MIN_HEIGHT);
          else
            m_resizeComponent.setBounds(m_rightX-MIN_WIDTH, 
              m_lowerY-MIN_HEIGHT, MIN_WIDTH, MIN_HEIGHT);
          break;
      }
      m_rightX = x + w;
      m_resizeComponent.validate();
    }
  
    public void mouseEntered(MouseEvent e) {
      mouseMoved(e);
    }
    
    public void mouseExited(MouseEvent e) {
      if (!m_dragging)
        setCursor(Cursor.getPredefinedCursor(
          Cursor.DEFAULT_CURSOR));
    }
    
    public void mousePressed(MouseEvent e) {
      toFront();
      m_dragging = true;
      m_dragX = e.getX();
      m_dragY = e.getY();
      m_lowerY = m_resizeComponent.getY()
        + m_resizeComponent.getHeight();
      if (e.getX() < CORNER) {
        m_mode = NORTHWEST;
      }
      else if(e.getX() > getWidth()-CORNER) {
        m_mode = NORTHEAST;
      }
      else {
        m_mode = NORTH;    
      }
    }  
  }
  
  class SouthResizeEdge extends JPanel
  implements MouseListener, MouseMotionListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = -687949867012430204L;
	private static final int SOUTH = 0;
    private static final int SOUTHEAST = 1;
    private static final int SOUTHWEST = 2;
    private int CORNER = 10;
    private int HEIGHT = BORDER_THICKNESS;
    private int MIN_WIDTH = ICONIZED_WIDTH;
    private int MIN_HEIGHT = TITLE_BAR_HEIGHT+(2*HEIGHT);
    private int /*m_width, */m_dragX, m_dragY, m_rightX;
    private boolean m_dragging;
    private JComponent m_resizeComponent;
    private int m_mode;
    
    protected SouthResizeEdge(JComponent c) {
      m_resizeComponent = c;
      setOpaque(true);
      setBackground(BORDER_COLOR);
    }

    public Dimension getPreferredSize() {
      return new Dimension(m_resizeComponent.getWidth(), HEIGHT);
    }
  
    public void mouseClicked(MouseEvent e) {}
  
    public void mouseMoved(MouseEvent e) {
      if (!m_dragging) {
        if (e.getX() < CORNER) {
          setCursor(Cursor.getPredefinedCursor(
            Cursor.SW_RESIZE_CURSOR));
        }
        else if(e.getX() > getWidth()-CORNER) {
          setCursor(Cursor.getPredefinedCursor(
            Cursor.SE_RESIZE_CURSOR));
        }
        else {
          setCursor(Cursor.getPredefinedCursor(
            Cursor.S_RESIZE_CURSOR));
        }
      }
    }
  
    public void mouseReleased(MouseEvent e) {
      m_dragging = false;
    }
  
    public void mouseDragged(MouseEvent e) {
      int h = m_resizeComponent.getHeight();
      int w = m_resizeComponent.getWidth();
      int x = m_resizeComponent.getX();
      int y = m_resizeComponent.getY();
      int ex = e.getX();
      int ey = e.getY();
      switch (m_mode) {
        case SOUTH:
          if (h+(ey-m_dragY) >= MIN_HEIGHT)
           m_resizeComponent.setBounds(x, y, w, h+(ey-m_dragY));
          else
            m_resizeComponent.setBounds(x, y, w, MIN_HEIGHT);
          break;
        case SOUTHEAST:
          if (h+(ey-m_dragY) >= MIN_HEIGHT
            && w + (ex-(getWidth()-CORNER)) >= MIN_WIDTH)
            m_resizeComponent.setBounds(x, y, 
              w + (ex-(getWidth()-CORNER)), h+(ey-m_dragY));
          else if (h+(ey-m_dragY) >= MIN_HEIGHT
            && !(w + (ex-(getWidth()-CORNER)) >= MIN_WIDTH))
            m_resizeComponent.setBounds(x, y, 
              MIN_WIDTH, h+(ey-m_dragY));
          else if (!(h+(ey-m_dragY) >= MIN_HEIGHT)
            && w + (ex-(getWidth()-CORNER)) >= MIN_WIDTH)
            m_resizeComponent.setBounds(x, y, 
              w + (ex-(getWidth()-CORNER)), MIN_HEIGHT);
          else
            m_resizeComponent.setBounds(x, 
              y, MIN_WIDTH, MIN_HEIGHT);
          break;
        case SOUTHWEST:
          if (h+(ey-m_dragY) >= MIN_HEIGHT 
            && w-(ex-m_dragX) >= MIN_WIDTH)
            m_resizeComponent.setBounds(x + (ex-m_dragX), y, 
              w-(ex-m_dragX), h+(ey-m_dragY));
          else if (h+(ey-m_dragY) >= MIN_HEIGHT
            && !(w-(ex-m_dragX) >= MIN_WIDTH)) {
            if (x + MIN_WIDTH < m_rightX)
              m_resizeComponent.setBounds(m_rightX-MIN_WIDTH, y, 
                MIN_WIDTH, h+(ey-m_dragY));
            else
              m_resizeComponent.setBounds(x, y, w, 
                h+(ey-m_dragY));
          }
          else if (!(h+(ey-m_dragY) >= MIN_HEIGHT)
            && w-(ex-m_dragX) >= MIN_WIDTH)
            m_resizeComponent.setBounds(x + (ex-m_dragX), y, 
              w-(ex-m_dragX), MIN_HEIGHT);
          else
            m_resizeComponent.setBounds(m_rightX-MIN_WIDTH, 
              y, MIN_WIDTH, MIN_HEIGHT);
          break;
      }
      m_rightX = x + w;
      m_resizeComponent.validate();
    }
  
    public void mouseEntered(MouseEvent e) {
      mouseMoved(e);
    }
    
    public void mouseExited(MouseEvent e) {
      if (!m_dragging)
        setCursor(Cursor.getPredefinedCursor(
          Cursor.DEFAULT_CURSOR));
    }
    
    public void mousePressed(MouseEvent e) {
      toFront();
      m_dragging = true;
      m_dragX = e.getX();
      m_dragY = e.getY();
      if (e.getX() < CORNER) {
        m_mode = SOUTHWEST;
      }
      else if(e.getX() > getWidth()-CORNER) {
        m_mode = SOUTHEAST;
      }
      else {
        m_mode = SOUTH;    
      }
    }  
  }
}