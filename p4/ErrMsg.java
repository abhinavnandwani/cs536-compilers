/**
 * ErrMsg
 *
 * This class is used to generate warning and fatal error messages.
 */
class ErrMsg {
    // Tracks whether any fatal error has been reported
    private static boolean hasFatalError = false;

    /**
     * Generates a fatal error message.
     */
    static void fatal(int lineNum, int charNum, String msg) {
        System.err.println(lineNum + ":" + charNum + " ****ERROR**** " + msg);
        hasFatalError = true;
    }

    /**
     * Generates a warning message.
     */
    static void warn(int lineNum, int charNum, String msg) {
        System.err.println(lineNum + ":" + charNum + " ****WARNING**** " + msg);
    }

    /**
     * Checks if any fatal error has occurred.
     */
    static boolean anyErrors() {
        return hasFatalError;
    }
}
