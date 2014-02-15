package org.basex.gui.text;

/**
 * Listener interface for handling link events.
 *
 * @author BaseX Team 2005-14, BSD License
 * @author Christian Gruen
 */
public interface LinkListener {
  /**
   * Invoked when a link is pressed.
   * @param link pressed link
   */
  void linkClicked(final String link);
}