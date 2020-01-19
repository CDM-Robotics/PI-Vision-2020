package team6072.vision.logging;

public class LogWrapper {

    private String mName;
    private FileType mFileType;
    private Permission mPermission;

    private String mIdentifier;
    private String mPrintString;
    private String mAlarmString;
    private String mWarningString;
    private String mErrorString;

    private String mDebugString;
    private String mDebugString2;
    private String mDebugString3;
    private String mDebugString4;

    /**
     * The type of file
     */
    public enum FileType {
        MAIN, PIPELINE, CONFIG, CAMERA_SYSTEM, NETWORK_TABLES;
    }

    /**
     * The permission extent to which a logger can log things.
     * 
     * Periodic_Off - disables all periodic loggers but leaves everything else, used
     * to clean us the debugger a little bit ALL - enables all loggers
     * Warnings_and_errors - only allows warnings and errors to be shown by that
     * logger Errors_only - only allows errors to be shown
     */
    public enum Permission {
        PERIODIC_OFF, ALL, WARNINGS_AND_ERRORS, ERRORS_ONLY;
    }

    public LogWrapper(FileType fileType, String name, Permission permission) {
        mName = name;
        mFileType = fileType;
        mPermission = permission;

        mIdentifier = mFileType.toString() + ": " + mName + ":";
        mPrintString = (mIdentifier + " %s \n");
        mAlarmString = "**ALARM: " + mPrintString;
        mWarningString = "****************************************************************************\nWARNING: "
                + mPrintString + "****************************************************************************\n";
        mErrorString = "****************************************************************************\nERROR: "
                + mPrintString + "****************************************************************************\n";

        mDebugString = mIdentifier + "  %s: %.3f\n";
        mDebugString2 = mIdentifier + "  %s: %.3f  %s: %.3f\n";
        mDebugString3 = mIdentifier + "  %s: %.3f  %s: %.3f  %s: %.3f\n";
        mDebugString4 = mIdentifier + "  %s: %.3f  %s: %.3f  %s: %.3f  %s: %.3f\n";

    }

    private int mIterations;

    /**
     * Prints a string after it has been called a certian number of times prevents
     * console overload
     * 
     * @param s          the string
     * @param iterations the number of iterations before it logs
     */
    public void periodicPrint(String s, int iterations) {
        if (mPermission == Permission.ALL) {
            if (mIterations % iterations == 0) {
                System.out.printf(mPrintString, s);
            }
            mIterations++;
        }
    }

    /**
     * prints the strinng
     * 
     * @param s
     */
    public void print(String s) {
        if (mPermission == Permission.ALL || mPermission == Permission.PERIODIC_OFF) {
            System.out.printf(mPrintString, s);
        }
    }

    /**
     * Prints the string with a special tag
     */
    public void alarm(String s) {
        if (mPermission == Permission.ALL || mPermission == Permission.PERIODIC_OFF) {
            System.out.printf(mAlarmString, s);
        }
    }

    /**
     * Makes a noticable string in the console
     * 
     * @param s
     */
    public void warning(String s) {
        if (mPermission == Permission.ALL || mPermission == Permission.WARNINGS_AND_ERRORS
                || mPermission == Permission.PERIODIC_OFF) {
            System.out.printf(mWarningString, s);
        }
    }

    /**
     * makes a VERY noticable string in the console and cannot be blocked in any way
     * 
     * @param s
     */
    public void error(String s) {
        System.out.printf(mErrorString, s);

    }

    /**
     * Takes a certian number of variables and names and formats the string to be
     * good for tracking the numbers over a period of time
     * 
     * @param name
     * @param var
     */
    public void debug(String name, double var) {
        if (mPermission == Permission.ALL || mPermission == Permission.PERIODIC_OFF) {
            System.out.printf(mDebugString, name, var);
        }
    }

