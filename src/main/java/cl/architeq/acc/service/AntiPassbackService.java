package cl.architeq.acc.service;

import cl.architeq.acc.model.AntiPassback;
import cl.architeq.acc.model.Device;
import cl.architeq.acc.repository.AntiPassbackRepository;
import cl.architeq.acc.repository.DeviceRepository;
import cl.architeq.acc.util.LocalSync;
import cl.architeq.acc.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.HttpHostConnectException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.ConnectException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.chrono.ChronoPeriod;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AntiPassbackService {

    @Autowired
    private Environment env;

    @Autowired
    private AntiPassbackRepository antiPassbackRepo;

    @Autowired
    private DeviceRepository deviceRepo;

    private Integer DEVICE_ID;

    private static List<LocalSync> localSyncList = Collections.synchronizedList( new ArrayList<>() );


    @PostConstruct
    private void init() {
        // ..
        DEVICE_ID = Integer.parseInt(env.getProperty("conf.device.id").trim());
    }


    public AntiPassback saveAPB(AntiPassback apb) {

        AntiPassback antiPassback = null;

        try {

            log.info("!! ACTUALIZAR ESTADO ANTIPASSBACK -> {}", apb.toString());

            antiPassback = antiPassbackRepo.findFirstByUserId(apb.getUserId()).orElse(null);

            if (antiPassback == null) {
                antiPassback = new AntiPassback(apb.getUserId(), apb.getUserStatus());
                antiPassback = antiPassbackRepo.save(antiPassback);
            } else {

                // Algoritmo de validacion de mapa de estados de cada unidad ..
                // comparar fechas de registro ..

                if ( apb.getDateTime().isAfter ( antiPassback.getDateTime()) ) {

                    antiPassback.setUserStatus( apb.getUserStatus() );
                    antiPassback.setDateTime( apb.getDateTime() );
                    antiPassback.setSync(1); // finalizar propagacion ..
                    antiPassback = antiPassbackRepo.save(antiPassback);

                } else {
                    antiPassback.setSync(1); // finalizar propagacion ..
                }

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return antiPassback;

    }


    public AntiPassback fetchAPB(String userId) {
        // ..
        return antiPassbackRepo.findByUserId(userId).orElse(null);
    }


    public List<AntiPassback> fetchDataAPB(LocalDateTime from, LocalDateTime to, Integer page) {

        if (page == null) return new ArrayList<>();
        if (page < 0) return new ArrayList<>();
        page = page - 1;
        Pageable pageable = PageRequest.of(page, 10, Sort.by("dateTime").descending());
        return antiPassbackRepo.findByDateTimeBetween(from, to, pageable).orElse( new ArrayList<>());

    }


    public void distributeAPB(AntiPassback apb) {

        try {

            List<Device> deviceList = deviceRepo.findByCodNotAndEnabled(DEVICE_ID, true).orElse(new ArrayList<>());
            deviceList.forEach( x -> {

                if (Util.CONNECT_LOCAL_STATUS.get()) {
                    boolean success = this.sendEndPointAPB( apb, x );
                    if (success) {
                        apb.setSync(1);
                        antiPassbackRepo.save(apb);
                    } else {
                        localSyncList.removeIf( t -> t.getApb().getUserId().equals(apb.getUserId()) && t.getDevice().getCod().equals(x.getCod()) );
                        LocalSync localSync = new LocalSync(apb, x, false);
                        localSyncList.add(localSync);
                    }
                } else {
                    localSyncList.removeIf( t -> t.getApb().getUserId().equals(apb.getUserId()) && t.getDevice().getCod().equals(x.getCod()) );
                    LocalSync localSync = new LocalSync(apb, x, false);
                    localSyncList.add(localSync);
                }

            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    @Async
    @Scheduled(fixedDelay = 3000, initialDelay = 5000)
    public void synchronizeAPB() throws Exception {

        //System.out.println("!Util.CONNECT_LOCAL_STATUS.get() -> " + Util.CONNECT_LOCAL_STATUS.get());

        if (!Util.CONNECT_LOCAL_STATUS.get()) return;

        // filtro cantidad de dispositvos dispnibles en la red ..
        List<Device> deviceList = deviceRepo.findByCodNotAndEnabled(DEVICE_ID, true).orElse(new ArrayList<>());
        if (deviceList.size() == 0) return;


        List<AntiPassback> apbList = antiPassbackRepo.findBySync(0).orElse(new ArrayList<>());

        // quitar del mapa de transmision los registros con fecha desfasada (hasta 8 hrs .. )
        apbList.forEach( x -> {
            LocalDateTime ldt = LocalDateTime.now();
            Duration duration = Duration.between( ldt, x.getDateTime() );
            long diff = Math.abs(duration.toMinutes());
            // maximo 8 horas ..
            if (diff > 480) {
                x.setSync(1);
                antiPassbackRepo.save(x);
            }
        });


        apbList.forEach( apb -> {
            if ( apb.getSync().equals(0) ) {
                deviceList.forEach( device -> {
                    localSyncList.removeIf( x -> x.getApb().getUserId().equals(apb.getUserId()) && x.getDevice().getCod().equals(device.getCod()) );
                    LocalSync transmission = new LocalSync(apb, device, false);
                    localSyncList.add(transmission);
                });
            }
        });

        long count = localSyncList.stream().filter( x -> !x.isAck() ).count();
        log.info("COUNT MAP SYNCHRONIZATION ANTIPASSBACK -> {}", count);
        if (count == 0) return;


        localSyncList.forEach( x -> {
            boolean success = this.sendEndPointAPB( x.getApb(), x.getDevice() );
            x.setAck(success);
            log.info(" REGISTRO LISTA TRANSMISION {}", x.toString());
        });


        apbList.forEach( apb -> {

            int cnt = 0;
            for (Device device : deviceList) {
                LocalSync transmission = localSyncList.stream()
                                                            .filter( x -> x.getApb().getUserId().equals(apb.getUserId()) && x.getDevice().getCod().equals(device.getCod()) )
                                                            .findAny()
                                                            .orElse(null);
                if (transmission != null) {
                    log.info("transmission.isAck() -> {}", transmission.isAck());
                    if (transmission.isAck()) {
                        cnt++;
                    }
                }
            }

            if (cnt == deviceList.size()) {
                apb.setSync(1);
                antiPassbackRepo.save(apb);
            }

        });

        // actualizar lista de transmision ..
        log.info("LISTA DE SINCRONIZACION ANTIPASSBACK - RED LOCAL - CANTIDAD: {}", localSyncList.size());
        List<LocalSync> toRemove = new ArrayList<>();
        for (LocalSync sync : localSyncList) {
            if (sync.isAck()) {
                toRemove.add(sync);
            }
        }
        localSyncList.removeAll(toRemove);
        log.info("LISTA DE SINCRONIZACION ANTIPASSBACK - RED LOCAL - CANTIDAD: {}", localSyncList.size());

    }



    private boolean sendEndPointAPB (AntiPassback apb, Device device) {

        if (!Util.CONNECT_LOCAL_STATUS.get()) return false;
        boolean bln = false;
        String url = "";

        try {

            if (device.getTcpPortService() == null || device.getTcpPortService() <= 0) return false;

            /*
            // FALLA FUNCION PING A PC .. !!!
            if (! Util.pingIpAddr( device.getIpAddr() )) {
                System.out.println("FALLA PING A HOST " + device.getIpAddr());
                return false;
            }
            */

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            RestTemplate restTemplate = new RestTemplate();
            url = "http://" + device.getIpAddr() + ":" + device.getTcpPortService() + "/ws/v1/antipassback";

            HttpEntity<AntiPassback> payload = new HttpEntity<>(apb, headers);
            ResponseEntity<AntiPassback> response = restTemplate.exchange(url, HttpMethod.POST, payload, AntiPassback.class);

            log.info("ENDPOINT ANTIPASSBACK LOCAL -> " + url + " - HTTP CODE RESPONSE -> {}",response.getStatusCode());

            if (response.getStatusCode() == HttpStatus.OK) {
                bln = true;
            }

        } catch (Exception ex) {

            if (ex instanceof HttpHostConnectException || ex.getCause() instanceof ConnectException) {
                log.error("!!! FALLA CONEXION ENDPOINT ANTIPASSBACK " + url + " ERROR ： " + ex.getMessage());
            } else {
                ex.printStackTrace();
            }

        }

        return bln;

    }


}
