package com.github.sisong;

public class HPatch {
    private static final long DEFAULT_CACHE_MEMORY_BYTES = 8 * 1024 * 1024L;

    static {
        System.loadLibrary("hpatchz");
    }

    public static native int patch(
            String oldFileName,
            String diffFileName,
            String outNewFileName,
            long cacheMemory,
            int threadNum
    );

    public static int patch(String oldFileName, String diffFileName, String outNewFileName, long cacheMemory) {
        return patch(oldFileName, diffFileName, outNewFileName, cacheMemory, 1);
    }

    public static int patch(String oldFileName, String diffFileName, String outNewFileName) {
        return patch(oldFileName, diffFileName, outNewFileName, DEFAULT_CACHE_MEMORY_BYTES, 1);
    }
}
