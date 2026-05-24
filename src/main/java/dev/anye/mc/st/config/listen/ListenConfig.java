package dev.anye.mc.st.config.listen;

import com.google.gson.reflect.TypeToken;
import dev.anye.core.json._JsonConfig;
import dev.anye.mc.st.ST;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ListenConfig extends _JsonConfig<ListenData> {
    private static final String File = ST.ConfigDir + "Listen.json";
    public static final ListenConfig I = new ListenConfig();
    public ListenConfig() {
        super(File, """
                {
                    "enable": true,
                    "port": 10101,
                    "token":\"""" +resetToken()+"""
                "
                }
                """, new TypeToken<>(){},false);
    }

    public int getPort() {
        return getDatas().getPort();
    }

    public static String resetToken() {
        try {
            return generateRandomString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static String generateRandomString() throws UnknownHostException, NoSuchAlgorithmException {
        long timestamp = System.currentTimeMillis();
        String hostName = InetAddress.getLocalHost().getHostName();
        String osName = System.getProperty("os.name");
        //String macAddress = getMacAddress();
        String combinedInfo = osName + hostName + timestamp;
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(combinedInfo.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.substring(0, 16);
    }
    private static String getMacAddress() {
        try {
            InetAddress ip = InetAddress.getLocalHost();
            byte[] mac = java.net.NetworkInterface.getByInetAddress(ip).getHardwareAddress();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X", mac[i]));
                if (i != mac.length - 1) sb.append("-");
            }
            return sb.toString();
        } catch (Exception e) {
            return "UnknownMAC";
        }
    }

    public String getToken() {
        return getDatas().getToken();
    }
}
