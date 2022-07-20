package cl.architeq.acc.service;

import cl.architeq.acc.model.*;
import cl.architeq.acc.repository.CompanyRepository;
import cl.architeq.acc.repository.DeviceRepository;
import cl.architeq.acc.repository.LocationRepository;
import cl.architeq.acc.repository.UserRepository;
import cl.architeq.acc.util.SyncDeviceStatus;
import cl.architeq.acc.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.HttpHostConnectException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.ParameterizedTypeReference;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
public class DeviceService {


    @Autowired
    private Environment env;

    @Autowired
    private DeviceRepository deviceRepo;

    @Autowired
    private LocationRepository locationRepo;

    @Autowired
    private CompanyRepository companyRepo;

    @Autowired
    private UserRepository userRepo;


    private static String ipAddr;
    private static String macAddr;
    private static String deviceId;
    private static String deviceName;
    private static String deviceModel;
    private static String locationId;
    private static String companyId;

    private static String URL_WS;
    private static String customerId;
    private static String customerKey;

    private static String SYNC_NOTIFY_STATUS_SERVICE;

    private static String SYNC_INFO_SERVICE;

    @PostConstruct
    private void init() throws Exception {

        URL_WS = env.getProperty("url.endpoint.webservice").trim();
        customerId = env.getProperty("customer.id").trim();
        customerKey = env.getProperty("customer.key").trim();

        ipAddr = env.getProperty("conf.ip.addr").trim();
        macAddr = env.getProperty("conf.mac.addr").trim().toUpperCase();
        deviceId = env.getProperty("conf.device.id").trim();
        deviceName = env.getProperty("conf.device.name").trim();
        deviceModel = env.getProperty("conf.device.model").trim();
        locationId = env.getProperty("conf.location.id").trim();
        companyId = env.getProperty("conf.company.id").trim();

        SYNC_INFO_SERVICE = env.getProperty("conf.sync.info.device");
        SYNC_NOTIFY_STATUS_SERVICE = env.getProperty("conf.sync.notify.status.device");

        Company com = companyRepo.findByCod("10").orElse(null);
        if (com == null) {
            com = new Company( companyId, "EMPRESA-TEST", "EMPRESA-TEST");
            companyRepo.save(com);
        }

        Location loc = locationRepo.findByCod(locationId).orElse(null);
        if (loc == null) {
            loc = new Location(locationId, "SUCURSAL-TEST", com,"SUCURSAL-TEST");
            locationRepo.save(loc);
        }

        String ip_addr = ( Util.getIpAddr().contains("127.0") ) ? ipAddr : Util.getIpAddr();
        String mac_addr = ( Util.getMacAddr().trim().isEmpty() ) ? macAddr : Util.getMacAddr();

        Device device = deviceRepo.findByCod(Integer.parseInt(deviceId)).orElse(null);
        if (device == null) {
            device = new Device( Integer.parseInt(deviceId), deviceName, deviceModel, ip_addr, mac_addr, loc );
        } else {
            device.setIpAddr( ip_addr );
            device.setMacAddr( mac_addr );
            device.setEnabled( true );
        }
        deviceRepo.save(device);

    }


    public Device saveDevice(Device dev, String locationCod) {

        if (locationCod == null) return null;
        if (dev.getMacAddr() == null) return null;


        Location location = locationRepo.findByCod(locationCod).orElse( null);
        if (location == null) return null;

        String macAddr = dev.getMacAddr().trim().toUpperCase();

        Device device = deviceRepo.findByCod(dev.getCod()).orElse(null);

        if (device == null) {

            Device macCheck = deviceRepo.findByMacAddr( macAddr ).orElse(null);
            if (macCheck != null) return null;

            device = new Device( dev.getCod(), dev.getName(), dev.getModel(), dev.getIpAddr(), macAddr, location );

            if (dev.getTcpPortService() == null) device.setTcpPortService(0);
            if (dev.getTcpPortService() != null) device.setTcpPortService( dev.getTcpPortService() );

            if (dev.getModel() != null) device.setModel( dev.getModel() );
            if (dev.getIpAddrWLAN() != null) device.setIpAddrWLAN( dev.getIpAddrWLAN() );
            if (dev.getMacAddrWLAN() != null) device.setMacAddrWLAN( dev.getMacAddrWLAN() );

        } else {

            Device macCheck = deviceRepo.findByMacAddr( macAddr ).orElse(null);
            if (macCheck != null)
                if (macCheck.getId() != device.getId())
                    return null;

            device.setName( dev.getName() );
            device.setIpAddr( dev.getIpAddr() );
            device.setMacAddr( macAddr );

            if (dev.getEnabled() == null) dev.setEnabled(true);
            if (dev.getEnabled() != null) dev.setEnabled(dev.getEnabled());

            if (dev.getTcpPortService() == null) dev.setTcpPortService(0);
            if (dev.getModel() == null) dev.setModel("");
            if (dev.getIpAddrWLAN() == null) dev.setIpAddrWLAN("");
            if (dev.getMacAddrWLAN() == null) dev.setMacAddrWLAN("");


            device.setIpAddrWLAN( dev.getIpAddrWLAN() );
            device.setMacAddrWLAN( dev.getMacAddrWLAN() );
            device.setModel( dev.getModel() );

            device.setTcpPortService( dev.getTcpPortService() );

            device.setLocation(location);
            device.setUpdated(LocalDateTime.now());

        }

        deviceRepo.save(device);

        return device;
    }


