package com.tradeforge.controller;

import com.tradeforge.mediator.TradingMediator;
import com.tradeforge.model.User;
import com.tradeforge.view.DashboardFrame;
import com.tradeforge.view.LoginFrame;
import java.awt.event.ActionEvent;

/**
 * CONTROLLER: LoginController
 * Handles user authentication actions from the LoginFrame.
 */
public class LoginController {

    private final LoginFrame view;
    private final TradingMediator mediator;

    public LoginController(LoginFrame view, TradingMediator mediator) {
        this.view = view;
        this.mediator = mediator;
        initController();
    }

    private void initController() {
        view.addLoginListener(this::handleLogin);
    }

    private void handleLogin(ActionEvent event) {
        String username = view.getUsername();
        String password = view.getPassword();

        if (username.isEmpty() || password.isEmpty()) {
            view.setErrorMessage("Please enter both username and password.");
            return;
        }

        try {
            User user = mediator.login(username, password);
            if (user == null) {
                view.setErrorMessage("Invalid credentials. Please try again.");
                return;
            }

            if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
                view.setErrorMessage("Your account is not active. Please contact administrator.");
                return;
            }

            // Authentication successful -> Open Dashboard
            view.setVisible(false);
            view.dispose();

            DashboardFrame dashboardView = new DashboardFrame();
            new DashboardController(dashboardView, mediator, user);
            dashboardView.setVisible(true);

        } catch (Exception ex) {
            view.setErrorMessage("Database connection error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