    /**
     * 
     * Takes a certian number of variables and names and formats the string to be
     * good for tracking the numbers over a period of time
     * @param name
     * @param var
     * @param name2
     * @param var2
     */
    public void debug(String name, double var, String name2, double var2) {
        if (mPermission == Permission.ALL || mPermission == Permission.PERIODIC_OFF) {
            System.out.printf(mDebugString2, name, var, name2, var2);
        }
    }

    /**
     * 
     * Takes a certian number of variables and names and formats the string to be
     * good for tracking the numbers over a period of time
     * @param name
     * @param var
     * @param name2
     * @param var2
     * @param name3
     * @param var3
     */
    public void debug(String name, double var, String name2, double var2, String name3, double var3) {
        if (mPermission == Permission.ALL || mPermission == Permission.PERIODIC_OFF) {
            System.out.printf(mDebugString3, name, var, name2, var2, name3, var3);
        }
    }
    /**
     * 
     * Takes a certian number of variables and names and formats the string to be
     * good for tracking the numbers over a period of time
     * @param name
     * @param var
     * @param name2
     * @param var2
     * @param name3
     * @param var3
     * @param name4
     * @param var4
     */
    public void debug(String name, double var, String name2, double var2, String name3, double var3, String name4,
            double var4) {
        if (mPermission == Permission.ALL || mPermission == Permission.PERIODIC_OFF) {
            System.out.printf(mDebugString4, name, var, name2, var2, name3, var3, name4, var4);
        }
    }

    /**
     * Takes a certian number of variables and names and formats the string to be
     * good for tracking the numbers over a period of time
     * This has a periodic attribute, however, that prevents console overload
     * Therefore it only prints after it has been called a certian number of times
     * @param iterations number of timmes it must be called before it prints
     * @param name
     * @param var
     */
    public void periodicDebug(int iterations, String name, double var) {
        if (mPermission == Permission.ALL) {
            if (mIterations % iterations == 0) {
                System.out.printf(mDebugString, name, var);
            }
            mIterations++;
        }
    }

    /**
     * Takes a certian number of variables and names and formats the string to be
     * good for tracking the numbers over a period of time
     * This has a periodic attribute, however, that prevents console overload
     * Therefore it only prints after it has been called a certian number of times
     * @param iterations
     * @param name
     * @param var
     * @param name2
     * @param var2
     */
    public void periodicDebug(int iterations, String name, double var, String name2, double var2) {
        if (mPermission == Permission.ALL) {
            if (mIterations % iterations == 0) {
                System.out.printf(mDebugString2, name, var, name2, var2);
            }
            mIterations++;
        }
    }

    /**
     * Takes a certian number of variables and names and formats the string to be
     * good for tracking the numbers over a period of time
     * This has a periodic attribute, however, that prevents console overload
     * Therefore it only prints after it has been called a certian number of times
     * @param iterations number of timmes it must be called before it prints
     * @param name
     * @param var
     * @param name2
     * @param var2
     * @param name3
     * @param var3
     */
    public void periodicDebug(int iterations, String name, double var, String name2, double var2, String name3,
            double var3) {
        if (mPermission == Permission.ALL) {
            if (mIterations % iterations == 0) {
                System.out.printf(mDebugString3, name, var, name2, var2, name3, var3);
            }
            mIterations++;
        }
    }

    /**
     * Takes a certian number of variables and names and formats the string to be
     * good for tracking the numbers over a period of time
     * This has a periodic attribute, however, that prevents console overload
     * Therefore it only prints after it has been called a certian number of times
     * @param iterations number of timmes it must be called before it prints
     * @param name
     * @param var
     * @param name2
     * @param var2
     * @param name3
     * @param var3
     * @param name4
     * @param var4
     */
    public void periodicDebug(int iterations, String name, double var, String name2, double var2, String name3,
            double var3, String name4, double var4) {
        if (mPermission == Permission.ALL) {
            if (mIterations % iterations == 0) {
                System.out.printf(mDebugString4, name, var, name2, var2, name3, var3, name4, var4);
            }
            mIterations++;
        }
    }
}