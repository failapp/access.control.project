package cl.architeq.acc.util;

import cl.architeq.acc.model.AntiPassback;
import cl.architeq.acc.model.Device;
import cl.architeq.acc.model.User;

public class LocalSync {

    private AntiPassback apb;
    private Device device;
    private User user;
    private boolean ack;

    public LocalSync() {
        // ..
    }

    public LocalSync(AntiPassback apb, Device device, boolean ack) {
        this.apb = apb;
        this.device = device;
        this.ack = ack;
    }

    public LocalSync(User user, Device device, boolean ack) {
        this.user = user;
        this.device = device;
        this.ack = ack;
    }

    public AntiPassback getApb() {
        return apb;
    }

    public void setApb(AntiPassback apb) {
        this.apb = apb;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public boolean isAck() {
        return ack;
    }

    public void setAck(boolean ack) {
        this.ack = ack;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {

        if (this.user == null) {
            return "LocalSync{" +
                    "apb=" + apb + ", device=" + device + ", ack=" + ack + '}';
        } else {
            return "LocalSync{" +
                    "user=" + user + ", device=" + device + ", ack=" + ack + '}';
        }

    }
}
