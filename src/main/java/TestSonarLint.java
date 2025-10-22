// File di test per dimostrare SonarLint
import java.util.logging.Logger;

public class TestSonarLint {
    private static final Logger logger = Logger.getLogger(TestSonarLint.class.getName());

    public void testProblems() {
        // Problema 1: Concatenazione di stringhe nei log (S4792)
        String user = "test@example.com";
        int id = 123;
        logger.info("User logged in: " + user + " with ID: " + id); // ❌ SonarLint segnalerà questo

        // Problema 2: Catch generico (S1166)
        try {
            // qualche operazione
            // sonar:ignore=S1854 // Il valore dell'espressione non viene utilizzato intenzionalmente
            int result = 10 / 0;
        } catch (Exception e) { // ❌ SonarLint segnalerà questo
            logger.severe("Error occurred: " + e.getMessage());
        }

        // Problema 3: Metodo troppo complesso (S1541)
        // Questo metodo ha troppi livelli di annidamento
        if (user != null) {
            if (user.length() > 0) {
                if (user.contains("@")) {
                    if (user.endsWith(".com")) {
                        logger.info("Valid email: " + user);
                        // più livelli...
                        if (id > 0) {
                            if (id < 1000) {
                                if (id % 2 == 0) {
                                    logger.info("Even ID: " + id);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Problema 4: Metodo con complessità cognitiva alta (S3776)
    public String complexMethod(String input, boolean flag1, boolean flag2, int value) {
        String result = "";

        if (input != null && !input.isEmpty()) {
            if (flag1 && flag2) {
                if (value > 10) {
                    result = input.toUpperCase();
                } else if (value < 5) {
                    result = input.toLowerCase();
                } else {
                    result = input;
                }
                if (result.length() > 20) {
                    result = result.substring(0, 20) + "...";
                }
            } else if (flag1 || flag2) {
                result = flag1 ? input.toUpperCase() : input.toLowerCase();
            } else {
                result = input;
            }
        } else {
            result = "default";
        }

        return result;
    }
}
