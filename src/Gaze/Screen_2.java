/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gaze;


    /**
 *
 * @author C. Levallois
 */
public class Screen_2 extends javax.swing.JFrame {

    /**
     * Creates new form Screen_2
     */
    boolean toggleDirectedNetwork;
    boolean toggleWeightedNetwork;


    public Screen_2() {
        initComponents();
        directedNetwork.setSelected(true);
        toggleDirectedNetwork = directedNetwork.isSelected();

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        directedNetwork = new javax.swing.JToggleButton();
        jButton1 = new javax.swing.JButton();
        cosineMin = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        maxTargets4Calc = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        minOccAsTarget = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        weightNetwork = new javax.swing.JToggleButton();
        minOccAsSource = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        directedNetwork.setSelected(true);
        directedNetwork.setText("directed network");
        directedNetwork.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                directedNetworkActionPerformed(evt);
            }
        });

        jButton1.setText("SAVE");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        cosineMin.setText("0.01");
        cosineMin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cosineMinActionPerformed(evt);
            }
        });

        jLabel1.setText("<html><b>cosine min</b><br>from 0 to 1, choose a higher number to select only strong similarities</html>");

        maxTargets4Calc.setText("30");
        maxTargets4Calc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maxTargets4CalcActionPerformed(evt);
            }
        });

        jLabel2.setText("<html><b>max number of targets used in the similarity computation</b><br>choose a lower nb to speed up things - but decreases precision</html>");

        minOccAsTarget.setText("0");
        minOccAsTarget.setMinimumSize(new java.awt.Dimension(15, 22));
        minOccAsTarget.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                minOccAsTargetActionPerformed(evt);
            }
        });

        jLabel3.setText("<html><b>min occurrence as a target in the whole network</b><br>a node occurring less than this number of times as a <b>target</b> will not be included in the final network. Choose a higher value to delete uninteresting nodes.<br> (used only in directed networks)</html>");

        jTextField3.setText(",");
        jTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField3ActionPerformed(evt);
            }
        });

        jLabel4.setText("<html><b>field separator</b><br>edges list should be like:<br>source[field sep]target[field sep]weight<br>(weight is optional)</html>");

        weightNetwork.setText("unweighted network");
        weightNetwork.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                weightNetworkActionPerformed(evt);
            }
        });

        minOccAsSource.setText("0");

        jLabel5.setText("<html><b>min occurrence as a source in the whole network</b><br>a node occurring less than this number of times as a <b>source</b> will not be included in the final network. Choose a higher value to delete uninteresting nodes.<br> (used only in directed networks)</html>");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 540, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(directedNetwork)
                                .addGap(35, 35, 35)
                                .addComponent(weightNetwork))
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 483, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(224, 224, 224)
                                .addComponent(jButton1))
                            .addComponent(minOccAsTarget, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(maxTargets4Calc, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cosineMin, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(minOccAsSource, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(directedNetwork)
                    .addComponent(weightNetwork))
                .addGap(18, 18, 18)
                .addComponent(cosineMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38)
                .addComponent(maxTargets4Calc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45)
                .addComponent(minOccAsTarget, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addComponent(minOccAsSource, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(62, 62, 62)
                        .addComponent(jButton1)))
                .addContainerGap(84, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void directedNetworkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_directedNetworkActionPerformed
        if (evt.getSource() == directedNetwork) {
            toggleDirectedNetwork = !toggleDirectedNetwork;
            System.out.println("toggle status is: "+toggleDirectedNetwork);
            directedNetwork.setSelected(toggleDirectedNetwork);
            if (directedNetwork.isSelected()) {
                directedNetwork.setText("directed network");
            } else {
                directedNetwork.setText("undirected network");
            }

    }//GEN-LAST:event_directedNetworkActionPerformed
    }
        private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Screen_1.screen_1.setVisible(true);
        Screen_1.screen_2.setVisible(false);
        
    }//GEN-LAST:event_jButton1ActionPerformed

    private void cosineMinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cosineMinActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cosineMinActionPerformed

    private void maxTargets4CalcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maxTargets4CalcActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_maxTargets4CalcActionPerformed

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField3ActionPerformed

    private void weightNetworkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_weightNetworkActionPerformed
        if (evt.getSource() == weightNetwork) {
            toggleWeightedNetwork = !toggleWeightedNetwork;
            System.out.println("toggle status is: "+toggleWeightedNetwork);
            weightNetwork.setSelected(toggleWeightedNetwork);
            if (weightNetwork.isSelected()) {
                weightNetwork.setText("weighted network");
            } else {
                weightNetwork.setText("unweighted network");
            }

    }
    }//GEN-LAST:event_weightNetworkActionPerformed

    private void minOccAsTargetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_minOccAsTargetActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_minOccAsTargetActionPerformed
    
/**
     * @param args the command line arguments
     */
//    public static void main(String args[]) {
//        /*
//         * Set the Nimbus look and feel
//         */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /*
//         * If Nimbus (introduced in Java SE 6) is not available, stay with the
//         * default look and feel. For details see
//         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(Screen_2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(Screen_2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(Screen_2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(Screen_2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /*
//         * Create and display the form
//         */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//
//            public void run() {
//                new Screen_2().setVisible(true);
//            }
//        });
//    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JTextField cosineMin;
    public javax.swing.JToggleButton directedNetwork;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTextField3;
    public javax.swing.JTextField maxTargets4Calc;
    public javax.swing.JTextField minOccAsSource;
    public javax.swing.JTextField minOccAsTarget;
    public javax.swing.JToggleButton weightNetwork;
    // End of variables declaration//GEN-END:variables
}
