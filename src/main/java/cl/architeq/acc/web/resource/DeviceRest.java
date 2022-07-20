package cl.architeq.acc.web.resource;

import cl.architeq.acc.model.Device;
import cl.architeq.acc.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("ws/v1")
public class DeviceRest {

    @Autowired
    private DeviceService deviceService;

    @GetMapping(path = "/devices/page/{page}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Device>> fetchDevices(@PathVariable(value = "page") Integer page) {

        List<Device> deviceList = deviceService.fetchDevices();
        if (deviceList.size() > 0) {
            return new ResponseEntity<>(deviceList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(deviceList, HttpStatus.NO_CONTENT);
        }
    }


    @GetMapping(path = "/devices", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Device> fetchDevice(@RequestParam(value = "cod") Integer cod) {

        Device device = deviceService.fetchDevice(cod);
        if (device != null)
            return new ResponseEntity<>(device, HttpStatus.OK);
        else
            return new ResponseEntity<>(new Device(), HttpStatus.NO_CONTENT);
    }

    @PostMapping(path = "/devices", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Device> saveDevice(@RequestBody Device device, @RequestParam("locationCod") String locationCod) {

        Device dev = this.deviceService.saveDevice(device, locationCod);
        if (dev != null)
            return new ResponseEntity<>(dev, HttpStatus.OK);
        else
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }




}
