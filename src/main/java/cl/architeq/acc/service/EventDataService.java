package cl.architeq.acc.service;


import cl.architeq.acc.model.*;
import cl.architeq.acc.repository.*;
import cl.architeq.acc.util.EventCode;
import cl.architeq.acc.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class EventDataService {

    @Autowired
    private Environment env;

    @Autowired
    private EventDataRepository eventDataRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private DeviceRepository deviceRepo;

    @Autowired
    private AntiPassbackRepository antiPassbackRepo;

    @Autowired
    private GPIOService gpioService;

    @Autowired
    private SyncStatusRepository syncStatusRepo;

    @Autowired
    private AntiPassbackService antiPassbackService;


    private static Device device;
    private static Integer deviceId;
    private static String locationId;
    private static String companyId;

    private static String URL_WS;
    private static String customerId;
    private static String customerKey;

    private static String SYNC_INFO_EVENTDATA;
    private static String SYNC_INFO_USER_STATUS;


    @PostConstruct
    private void init() throws Exception {

        URL_WS = env.getProperty("url.endpoint.webservice").trim();
        customerId = env.getProperty("customer.id").trim();
        customerKey = env.getProperty("customer.key").trim();

        SYNC_INFO_EVENTDATA = env.getProperty("conf.sync.info.eventdata").trim();
        SYNC_INFO_USER_STATUS = env.getProperty("conf.sync.notify.status.user").trim();

        deviceId = Integer.parseInt(env.getProperty("conf.device.id"));
        device = deviceRepo.findByCod( deviceId ).orElse(null);
        locationId = (device != null) ? device.getLocation().getCod() : env.getProperty("conf.location.id");
        companyId = (device != null) ? device.getLocation().getCompany().getCod() : env.getProperty("conf.company.id");

    }


    public void registerAccessData(LocalDateTime ldt, String dni, int accessType) throws Exception {
        this.registerDataScanner(ldt, dni, accessType);
    }

    @Async
    public void registerDataScanner(final LocalDateTime ldt, final String dni, final int accessType) throws Exception {

        if (dni.isEmpty()) return;

        EventData eventData = null;
        AntiPassback antiPassback = null;

        //User user = userRepo.findByDni(dni).orElse(null);
        //System.out.println(LocalDateTime.now().format(Util.formatDateTime) + " DNI -> " + dni);

        User user = null;
        if (dni.length() > 5) {
            user = userRepo.findFirstByDniContainingIgnoreCase(dni).orElse(null);
        }

        if (user != null) {
            if (user.getEnabled()) {

                if (user.getAntiPassback() && Util.CONNECT_LOCAL_STATUS.get()) {

                    antiPassback = antiPassbackRepo.findByUserId(user.getDni()).orElse(null);
                    if (antiPassback == null) {

                        antiPassback = new AntiPassback(user.getDni(), accessType);
                        antiPassbackRepo.save(antiPassback);

                        // distribuir estado de antipassback a dispositivos de la localidad !!
                        antiPassbackService.distributeAPB(antiPassback);

                        // CONCEDER ACCESO : USUARIO HABILITADO ..
                        log.info("CONCEDER ACCESO: USUARIO {}, {}", user.getDni(), LocalDateTime.now().format(Util.formatDateTime));

                        if (accessType == 1) gpioService.activateRelayIN(); else gpioService.activateRelayOUT();
                        EventCode eventCode = EventCode.ACCESS_GRANTED_BY_SCANNER;
                        eventData = new EventData(ldt, eventCode.getCode(), user.getDni(), deviceId, accessType, locationId);

                    } else {

                        int dayEvent = ldt.getDayOfYear();
                        int dayAPB   = antiPassback.getDateTime().getDayOfYear();

                        if (dayEvent == dayAPB && accessType == antiPassback.getUserStatus()) {

                            log.info("CONTROL ANTIPASSBACK: NO PUEDE PASAR!! USUARIO {}, {}", user.getDni(), LocalDateTime.now().format(Util.formatDateTime));

                            EventCode eventCode = (accessType == 1) ? EventCode.ACCESS_DENY_BY_APB_IN_SCANNER : EventCode.ACCESS_DENY_BY_APB_OUT_SCANNER;
                            eventData = new EventData(ldt, eventCode.getCode(), user.getDni(), deviceId, accessType, locationId);

                        } else {

                            // CONCEDER ACCESO : USUARIO HABILITADO ..
                            log.info("CONCEDER ACCESO: USUARIO {}, {}", user.getDni(), LocalDateTime.now().format(Util.formatDateTime));

                            if (accessType == 1) gpioService.activateRelayIN(); else gpioService.activateRelayOUT();
                            EventCode eventCode = EventCode.ACCESS_GRANTED_BY_SCANNER;
                            eventData = new EventData(ldt, eventCode.getCode(), user.getDni(), deviceId, accessType, locationId);

                            // actualizar antipassback !! ..
                            antiPassback.setDateTime(ldt);
                            antiPassback.setUserStatus(accessType);
                            antiPassback.setSync(0);
                            antiPassbackRepo.save(antiPassback);

                            // distribuir estado de antipassback a dispositivos de la localidad !!
                            antiPassbackService.distributeAPB(antiPassback);

                        }
                    } // fin antiPassback == null ..


                } else {

                    // CONCEDER ACCESO : USUARIO HABILITADO ..
                    log.info("CONCEDER ACCESO: USUARIO {}, {}", user.getDni(), LocalDateTime.now().format(Util.formatDateTime));

                    if (accessType == 1) gpioService.activateRelayIN(); else gpioService.activateRelayOUT();

                    EventCode eventCode = EventCode.ACCESS_GRANTED_BY_SCANNER;

                    if (!Util.CONNECT_LOCAL_STATUS.get())
                        eventCode = EventCode.ACCESS_GRANTED_BY_CONNECT_FAIL;

                    eventData = new EventData(ldt, eventCode.getCode(), user.getDni(), deviceId, accessType, locationId);

                    if ( user.getAntiPassback() ) {
                        antiPassback = antiPassbackRepo.findByUserId(user.getDni()).orElse(null);
                        if (antiPassback == null) {
                            antiPassback = new AntiPassback(user.getDni(), accessType);
                            antiPassback.setSync(0);
                            antiPassbackRepo.save(antiPassback);
                        } else {
                            antiPassback.setDateTime(ldt);
                            antiPassback.setUserStatus(accessType);
                            antiPassback.setSync(0);
                            antiPassbackRepo.save(antiPassback);
                        }
                    }


                } // fin user.getAntiPassback ..


            } else {


                // REQUERIMIENTO DE CLIENTE DEFINE ACCESO SOLO PARA USUARIOS HABILIDADOS ..
                // DENEGAR ACCESO ..
                log.info(LocalDateTime.now().format(Util.formatDateTime) + " USUARIO DESHABILITADO !! NO PUEDE PASAR !! ");

                EventCode eventCode = EventCode.ACCESS_DENY_BY_SCANNER;
                eventData = new EventData(ldt, eventCode.getCode(), user.getDni(), deviceId, accessType, locationId);

            }

        } else { // fin user == null ..

            // DENEGAR ACCESO
            log.info(LocalDateTime.now().format(Util.formatDateTime) + " USUARIO NO REGISTRADO !! NO PUEDE PASAR !! ");
            EventCode eventCode = EventCode.ACCESS_DENY_BY_SCANNER;
            eventData = new EventData(ldt, eventCode.getCode(), dni, deviceId, accessType, locationId);
        }

        if (eventData != null)
            eventDataRepo.save(eventData);


        if (user != null &&  eventData != null && device != null) {

            SyncStatus syncStatus = syncStatusRepo.findByUserId(user.getDni()).orElse(null);
            if (syncStatus == null) {
                syncStatus = new SyncStatus(user, eventData, antiPassback, device);
            } else {
                SyncStatus aux = new SyncStatus(user, eventData, antiPassback, device);
                syncStatus.setDeviceId( aux.getDeviceId() );
                syncStatus.setDateSync( aux.getDateSync() );
                syncStatus.setUserSync( aux.getUserSync() );
                syncStatus.setEventSync ( aux.getEventSync() );
                syncStatus.setDeviceSync( aux.getDeviceSync() );
                syncStatus.setSync(0);
            }

            syncStatusRepo.save(syncStatus);

        }

    }


    public List<EventData> fetchByUserId(String userId, Integer page) {

        if (page == null) return new ArrayList<>();
        if (page < 0) return new ArrayList<>();
        page = page - 1;
        Pageable dataPage = PageRequest.of(page, 10);
        return eventDataRepo.findByUserId(userId, dataPage).orElse(new ArrayList<>());
    }


    public List<EventData> fetchEventData(LocalDateTime from, LocalDateTime to, Integer page) {

        if (page == null) return new ArrayList<>();
        if (page < 0) return new ArrayList<>();
        page = page - 1;
        Pageable dataPage = PageRequest.of(page, 10, Sort.by("dateTime").descending());
        return eventDataRepo.findByDateTimeBetween(from, to, dataPage).orElse(new ArrayList<>());

    }


    @Async
    @Scheduled (fixedDelay = 10100, initialDelay = 10100)
    public void synchronizeEventData() throws Exception {

        if (SYNC_INFO_EVENTDATA.equals("0")) return;

        final int sync = 0;
        int fromCod = 200;
        int toCod = 499;

        Pageable dataPage = PageRequest.of(0, 100, Sort.by("dateTime"));
        Page<EventData> page = eventDataRepo.findBySyncAndEventCodeBetween ( sync, fromCod, toCod, dataPage );

        if (page.getTotalElements() == 0) return;

        log.info("synchronize eventdata, registers: {}", page.getTotalElements() );

        for (int i=0; i<= page.getTotalPages(); i++) {

            List<EventData> dataList = page.getContent();
            sendEventDataEndPoint(dataList);
            Util.sleep(2);

            if (!page.hasNext()) break;
            page.nextPageable();
        }

    }


    private void sendEventDataEndPoint(List<EventData> eventList) {

        try {

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("customer_id", customerId);
            headers.set("customer_key", customerKey);

            RestTemplate restTemplate = new RestTemplate();
            String url = URL_WS + "/eventdata/location/"+locationId;

            HttpEntity<List<EventData>> payload = new HttpEntity<>(eventList, headers);

            ResponseEntity<List<EventData>> response = restTemplate.exchange(url, HttpMethod.POST, payload, new ParameterizedTypeReference< List<EventData>>(){});

            if ( response.getStatusCode() == HttpStatus.OK ) {
                List<EventData> dataList = response.getBody();

                for (EventData data : dataList) {

                    String userId = data.getUserId().trim();
                    String deviceId = data.getDeviceId().toString().trim();
                    String eventDate = data.getDateTime().format(Util.formatDateTime).trim();

                    EventData eventData = eventList.stream()
                            .filter( e -> e.getUserId().trim().equals( userId )
                                       && e.getDeviceId().toString().trim().equals( deviceId )
                                       && e.getDateTime().format(Util.formatDateTime).equals( eventDate ) )
                            .findAny().orElse(null);

                    if (eventData != null) {
                        eventData.setSync(1);
                        eventDataRepo.save(eventData);
                    }

                }


            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    @Async
    @Scheduled( fixedDelay = 10100, initialDelay = 10100)
    public void synchronizeUserStatus() throws Exception {

        //System.out.println("SYNC_INFO_USER_STATUS -> " + SYNC_INFO_USER_STATUS);

        if (SYNC_INFO_USER_STATUS.equals("0")) return;

        String today = LocalDate.now().format(Util.formatDate) + " 00:00:00";
        LocalDateTime fromDate = LocalDateTime.parse( today, Util.formatDateTime);
        LocalDateTime toDate  = LocalDateTime.now();
        int sync = 0;

        Pageable pageable = PageRequest.of(0, 100, Sort.by("dateSync"));
        Page<SyncStatus> page = syncStatusRepo.findBySyncAndDateSyncBetween(sync, fromDate, toDate, pageable);

        if (page.getTotalElements() == 0) return;

        log.info("synchronizr user status, registers: ", page.getTotalElements() );

        for (int i=0; i<= page.getTotalPages(); i++) {

            List<SyncStatus> dataList = page.getContent();
            this.sendUserStatusEndPoint(dataList);
            Util.sleep(2);

            if (!page.hasNext()) break;
            page.nextPageable();
        }


    }




    private void sendUserStatusEndPoint(List<SyncStatus> statusList) {

        try {

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("customer_id", customerId);
            headers.set("customer_key", customerKey);

            RestTemplate restTemplate = new RestTemplate();
            String url = URL_WS + "/sync/status/users/location/"+locationId+"/device/"+deviceId;

            HttpEntity<List<SyncStatus>> payload = new HttpEntity<>( statusList, headers );

            ResponseEntity<List<SyncStatus>> response = restTemplate.exchange(url, HttpMethod.POST, payload, new ParameterizedTypeReference< List<SyncStatus>>(){});

            log.info("apirest: " + url + " -> response code: " + response.getStatusCode());

            if ( response.getStatusCode() == HttpStatus.OK ) {

                List<SyncStatus> dataList = response.getBody();

                for (SyncStatus data : dataList) {

                    String userId = data.getUserId().trim();

                    SyncStatus userStatus = statusList.stream()
                            .filter( e -> e.getUserId().trim().equals( userId ))
                            .findAny().orElse(null);

                    if (userStatus != null) {
                        userStatus.setSync(1);
                        syncStatusRepo.save(userStatus);
                    }
                }

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }







    /// ////////////////////////////////////////////////////////////////////////////
}
