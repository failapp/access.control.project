package cl.architeq.acc.util;

import java.util.List;


public class DeviceSync {

    private Integer deviceId;
    private String label;
    private List<String> dataList;

    public DeviceSync() {
        // ..
    }

    public DeviceSync(Integer deviceId, String label, List<String> dataList) {
        this.deviceId = deviceId;
        this.label = label;
        this.dataList = dataList;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<String> getDataList() {
        return dataList;
    }

    public void setDataList(List<String> dataList) {
        this.dataList = dataList;
    }


}
