package com.mycompany.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.plaf.basic.BasicScrollBarUI;


public class ModernScrollBarUI extends BasicScrollBarUI {
    
    // Cores para a barra de rolagem personalizada
    private static final Color SCROLLBAR_TRACK_COLOR = new Color(248, 250, 252);
    private static final Color SCROLLBAR_THUMB_COLOR = new Color(203, 213, 225);
    private static final Color SCROLLBAR_THUMB_HOVER_COLOR = new Color(148, 163, 184);
    private static final Color SCROLLBAR_THUMB_PRESSED_COLOR = new Color(100, 116, 139);
    
    @Override
        protected void configureScrollBarColors() {
            this.thumbColor = SCROLLBAR_THUMB_COLOR;
            this.thumbHighlightColor = SCROLLBAR_THUMB_HOVER_COLOR;
            this.thumbDarkShadowColor = SCROLLBAR_THUMB_PRESSED_COLOR;
            this.trackColor = SCROLLBAR_TRACK_COLOR;
            this.trackHighlightColor = SCROLLBAR_TRACK_COLOR;
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }

        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2.setColor(SCROLLBAR_TRACK_COLOR);
            g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
            
            g2.dispose();
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color color = SCROLLBAR_THUMB_COLOR;
            JScrollBar scrollBar = (JScrollBar) c;
            
            if (isDragging) {
                color = SCROLLBAR_THUMB_PRESSED_COLOR;
            } else if (isThumbRollover()) {
                color = SCROLLBAR_THUMB_HOVER_COLOR;
            }

            g2.setColor(color);
            
            // Desenhar o thumb com bordas arredondadas
            int arc = Math.min(thumbBounds.width, thumbBounds.height);
            if (scrollBar.getOrientation() == JScrollBar.VERTICAL) {
                int x = thumbBounds.x + 2;
                int y = thumbBounds.y + 2;
                int width = thumbBounds.width - 4;
                int height = thumbBounds.height - 4;
                g2.fillRoundRect(x, y, width, height, arc - 2, arc - 2);
            } else {
                int x = thumbBounds.x + 2;
                int y = thumbBounds.y + 2;
                int width = thumbBounds.width - 4;
                int height = thumbBounds.height - 4;
                g2.fillRoundRect(x, y, width, height, arc - 2, arc - 2);
            }

            g2.dispose();
        }

        @Override
        protected void setThumbBounds(int x, int y, int width, int height) {
            super.setThumbBounds(x, y, width, height);
            scrollbar.repaint();
        }
    
}
