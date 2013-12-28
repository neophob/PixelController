package com.neophob.sematrix.cli;

public final class ClientHelper {

    private ClientHelper() {
        // no instance
    }

    /**
     * 
     * @param s
     * @param length
     * @return
     */
    public static String pretifyString(String s, int length) {
        StringBuilder sb = new StringBuilder();

        sb.append(s);
        while (sb.length() < length) {
            sb.append(' ');
        }

        return sb.toString();
    }
}
