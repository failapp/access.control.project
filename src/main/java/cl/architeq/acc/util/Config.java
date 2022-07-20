package cl.architeq.acc.util;


public class Config {

    private String urlEndpointWebservice;
    private String urlEndpointWebsocket;
    private String urlEndpointWebsocketSubscribe;
    private String customerId;
    private String customerKey;

    private String companyId;
    private String locationId;
    private String deviceId;
    private String deviceName;
    private String deviceModel;
    private String comPortIn;
    private String comPortOut;

    private String ipAddr;
    private String macAddr;


    public Config() {
        //..
    }

    public String getUrlEndpointWebservice() {
        return urlEndpointWebservice;
    }

    public void setUrlEndpointWebservice(String urlEndpointWebservice) {
        this.urlEndpointWebservice = urlEndpointWebservice;
    }

    public String getUrlEndpointWebsocket() {
        return urlEndpointWebsocket;
    }

    public void setUrlEndpointWebsocket(String urlEndpointWebsocket) {
        this.urlEndpointWebsocket = urlEndpointWebsocket;
    }

    public String getUrlEndpointWebsocketSubscribe() {
        return urlEndpointWebsocketSubscribe;
    }

    public void setUrlEndpointWebsocketSubscribe(String urlEndpointWebsocketSubscribe) {
        this.urlEndpointWebsocketSubscribe = urlEndpointWebsocketSubscribe;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerKey() {
        return customerKey;
    }

    public void setCustomerKey(String customerKey) {
        this.customerKey = customerKey;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getComPortIn() {
        return comPortIn;
    }

    public void setComPortIn(String comPortIn) {
        this.comPortIn = comPortIn;
    }

    public String getComPortOut() {
        return comPortOut;
    }

    public void setComPortOut(String comPortOut) {
        this.comPortOut = comPortOut;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public String getMacAddr() {
        return macAddr;
    }

    public void setMacAddr(String macAddr) {
        this.macAddr = macAddr;
    }
}
