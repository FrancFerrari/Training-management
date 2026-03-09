package com.allenamento;

import com.allenamento.view.MainWindow;
import javafx.application.Application;

/**
 * Main class for starting the Allenamento Manager application.
 * Entry point of the JavaFX application.
 */
public class AllenamentoManagerApp {
    /**
     * Main method to start the application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        // Start the JavaFX application
        Application.launch(MainWindow.class, args);
    }
}
