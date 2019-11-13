package tank.com.kotlin.ndk;

public class BsPatchApi {

    static {
        System.loadLibrary("native-lib");
    }

    public native void doPatch(String sourcePath, String patchPath, String newApkPath);

}
