package ben_mkiv.ocdevices;

// autor: mimiru
public class BuildInfo {
    public static final String modName = "OCDevices";
    public static final String modID = "ocdevices";

    public static final String versionNumber = "@VERSION@";
    public static final String buildNumber = "@BUILD@";

    public static int getBuildNumber() {
        if (buildNumber.equals("@" + "BUILD" + "@"))
            return 0;
        return Integer.parseInt(buildNumber);
    }

    public static int getVersionNumber() {
        if (versionNumber.equals("@" + "VERSION" + "@"))
            return 0;
        return Integer.parseInt(versionNumber);
    }

    public static boolean isDevelopmentEnvironment() {
        return getBuildNumber() == 0;
    }
}
