package service;

import java.util.logging.Logger;

public class SimpleTest {
    private static final Logger logger = Logger.getLogger(SimpleTest.class.getName());

    public void badMethod() {
        // Questo dovrebbe essere segnalato da SonarLint
        logger.info("Test: " + "concatenazione");

        try {
            // Test divisione con controllo del denominatore
            int denominator = 0;
            if (denominator == 0) {
                throw new ArithmeticException("Divisione per zero non consentita");
            }
            int result = 1 / denominator;
            logger.info("Risultato: " + result);
        } catch (ArithmeticException e) {
            // Usa logger invece di System.out
            logger.severe("Errore aritmetico: " + e.getMessage());
        } catch (Exception e) {
            logger.severe("Errore generico: " + e.getMessage());
        }
    }
}

