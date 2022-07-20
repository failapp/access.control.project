package cl.architeq.acc.service;

import cl.architeq.acc.util.Util;
import com.fazecast.jSerialComm.SerialPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.LocalDateTime;

@Slf4j
@Service
public class SerialPortService {

    @Autowired
    private Environment env;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private EventDataService eventDataService;

    private static SerialPort comPortIN;
    private static SerialPort comPortOUT;

    @PostConstruct
    private void init(){

        //System.out.println("!! config serial port (in .. / out ..)");

        try {

            final String portIn = env.getProperty("conf.com.port.in");
            final String portOut = env.getProperty("conf.com.port.out");

            SerialPort[] comPorts = SerialPort.getCommPorts();
            log.info("SERIAL PORTS DETECTED -> {}", comPorts.length);

            if (comPorts.length == 1)
                comPortIN =  SerialPort.getCommPort( portIn );

            if (comPorts.length >= 2)
                comPortIN = SerialPort.getCommPort( portIn );
                comPortOUT = SerialPort.getCommPort( portOut );

            if (comPortIN != null)
                log.info("COMM IN -> {} {}", comPortIN.getSystemPortName(), comPortIN.getDescriptivePortName());

            if (comPortOUT != null)
                log.info("COMM OUT -> {} {}", comPortOUT.getSystemPortName(), comPortOUT.getDescriptivePortName());

        } catch ( Exception ex ) {
            ex.printStackTrace();
        }


    }



    @Async
    public void handlerFlowIN(){

        if (comPortIN == null) return;

        comPortIN.openPort();
        comPortIN.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 100, 0);
        //comPortIN.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);

        log.info("lectura scanner de entrada ..");

        while (true) {

            try {

                if (comPortIN.isOpen()) {

                    byte[] readBuffer = new byte[512]; //1024
                    int numRead = comPortIN.readBytes(readBuffer, readBuffer.length);

                    if (numRead > 0) {
                        String str = Util.bytesToString(readBuffer, numRead);
                        String dni = Util.formatDni(str, numRead);
                        log.info("ENTRADA .. DNI: {}", dni);
                        eventDataService.registerAccessData(LocalDateTime.now(), dni, 1);
                    }

                } else {
                    comPortIN.openPort();
                }

                Util.sleep(1);
                //Util.sleepMillis(500);

            } catch (Exception ex) {
                ex.printStackTrace();
                continue;
            }
        }


    }



    @Async
    public void handlerFlowOUT(){

        if (comPortOUT == null) return;

        comPortOUT.openPort();
        comPortOUT.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 100, 0);
        //comPortOUT.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);

        log.info("lectura scanner de salida ..");

        while (true) {

            try {

                if (comPortOUT.isOpen()) {

                    byte[] readBuffer = new byte[512]; // 1024
                    int numRead = comPortOUT.readBytes(readBuffer, readBuffer.length);

                    if (numRead > 0) {

                        String str = Util.bytesToString(readBuffer, numRead);
                        String dni = Util.formatDni(str, numRead);

                        log.info("SALIDA .. DNI: {}", dni);
                        eventDataService.registerAccessData(LocalDateTime.now(), dni, 0);

                    }

                } else {
                    comPortOUT.openPort();
                }

                Util.sleep(1);
                //Util.sleepMillis(500);

            } catch (Exception ex) {
                ex.printStackTrace();
                continue;
            }
        }


    }




    @PreDestroy
    private void preDestroy() {

        try {
            if (comPortIN != null) {
                if (comPortIN.isOpen()) {
                    comPortIN.closePort();
                    log.info("CERRAR PUERTO SERIAL - CONTROL ENTRADA !!");
                }
            }
        }catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            if (comPortOUT != null) {
                if (comPortOUT.isOpen()) {
                    comPortOUT.closePort();
                    log.info("CERRAR PUERTO SERIAL - CONTROL SALIDA !!");
                }
            }
        } catch (Exception ex ) {
            ex.printStackTrace();
        }

    }

}