    public List<Device> fetchDevices() {
        List<Device> list = (List<Device>) this.deviceRepo.findAll();
        if (list == null) list = new ArrayList<>();
        return list;
    }


    public Device fetchDevice(Integer cod) {
        if (cod == null) return null;
        return this.deviceRepo.findByCod(cod).orElse(null);
    }


    public List<Device> fetchDevicesByLocation(String locationCod, Integer page) {

        if (page == null) return new ArrayList<>();
        if (page < 0) return new ArrayList<>();

        if (locationCod == null) return new ArrayList<>();
        Location location = locationRepo.findByCod(locationCod).orElse(null);
        if (location == null) return new ArrayList<>();

        page = page-1;
        Pageable dataPage = PageRequest.of(page, 10, Sort.by("name"));
        return this.deviceRepo.findByLocation(location, dataPage).orElse( new ArrayList<>());

    }


    @Scheduled(fixedDelay = 10100, initialDelay = 10100)
    public void registerDevice() {

        if (SYNC_INFO_SERVICE.equals("0")) return;

        //System.out.println( "* SYNC_DEVICE_STATUS -> " + Util.SYNC_DEVICE_STATUS + " !!");
        if (Util.SYNC_DEVICE_STATUS) return;

        try {
            Device device = deviceRepo.findByCod(Integer.parseInt(deviceId)).orElse(null);
            if (device == null) return;

            Util.SYNC_DEVICE_STATUS = fetchDeviceApiRest(device);

            if (!Util.SYNC_DEVICE_STATUS) {
                registerDeviceApiRest(device);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private boolean fetchDeviceApiRest(Device device)  {

        boolean bln = false;

        try {

            if (device == null) return false;

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("customer_id", customerId);
            headers.set("customer_key", customerKey);

            RestTemplate restTemplate = new RestTemplate();

            String url = URL_WS + "/devices?cod=" + device.getCod();

            HttpEntity<String> params = new HttpEntity<>("parameters", headers);
            //HttpEntity<Device> params = new HttpEntity<>( null, headers);

            ResponseEntity<Device> response = restTemplate.exchange(url, HttpMethod.GET, params, Device.class);

            //ResponseEntity response = restTemplate.getForEntity( url, Device.class, params );

            if (response != null) {
                if ( response.getStatusCode() == HttpStatus.OK ) {
                    bln = device.equals( response.getBody() );
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return bln;

    }


    private void registerDeviceApiRest(Device device)  {


        String url = "";

        try {

            if (device != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("customer_id", customerId);
                headers.set("customer_key", customerKey);

                RestTemplate restTemplate = new RestTemplate();
                url = URL_WS + "/devices?locationCod="+locationId;

                HttpEntity<Device> params = new HttpEntity<>(device, headers);
                ResponseEntity<Device> response = restTemplate.exchange(url, HttpMethod.POST, params, Device.class);

                if (response != null) {
                    if (response.getStatusCode() == HttpStatus.OK) {
                        Util.SYNC_DEVICE_STATUS = device.equals(response.getBody());
                    }
                }
            }

        } catch (Exception ex) {

            if (ex instanceof HttpHostConnectException || ex.getCause() instanceof ConnectException) {
                log.error("!!! FALLA CONEXION ENDPOINT REGISTRO DISPOSITIVO -> URL: " + url + " ERROR ： " + ex.getMessage());
            } else {
                ex.printStackTrace();
            }

        }
    }


    @Async
    @Scheduled(fixedDelay = 60100, initialDelay = 15100)
    public void notifySyncStatusDevice() {

        if (SYNC_NOTIFY_STATUS_SERVICE.equals("0")) return;
        if (!Util.SYNC_DEVICE_STATUS) return;

        log.info( "notificar estado de sincronizacion de dispositivo !! " );
        String url = "";

        try {

            int enabled = userRepo.countEnabled(true);
            int disabled = userRepo.countEnabled(false);
            LocalDateTime ldt = LocalDateTime.now();

            JSONObject status = new JSONObject();

            status.put("deviceId", Integer.parseInt(deviceId));
            status.put("deviceName", deviceName);
            status.put("totalUsersEnabled", enabled);
            status.put("totalUsersDisabled", disabled);
            status.put("sync", true);
            status.put("dateSync", ldt.format(Util.formatDateTime));

            SyncDeviceStatus deviceStatus = new SyncDeviceStatus(Integer.parseInt(deviceId));
            deviceStatus.setDateSync(ldt);
            deviceStatus.setStatusSync(status.toString());


            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("customer_id", customerId);
            headers.set("customer_key", customerKey);

            RestTemplate restTemplate = new RestTemplate();
            url = URL_WS + "/sync/status/devices/location/"+locationId+"/device/"+deviceId;

            HttpEntity<SyncDeviceStatus> payload = new HttpEntity<>( deviceStatus, headers );

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, payload, String.class);

            if ( response.getStatusCode() == HttpStatus.OK ){
                log.info("apirest: " + url + " -> response code: " + response.getStatusCode());
            }

        } catch (Exception ex) {

            if (ex instanceof HttpHostConnectException || ex.getCause() instanceof ConnectException) {
                log.error("!!! FALLA CONEXION ENDPOINT NOTIFICACION ESTADO DISPOSITIVO -> URL: " + url + " ERROR ： " + ex.getMessage());
            } else {
                ex.printStackTrace();
            }

        }


    }


    //@Async
    @Scheduled(fixedDelay = 10100, initialDelay = 5100)
    public void checkLocalConnect() {

        try {

            List<Device> deviceList = deviceRepo.findByCodNotAndEnabled( Integer.parseInt(deviceId), true ).orElse( new ArrayList<>() );

            int cnt = deviceList.size();

            log.info("DEVICE-ID -> " + deviceId + " - deviceList.size() -> " + cnt +  " - DeviceService.checkLocalConnect ..");

            if (cnt == 0) {
                Util.CONNECT_LOCAL_STATUS.compareAndSet(false,true);
                log.info("check local connect : CONNECT_LOCAL_STATUS -> " + Util.CONNECT_LOCAL_STATUS);
                return;
            } else {
                Util.CONNECT_LOCAL_STATUS.compareAndSet(false, true);
            }


            // FUNCION PING CON PROBLEMAS !!..

            int connected = 0;

            for (Device device : deviceList) {

                //boolean ping = Util.pingIpAddr(device.getIpAddr());
                //System.out.println("PING A " + device.getIpAddr() + " -> PING " + ping);

                boolean resource = checkLocalResource(device);
                if (resource) {
                    connected++;
                }

            }

            if ( connected == cnt ) {
                Util.CONNECT_LOCAL_STATUS.compareAndSet(false,true);
            } else {
                Util.CONNECT_LOCAL_STATUS.compareAndSet(true,false);
            }

            log.info("FLAG CONEXION RED LOCAL: CONNECT_LOCAL_STATUS -> {}", Util.CONNECT_LOCAL_STATUS);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private boolean checkLocalResource(Device device) {

        boolean bln = false;
        String url = "";
        try {

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            RestTemplate restTemplate = new RestTemplate();
            url = "http://" + device.getIpAddr() + ":" + device.getTcpPortService() + "/ws/v1/devices/?cod="+deviceId;

            HttpEntity<Void> payload = new HttpEntity<>( headers );
            ResponseEntity<Device> response = restTemplate.exchange(url, HttpMethod.GET, payload, Device.class);

            log.info("SERVICIO SINCRONIZACION LOCAL DISPOSITIVOS: " + url + " ->  RESPONSE CODE: " + response.getStatusCode());

            if ( response.getStatusCode() == HttpStatus.OK ) {

                Device dev = response.getBody();
                System.out.println(dev.toString());
                if ( dev.getEnabled() ) {
                    bln = true;
                    //System.out.println("DISPOSITIVO " + dev.getCod() + " : ENABLED -> " + dev.getEnabled());
                }
            }

        } catch (Exception ex) {

            if (ex instanceof HttpHostConnectException || ex.getCause() instanceof ConnectException) {
                log.error("!!! FALLA CONEXION ENDPOINT LOCAL SINCRONIZACION DISPOSITIVO -> URL: " + url + " ERROR ： " + ex.getMessage());
            } else {
                ex.printStackTrace();
            }
        }

        return bln;
    }



}
