package com.mycompany.view;

import com.mycompany.projeto_ibg.Main;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;


public class Menu3 extends javax.swing.JPanel {
    
    private List<MenuListener> listeners = new ArrayList<>();
    private int selectedTab = 0; // 0 = Saúde, 1 = Dados
   
    public Menu3() {
        initComponents();
        setOpaque(false);
        updateTabSelection();
    }

    // Adicionar listener
    public void addMenuListener(MenuListener listener) {
        listeners.add(listener);
    }
    
    // Remover listener
    public void removeMenuListener(MenuListener listener) {
        listeners.remove(listener);
    }
    
    // Notificar listeners quando Saúde for selecionada
    private void notifySaudeSelected() {
        for (MenuListener listener : listeners) {
            listener.onSaudeSelected();
        }
    }
    
    // Notificar listeners quando Dados for selecionada
    private void notifyDadosSelected() {
        for (MenuListener listener : listeners) {
            listener.onDadosSelected();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        MainelMoving = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        tabSaude = new javax.swing.JLabel();
        tabDados = new javax.swing.JLabel();

        MainelMoving.setOpaque(false);

        jLabel1.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/Logo_IBG_4.png"))); // NOI18N
        jLabel1.setText("Application");

        // Configuração da aba Saúde
        tabSaude.setFont(new java.awt.Font("Liberation Sans", 1, 14)); // NOI18N
        tabSaude.setForeground(new java.awt.Color(255, 255, 255));
        tabSaude.setText("Saúde");
        tabSaude.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        tabSaude.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20));
        tabSaude.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tabSaude.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabSaudeMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                tabSaudeMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                tabSaudeMouseExited(evt);
            }
        });

        // Configuração da aba Dados
        tabDados.setFont(new java.awt.Font("Liberation Sans", 1, 14)); // NOI18N
        tabDados.setForeground(new java.awt.Color(255, 255, 255));
        tabDados.setText("Dados");
        tabDados.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        tabDados.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20));
        tabDados.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tabDados.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabDadosMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                tabDadosMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                tabDadosMouseExited(evt);
            }
        });

        javax.swing.GroupLayout MainelMovingLayout = new javax.swing.GroupLayout(MainelMoving);
        MainelMoving.setLayout(MainelMovingLayout);
        MainelMovingLayout.setHorizontalGroup(
            MainelMovingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MainelMovingLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(tabSaude, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(tabDados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        MainelMovingLayout.setVerticalGroup(
            MainelMovingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MainelMovingLayout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(20, 20, 20)
                .addComponent(tabSaude)
                .addGap(5, 5, 5)
                .addComponent(tabDados)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(MainelMoving, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(MainelMoving, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 483, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Eventos das abas
    private void tabSaudeMouseClicked(java.awt.event.MouseEvent evt) {
        if (selectedTab != 0) {
            selectedTab = 0;
            updateTabSelection();
            onTabChanged("Saúde");
            
            
        }
    }

    private void tabDadosMouseClicked(java.awt.event.MouseEvent evt) {
        if (selectedTab != 1) {
            selectedTab = 1;
            updateTabSelection();
            onTabChanged("Dados");
        }
    }

    private void tabSaudeMouseEntered(java.awt.event.MouseEvent evt) {
        if (selectedTab != 0) {
            tabSaude.setOpaque(true);
            tabSaude.setBackground(new Color(255, 255, 255, 30));
            tabSaude.repaint();
        }
    }

    private void tabSaudeMouseExited(java.awt.event.MouseEvent evt) {
        if (selectedTab != 0) {
            tabSaude.setOpaque(false);
            tabSaude.repaint();
        }
    }

    private void tabDadosMouseEntered(java.awt.event.MouseEvent evt) {
        if (selectedTab != 1) {
            tabDados.setOpaque(true);
            tabDados.setBackground(new Color(255, 255, 255, 30));
            tabDados.repaint();
        }
    }

    private void tabDadosMouseExited(java.awt.event.MouseEvent evt) {
        if (selectedTab != 1) {
            tabDados.setOpaque(false);
            tabDados.repaint();
        }
    }

    // Método para atualizar a aparência das abas
    private void updateTabSelection() {
        if (selectedTab == 0) {
            // Aba Saúde selecionada
            tabSaude.setOpaque(true);
            tabSaude.setBackground(new Color(255, 255, 255, 50));
            tabSaude.setForeground(new Color(255, 255, 255));
            
            tabDados.setOpaque(false);
            tabDados.setForeground(new Color(255, 255, 255, 180));
        } else {
            // Aba Dados selecionada
            tabDados.setOpaque(true);
            tabDados.setBackground(new Color(255, 255, 255, 50));
            tabDados.setForeground(new Color(255, 255, 255));
            
            tabSaude.setOpaque(false);
            tabSaude.setForeground(new Color(255, 255, 255, 180));
        }
        repaint();
    }

    // Método que pode ser sobrescrito para lidar com mudanças de aba
    protected void onTabChanged(String tabName) {
        System.out.println("Aba selecionada: " + tabName);
        
        if (tabName.equals("Saúde")){
            //mainFrame.showPainelSaude();
            notifySaudeSelected();
        }else if(tabName.equals("Dados")){ 
            //mainFrame.showPainelDados();
            notifyDadosSelected();
        }
        
    }

    // Métodos públicos para controle das abas
    public void setSelectedTab(int tabIndex) {
        if (tabIndex >= 0 && tabIndex <= 1 && tabIndex != selectedTab) {
            selectedTab = tabIndex;
            updateTabSelection();
            onTabChanged(tabIndex == 0 ? "Saúde" : "Dados");
        }
    }

    public int getSelectedTab() {
        return selectedTab;
    }

    public String getSelectedTabName() {
        return selectedTab == 0 ? "Saúde" : "Dados";
    }

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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel tabSaude;
    private javax.swing.JLabel tabDados;
    // End of variables declaration//GEN-END:variables

}