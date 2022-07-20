package cl.architeq.acc.service;

import cl.architeq.acc.util.Util;
import com.pi4j.io.gpio.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.Properties;

@Slf4j
@Service
public class GPIOService {


    private static GpioController gpio;
    private static GpioPinDigitalOutput pinRelayIN;
    private static GpioPinDigitalOutput pinRelayOUT;


    @PostConstruct
    private void init() {

        String arch = System.getProperty("os.arch");
        if (arch.toLowerCase().contains("arm")) {

            gpio = GpioFactory.getInstance();
            pinRelayIN = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, "pinRelay", PinState.HIGH); //OFF
            pinRelayOUT = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05, "pinRelay", PinState.HIGH); //OFF

        } else {
            log.info("NO se detecta arquitectura ARM !!");
        }


        //System.out.println("os.name -> " + System.getProperty("os.name"));

        /*
        Properties props = System.getProperties();
        Enumeration<Object> keys = props.keys();
        while(keys.hasMoreElements()){
            Object key = keys.nextElement();
            Object value = props.get(key);
            System.out.println("Key: "+key + " Value: "+value);
        }
        */

    }

    @Async
    public void activateRelayIN() {

        if (pinRelayIN == null) return;

        pinRelayIN.low(); // ON ..
        Util.sleepMillis(1000);
        pinRelayIN.high(); // OFF ..

    }

    @Async
    public void activateRelayOUT() {
        if (pinRelayOUT == null) return;

        pinRelayOUT.low(); // ON ..
        //Util.sleep(2);
        Util.sleepMillis(1000);
        pinRelayOUT.high(); // OFF ..

    }



}
