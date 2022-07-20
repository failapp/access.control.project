package cl.architeq.acc.service;

import cl.architeq.acc.model.*;
import cl.architeq.acc.repository.*;
import cl.architeq.acc.util.DeviceSync;
import cl.architeq.acc.util.LocalSync;
import cl.architeq.acc.util.MessageDetails;
import cl.architeq.acc.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.HttpHostConnectException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import org.springframework.web.client.RestTemplate;


import javax.annotation.PostConstruct;
import java.net.ConnectException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Future;

@Slf4j
@Service
public class UserService {

    @Autowired
    private Environment env;

    @Autowired
    private SyncUserDeviceRepository syncUserDeviceRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private CompanyRepository companyRepo;

    @Autowired
    private LocationRepository locationRepo;

    @Autowired
    private DeviceRepository deviceRepo;


    private static String URL_WS;
    private static String customerId;
    private static String customerKey;
    private static Integer DEVICE_ID;

    private static List<LocalSync> localSyncList = Collections.synchronizedList( new ArrayList<>() );

    @PostConstruct
    private void init() throws Exception {
        URL_WS = env.getProperty("url.endpoint.webservice").trim();
        customerId = env.getProperty("customer.id").trim();
        customerKey = env.getProperty("customer.key").trim();
        DEVICE_ID = Integer.parseInt(env.getProperty("conf.device.id").trim());
    }


    public User saveUser(User u) {
        User user = this._checkUserRegister(u);
        if (user != null) {
            user = userRepo.save(user);
        }
        return user;
    }



