package org.basex.gui.layout;

import static org.basex.gui.layout.BaseXKeys.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.basex.gui.*;

/**
 * Project specific Popup menu implementation.
 *
 * @author BaseX Team 2005-13, BSD License
 * @author Christian Gruen
 * @author Lukas Kircher
 */
public final class BaseXPopup extends JPopupMenu {
  /** Reference to main window. */
  private final GUI gui;
  /** Popup commands. */
  private final GUICommand[] commands;

  /**
   * Constructor.
   * @param comp component reference
   * @param cmds popup reference
   */
  public BaseXPopup(final BaseXPanel comp, final GUICommand... cmds) {
    this(comp, comp.gui, cmds);
  }

  /**
   * Constructor.
   * @param comp component reference
   * @param g gui reference
   * @param cmds popup reference
   */
  public BaseXPopup(final JComponent comp, final GUI g, final GUICommand... cmds) {
    commands = cmds;
    gui = g;

    // both listeners must be implemented to support different platforms
    comp.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(final MouseEvent e) {
        if(!gui.updating && e.isPopupTrigger())
          show(e.getComponent(), e.getX() - 10, e.getY() - 15);
      }
      @Override
      public void mouseReleased(final MouseEvent e) {
        mousePressed(e);
      }
    });
    comp.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(final KeyEvent e) {
        if(!gui.updating && CONTEXT.is(e)) {
          show(e.getComponent(), 10, 10);
        } else {
          for(final GUICommand cmd : cmds) {
            if(cmd instanceof GUIPopupCmd) {
              final BaseXKeys sc = ((GUIPopupCmd) cmd).shortcut();
              if(sc != null && sc.is(e)) {
                cmd.execute(g);
                break;
              }
            }
          }
        }
      }
    });

    final StringBuilder mnemCache = new StringBuilder();
    for(final GUICommand cmd : cmds) {
      if(cmd == null) {
        addSeparator();
      } else {
        final JMenuItem item = add(cmd.label());
        item.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(final ActionEvent e) {
            if(!gui.updating) cmd.execute(gui);
          }
        });
        BaseXLayout.setMnemonic(item, mnemCache);
        item.setAccelerator(BaseXLayout.keyStroke(cmd));
        item.setToolTipText(cmd.help());
      }
    }
  }

  @Override
  public void show(final Component comp, final int x, final int y) {
    for(int b = 0; b < commands.length; ++b) {
      if(commands[b] != null) {
        final AbstractButton button = (AbstractButton) getComponent(b);
        button.setEnabled(commands[b].enabled(gui));
        button.setSelected(commands[b].selected(gui));
      }
    }
    super.show(comp, x, y);
  }
}
