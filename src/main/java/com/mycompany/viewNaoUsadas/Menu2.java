package com.mycompany.viewNaoUsadas;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JFrame;


public class Menu2 extends javax.swing.JPanel {

    private int selectedTab = 0; // 0 = Saúde, 1 = Dados
   
    public Menu2() {
        initComponents();
        setOpaque(false);
        setupTabListeners();
    }

    private void setupTabListeners() {
        // Listener para a aba Saúde
        saudeTab.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectTab(0);
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                if (selectedTab != 0) {
                    saudeTab.setBackground(new Color(255, 255, 255, 30));
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (selectedTab != 0) {
                    saudeTab.setBackground(new Color(255, 255, 255, 10));
                }
            }
        });
        
        // Listener para a aba Dados
        dadosTab.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectTab(1);
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                if (selectedTab != 1) {
                    dadosTab.setBackground(new Color(255, 255, 255, 30));
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (selectedTab != 1) {
                    dadosTab.setBackground(new Color(255, 255, 255, 10));
                }
            }
        });
        
        // Selecionar a primeira aba por padrão
        selectTab(0);
    }
    
    private void selectTab(int tabIndex) {
        selectedTab = tabIndex;
        
        // Reset das cores
        saudeTab.setBackground(new Color(255, 255, 255, 10));
        dadosTab.setBackground(new Color(255, 255, 255, 10));
        
        // Destacar aba selecionada
        if (tabIndex == 0) {
            saudeTab.setBackground(new Color(255, 255, 255, 50));
        } else {
            dadosTab.setBackground(new Color(255, 255, 255, 50));
        }
        
        repaint();
    }
    
    public int getSelectedTab() {
        return selectedTab;
    }
    
    public String getSelectedTabName() {
        return selectedTab == 0 ? "Saúde" : "Dados";
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        MainelMoving = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        tabsPanel = new javax.swing.JPanel();
        saudeTab = new javax.swing.JPanel();
        saudeLabel = new javax.swing.JLabel();
        dadosTab = new javax.swing.JPanel();
        dadosLabel = new javax.swing.JLabel();

        MainelMoving.setOpaque(false);

        jLabel1.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/logo.png"))); // NOI18N
        jLabel1.setText("Application");

        javax.swing.GroupLayout MainelMovingLayout = new javax.swing.GroupLayout(MainelMoving);
        MainelMoving.setLayout(MainelMovingLayout);
        MainelMovingLayout.setHorizontalGroup(
            MainelMovingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MainelMovingLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE)
                .addContainerGap())
        );
        MainelMovingLayout.setVerticalGroup(
            MainelMovingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, MainelMovingLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel1))
        );

        tabsPanel.setOpaque(false);

        saudeTab.setBackground(new java.awt.Color(255, 255, 255, 10));
        saudeTab.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 15, 10, 15));
        saudeTab.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        saudeLabel.setFont(new java.awt.Font("Liberation Sans", 1, 14)); // NOI18N
        saudeLabel.setForeground(new java.awt.Color(255, 255, 255));
        saudeLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        saudeLabel.setText("Saúde");

        javax.swing.GroupLayout saudeTabLayout = new javax.swing.GroupLayout(saudeTab);
        saudeTab.setLayout(saudeTabLayout);
        saudeTabLayout.setHorizontalGroup(
            saudeTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(saudeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        saudeTabLayout.setVerticalGroup(
            saudeTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(saudeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        dadosTab.setBackground(new java.awt.Color(255, 255, 255, 10));
        dadosTab.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 15, 10, 15));
        dadosTab.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        dadosLabel.setFont(new java.awt.Font("Liberation Sans", 1, 14)); // NOI18N
        dadosLabel.setForeground(new java.awt.Color(255, 255, 255));
        dadosLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        dadosLabel.setText("Dados");

        javax.swing.GroupLayout dadosTabLayout = new javax.swing.GroupLayout(dadosTab);
        dadosTab.setLayout(dadosTabLayout);
        dadosTabLayout.setHorizontalGroup(
            dadosTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dadosLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        dadosTabLayout.setVerticalGroup(
            dadosTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dadosLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout tabsPanelLayout = new javax.swing.GroupLayout(tabsPanel);
        tabsPanel.setLayout(tabsPanelLayout);
        tabsPanelLayout.setHorizontalGroup(
            tabsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(saudeTab, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(dadosTab, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        tabsPanelLayout.setVerticalGroup(
            tabsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabsPanelLayout.createSequentialGroup()
                .addComponent(saudeTab, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dadosTab, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(MainelMoving, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(tabsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(MainelMoving, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(tabsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 395, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    @Override
    protected void paintChildren(Graphics grphcs) {
        Graphics2D g2=(Graphics2D)grphcs;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint g =new GradientPaint(0, 0, Color.decode("#1CB5E0"), 0, getHeight(),Color.decode("#000046"));
        g2.setPaint(g);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 0, 0);
        g2.fillRect(getWidth()-20, 0, getWidth(), getHeight());
        super.paintChildren(grphcs); 
    }

     private int x;
    private int y;

    public void initMoving(JFrame fram) {
        MainelMoving.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                x = me.getX();
                y = me.getY();
            }

        });
        MainelMoving.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent me) {
                fram.setLocation(me.getXOnScreen() - x, me.getYOnScreen() - y);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel MainelMoving;
    private javax.swing.JPanel dadosTab;
    private javax.swing.JLabel dadosLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel saudeTab;
    private javax.swing.JLabel saudeLabel;
    private javax.swing.JPanel tabsPanel;
    // End of variables declaration//GEN-END:variables
}