    private User _checkUserRegister(User u) {

        User user = null;
        try {

            user = userRepo.findByDni(u.getDni()).orElse(null);

            if (user == null) {

                if (u.getDni() == null) return null;
                if (u.getDni().trim().isEmpty()) return null;

                if (u.getFirstName() == null) return null;
                if (u.getFirstName().trim().isEmpty()) return null;

                String dni = u.getDni().trim().replace(".","");
                u.setDni(dni.trim().toUpperCase());

                if (u.getLastName() == null) u.setLastName("");
                if (u.getValidity() == null) u.setValidity(LocalDate.now().plusYears(30));
                if (u.getEnabled() == null) u.setEnabled(true);
                if (u.getAntiPassback() == null) u.setAntiPassback(true);

                user = new User();

                user.setDni(u.getDni().trim());
                user.setFirstName(u.getFirstName().trim());
                user.setLastName(u.getLastName().trim());
                user.setEnabled(u.getEnabled());
                user.setValidity(u.getValidity());
                user.setAntiPassback(u.getAntiPassback());

            } else {

                if (u.getFirstName() == null) return null;
                if (u.getFirstName().trim().isEmpty()) return null;

                if (u.getLastName() == null) u.setLastName("");
                if (u.getValidity() == null) u.setValidity(LocalDate.now().plusYears(30));
                if (u.getValidity().toString().trim().isEmpty()) u.setValidity(LocalDate.now().plusYears(30));

                if (u.getEnabled() == null) u.setEnabled(true);
                if (u.getAntiPassback() == null) u.setAntiPassback(true);

                user.setFirstName(u.getFirstName().trim());
                user.setLastName(u.getLastName().trim());

                user.setEnabled(u.getEnabled());
                user.setAntiPassback(u.getAntiPassback());
                user.setValidity(u.getValidity());
                user.setUpdated(LocalDateTime.now());

            }

            if (u.getType() == null) user.setType("user");
            if (u.getProximity() == null) user.setProximity("");

            if (u.getProximity() != null) user.setProximity(u.getProximity());
            if (u.getType() != null) {
                if (u.getType().trim().toLowerCase().equals("user")
                        || u.getType().trim().toLowerCase().equals("admin")
                        || u.getType().trim().toLowerCase().equals("visitor")) {

                    user.setType( u.getType());
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return user;

    }



    public void saveSyncUserDevice(SyncUserDevice sync) {

        try {

            if ( !DEVICE_ID.toString().trim().equals(sync.getDeviceCod().toString().trim())) {

                /*
                // nutrir lista de sicronizacion de usuarios en red local ..
                User user = userRepo.findByDni( sync.getUserDni() ).orElse(null);
                Device device = deviceRepo.findByCod( sync.getDeviceCod() ).orElse( null);
                if (user != null && device != null) {

                    localSyncList.removeIf( x -> x.getUser().getDni().equals(user.getDni())
                            && x.getDevice().getCod().equals(device.getCod()) );

                    LocalSync localSync = new LocalSync(user, device, false );
                    localSyncList.add(localSync);
                }
                */
                return;
            }


            SyncUserDevice syncUserDevice = syncUserDeviceRepo.findByUserDniAndDeviceCod( sync.getUserDni(), sync.getDeviceCod()).orElse(null);
            if (syncUserDevice == null) {
                log.info("CREAR REGISTRO SINCRONIZACION !!" );
                syncUserDevice = new SyncUserDevice( sync.getUserDni(), sync.getDeviceCod(), sync.getAction() );
            } else {
                log.info("ACTUALIZAR REGISTRO SINCRONIZACION !!" );
                syncUserDevice.setDateRequest ( sync.getDateRequest() );
                syncUserDevice.setDateSync ( LocalDateTime.now().format ( Util.formatDateIDTi) );
                syncUserDevice.setAck(sync.getAck());
            }
            syncUserDeviceRepo.save(syncUserDevice);


        } catch ( Exception ex ) {
            ex.printStackTrace();
        }

    }


    @Async
    public Future<SyncUserDevice> synchronizeUser(SyncUserDevice sync) {

        String url = "";
        try {

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("customer_id", customerId);
            headers.set("customer_key", customerKey);

            RestTemplate restTemplate = new RestTemplate();

            url = URL_WS + "/users?dni=" + sync.getUserDni().trim();

            HttpEntity<String> params = new HttpEntity<>("parameters", headers);

            ResponseEntity<User> response = restTemplate.exchange(url, HttpMethod.GET, params, User.class);

            User user = null;
            if ( response.getStatusCode() == HttpStatus.OK ) {
                user = response.getBody();
                user = this.saveUser(user);
            }

            if (user != null) {

                sync.setAck(1);
                sync.setDateSync(LocalDateTime.now().format(Util.formatDateTimeIDTi));
                syncUserDeviceRepo.save(sync);

                return new AsyncResult<>(sync);
            }

        } catch (Exception ex) {

            if (ex instanceof HttpHostConnectException || ex.getCause() instanceof ConnectException) {
                log.error("!!! FALLA CONEXION ENDPOINT 'synchronizeUser' -> URL:" + url + " ERROR ： " + ex.getMessage());
            } else {
                ex.printStackTrace();
            }

        }

        return null;

    }



    public User fetchUser(String userId) {
        return userRepo.findByDni(userId).orElse(null);
    }



    public List<User> fetchUsers(Integer page) {

        if (page == null) return new ArrayList<>();
        if (page < 0) return new ArrayList<>();

        page = page - 1;
        Pageable dataPage = PageRequest.of(page, 10);
        return userRepo.findAllByEnabled(true, dataPage).orElse(new ArrayList<>());

    }



    /// /////////////////////////////////
    // @Async
    // @Scheduled(fixedDelay = 5000, initialDelay = 5000)
    public void synchronizeUserInfo() throws Exception {

        if (!Util.CONNECT_LOCAL_STATUS.get()) return;

        long count = localSyncList.stream().filter( x -> !x.isAck() ).count();
        log.info("COUNT MAP SYNCHRONIZATION USERS -> " + count);
        if (count == 0) return;

        for (LocalSync localSync : localSyncList) {
            boolean success = this.sendEndPointUser(localSync.getUser(), localSync.getDevice());
            localSync.setAck(success);
        }

        log.info("LISTA DE SINCRONIZACION DE USUARIOS - RED LOCAL - CANTIDAD: {}",localSyncList.size() );
        List<LocalSync> toRemove = new ArrayList<>();
        for (LocalSync sync : localSyncList) {
            if (sync.isAck()) {
                toRemove.add(sync);
            }
        }
        localSyncList.removeAll(toRemove);
        log.info("LISTA DE SINCRONIZACION DE USUARIOS - RED LOCAL - CANTIDAD: {}", localSyncList.size() );

    }



    private boolean sendEndPointUser(User user, Device device) {

        if (!Util.CONNECT_LOCAL_STATUS.get()) return false;
        boolean bln = false;
        String urlGet = "";

        try {

            if (device.getTcpPortService() == null || device.getTcpPortService() <= 0) return false;
            /*
            if (! Util.pingIpAddr( device.getIpAddr() )) {
                System.out.println("FALLA PING A HOST " + device.getIpAddr());
                return false;
            }
            */
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            RestTemplate restTemplate = new RestTemplate();

            urlGet = "http://" + device.getIpAddr() + ":" + device.getTcpPortService() + "/ws/v1/users/?userId=" + user.getDni();

            log.info("URL ENDPOINT SYNC USER LOCAL: GET METHOD {}", urlGet );

            HttpEntity<Void> payload = new HttpEntity<>(headers);
            ResponseEntity<User> responseGet = restTemplate.exchange(urlGet, HttpMethod.GET, payload, User.class);

            if (responseGet.getStatusCode() == HttpStatus.OK) {

                User u = responseGet.getBody();
                System.out.println("user -> " + user.toString());
                System.out.println("user -> " + u.toString());
                return true;

                /*
                if (user.getEnabled().equals(  responseGet.getBody().getEnabled() )) {
                    return true;
                }
                 */
            }

            /*
            String postUrl = "http://" + device.getIpAddr() + ":" + device.getTcpPortService() + "/ws/v1/users";
            System.out.println("URL ENDPOINT SYNC USER LOCAL: POST METHOD " + urlGet );
            ResponseEntity<User> responsePost = restTemplate.exchange(postUrl, HttpMethod.POST, payload, User.class);
            System.out.println("ENDPOINT USER LOCAL -> " + postUrl + " - HTTP CODE RESPONSE -> " + responsePost.getStatusCode());
            if (responsePost.getStatusCode() == HttpStatus.OK) {
                bln = true;
            }
            */

        } catch (Exception ex) {

            if (ex instanceof HttpHostConnectException || ex.getCause() instanceof ConnectException) {
                log.error("!!! FALLA CONEXION RED LOCAL - ENDPOINT SINCRONIZACION DE USUARIOS -> URL:" + urlGet + " ERROR ： " + ex.getMessage());
            } else {
                ex.printStackTrace();
            }

        }

        return false;
    }



    public int fetchCountSyncUsers(int deviceId) {

        int cnt = 0;
        String url = "";
        try {

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("customer_id", customerId);
            headers.set("customer_key", customerKey);

            RestTemplate restTemplate = new RestTemplate();

            url = URL_WS + "/users/sync/count/?deviceId=" + deviceId;

            HttpEntity<String> params = new HttpEntity<>("parameters", headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, params, String.class);

            if ( response.getStatusCode() == HttpStatus.OK ) {
                JSONObject json = new JSONObject(response.getBody());
                cnt = json.getInt("cantidad");
            }

        } catch (Exception ex) {

            if (ex instanceof HttpHostConnectException || ex.getCause() instanceof ConnectException) {
                log.error("!!! FALLA CONEXION ENDPOINT SINCRONIZACION DE USUARIOS -> URL:" + url + " ERROR ： " + ex.getMessage());
            } else {
                ex.printStackTrace();
            }

        }
        return cnt;
    }




    public Set<String> fetchSyncUsers(int page, int deviceId) {

        //List<User> userList = new ArrayList<>();
        Set<String> userSet = new HashSet<>();
        String url = "";
        try {

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("customer_id", customerId);
            headers.set("customer_key", customerKey);

            RestTemplate restTemplate = new RestTemplate();

            url = URL_WS + "/users/sync/page/" + page + "/?deviceId=" + deviceId;

            HttpEntity<String> params = new HttpEntity<>("parameters", headers);

            ResponseEntity<List<User>> response = restTemplate.exchange(url, HttpMethod.GET, params, new ParameterizedTypeReference<List<User>>(){} );

            if ( response.getStatusCode() == HttpStatus.OK ) {

                response.getBody().forEach( x -> {

                    System.out.println( x.toString());
                    User user = this.saveUser(x);
                    if (user != null) userSet.add(user.getDni().trim());

                });
            }

        } catch (Exception ex) {

            if (ex instanceof HttpHostConnectException || ex.getCause() instanceof ConnectException) {
                log.error("!!! FALLA CONEXION ENDPOINT SINCRONIZACION DE USUARIOS -> URL:" + url + " ERROR ： " + ex.getMessage());
            } else {
                ex.printStackTrace();
            }

        }

        return userSet;

    }



    public void sendSyncUsers(List<String> userIdList, int deviceId) {

        String url = "";

        try {

            HttpHeaders headers = new HttpHeaders();
            //headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("customer_id", customerId);
            headers.set("customer_key", customerKey);

            RestTemplate restTemplate = new RestTemplate();

            url = URL_WS + "/users/sync/?deviceId=" + deviceId;

            log.info("url -> {}", url);

            DeviceSync deviceSync = new DeviceSync(DEVICE_ID, "users", userIdList);

            HttpEntity<DeviceSync> payload = new HttpEntity<>(deviceSync, headers);

            ResponseEntity<MessageDetails> response = restTemplate.exchange(url, HttpMethod.POST, payload, MessageDetails.class);

            if ( response.getStatusCode() == HttpStatus.OK ) {
                log.info("response: message: {}, {}", response.getBody().getMessage(), response.getBody().getDetails());
            }

        } catch (Exception ex) {
            if (ex instanceof HttpHostConnectException || ex.getCause() instanceof ConnectException) {
                log.error("!!! FALLA CONEXION ENDPOINT SINCRONIZACION DE USUARIOS -> URL:" + url + " ERROR ： " + ex.getMessage());
            } else {
                ex.printStackTrace();
            }
        }

    }



}
