package com.tradeforge;

import com.tradeforge.controller.LoginController;
import com.tradeforge.mediator.TradingMediator;
import com.tradeforge.view.LoginFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * TradeForge Main Entry Point
 * 
 * Launches the TradeForge Design Pattern Lab Desktop Application.
 */
public class Main {

    public static void main(String[] args) {
        // Configure clean modern Look and Feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
        }

        // Start desktop application on the Swing Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                TradingMediator mediator = new TradingMediator();
                LoginFrame loginFrame = new LoginFrame();
                new LoginController(loginFrame, mediator);
                loginFrame.setVisible(true);
            } catch (Exception ex) {
                System.err.println("Fatal error launching TradeForge: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
    }
}
