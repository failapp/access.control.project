package cl.architeq.acc.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

public class Util {

    public static final DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter formatTime = DateTimeFormatter.ofPattern("HH:mm:ss");
    public static final DateTimeFormatter formatDateIDTi = DateTimeFormatter.ofPattern("yyyyMMdd");
    public static final DateTimeFormatter formatDateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter formatDateTimeIDTi = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public static final String REGEX_DNI = "^(\\d{1,8}-)([kK]{1}$|\\d{1}$)";
    //public static final String REGEX_DNI_FORMAT = "^(\\d{1}|\\d{2})\\.(\\d{3}\\.\\d{3}-)([kK]{1}$|\\d{1}$)";

    //public static AtomicBoolean CONNECT_SERVER_STATUS = new AtomicBoolean(false);
    public static AtomicBoolean CONNECT_LOCAL_STATUS = new AtomicBoolean( false);

    public static boolean SYNC_DEVICE_STATUS;

    public static final String UTF8_BOM = "\uFEFF";

    public static String removeUTF8BOM(String s) {
        if (s.startsWith(UTF8_BOM)) {
            s = s.substring(1);
        }
        return s;
    }

    public static Boolean isNumeric(String s) {
        return s.matches("[-+]?\\d*\\.?\\d+");
    }


    public static void sleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void sleepMillis(int millis) {
        try {
            TimeUnit.MILLISECONDS.sleep(millis);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }


    public static String bytesToString(byte[] _bytes, int numRead) {
        String str = "";
        for(int i = 0; i < numRead; i++) {
            str += (char)_bytes[i];
        }
        return str;
    }


    public static String formatDni(String str, int numBytesRead) {

        String dni = "";

        try {

            if (numBytesRead < 6) return "";

            System.out.println("--------------------------------------------------------------------------------------------------------------------------------------");
            System.out.println("numBytesRead -> " + numBytesRead);
            //System.out.println(str);
            System.out.println(str.substring(0,6));
            System.out.println("--------------------------------------------------------------------------------------------------------------------------------------");


            if (Util.isNumeric(str.substring(0,6))) {

                // PDF417 DE CEDULA ANTIGUA o CODIGO ESPECIFICO ..
                if (str.length() > 30) {

                    String[] arr = str.split(" ");
                    if (arr.length > 1) {
                        if (arr.length > 1) {
                            int dataLen = arr[0].trim().length();
                            dni = arr[0].trim();
                            if (dataLen == 8) dni = dni.substring(0, 7);
                            if (dataLen >= 9) dni = dni.substring(0, 8);
                        }
                    } else {
                        dni = str.substring(0, 6);  // CODIGO ESPECIFICO DE IMPLEMENTACION ..
                    }

                } else {
                    dni = str.trim();  // CODIGO ESPECIFICO DE IMPLEMENTACION ..
                }

            } else {
                // QR DE CEDULA ACTUAL ..
                if (str.contains("http")) {
                    String[] arr = str.split("=");
                    dni = (arr.length > 1) ? arr[1] : "";
                    arr = dni.split("&");
                    dni = (arr.length > 0) ? arr[0] : "";
                }
            }


            /*
            // PDF 417 ..
            if (numBytesRead >= 420) {
                // ..
            }
            */

            // CODIGO ESPECIFICO DE IMPLEMENTACION ..
            if (dni.isEmpty() && numBytesRead >= 8 && numBytesRead <= 20) {
                dni = str.trim();
            }



            // REGISTRAR LECTURAS FALLIDAS DE PDF 417 ..
            if (dni.isEmpty() && numBytesRead >= 100) {
                //System.out.println(LocalDateTime.now().format(Util.formatDateTime) + " FAIL -> " + str.substring(0,8));
                //System.out.println(str);
                dni = "000000000000";
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return dni;

    }



    public static String getIpAddr() {

        String ipAddr = "";
        InetAddress ip;
        try {
            ip = InetAddress.getLocalHost();
            ipAddr = ip.getHostAddress();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ipAddr;
    }


    public static String getMacAddr() {

        String macAddr = "";
        InetAddress ip;
        try {
            ip = InetAddress.getLocalHost();

            if (ip == null) return "";

            NetworkInterface network = NetworkInterface.getByInetAddress(ip);

            if (network == null) return "";

            byte[] mac = network.getHardwareAddress();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
            macAddr = sb.toString().toUpperCase();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return macAddr;
    }



    // NO funciona en windows, solo en Linux ..
    public static boolean pingIpAddr(String ipAddr) {
        boolean bln = false;
        try {
            InetAddress ping = InetAddress.getByName(ipAddr);
            bln = (ping.isReachable(5000)) ? true : false;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return bln;
    }




}
