package cl.architeq.acc.util;

public enum EventCode {

    ACCESS_GRANTED("Access granted", 200),
    ACCESS_GRANTED_BY_SCANNER("Access granted by scanner", 201),
    ACCESS_GRANTED_BY_RFID("Access granted by RFID", 202),
    ACCESS_GRANTED_BY_HID("Access granted by HID", 203),
    ACCESS_GRANTED_BY_FP("Access granted by finger print", 204),

    ACCESS_GRANTED_BY_CONNECT_FAIL("Access granted by connect status fail", 222),

    ACCESS_DENY("Access deny", 400),
    ACCESS_DENY_BY_SCANNER("Access deny by scanner", 401),
    ACCESS_DENY_BY_RFID("Access deny by RFID", 402),
    ACCESS_DENY_BY_HID("Access deny by HID", 403),
    ACCESS_DENY_BY_FP("Access deny by finger print", 404),
    ACCESS_DENY_BY_APB("Access deny by anti-passback", 430),
    ACCESS_DENY_BY_APB_IN("Access deny by anti-passback of IN", 431),
    ACCESS_DENY_BY_APB_OUT("Access deny by anti-passback of OUT", 432),
    ACCESS_DENY_BY_APB_IN_SCANNER("Access deny by anti-passback of IN - Scanner", 433),
    ACCESS_DENY_BY_APB_OUT_SCANNER("Access deny by anti-passback of OUT - Scanner", 434),
    ACCESS_DENY_BY_APB_IN_RFID("Access deny by anti-passback of IN - RFID", 435),
    ACCESS_DENY_BY_APB_OUT_RFID("Access deny by anti-passback of OUT - RFID", 436),
    ACCESS_DENY_BY_APB_IN_HID("Access deny by anti-passback of IN - HID", 437),
    ACCESS_DENY_BY_APB_OUT_HID("Access deny by anti-passback of OUT - HID", 438),


    USER_CREATED("User created success", 301),
    USER_UPDATED("User updated success", 302),
    USER_DELETED("User deleted success", 303),
    USER_SYNCHRONIZED("User synchronized success", 304);


    private String description;
    private int code;

    EventCode(String description, int code) {
        this.description = description;
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public int getCode() {
        return code;
    }


    public static String getName( int code ) {
        for (EventCode eventCode: EventCode.values()) {
            if (eventCode.getCode() == code) {
                return eventCode.name();
            }
        }
        return "";
    }

}
