package cl.architeq.acc.task;

import cl.architeq.acc.service.UserService;
import cl.architeq.acc.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class UserDataTask {

    @Autowired
    private Environment env;

    @Autowired
    private UserService userService;

    private static Integer DEVICE_ID;

    @PostConstruct
    private void init() {
        DEVICE_ID = Integer.parseInt(env.getProperty("conf.device.id").trim());
        //log.info("DEVICE_ID -> {}", DEVICE_ID);
    }


    @Async
    @Scheduled(fixedDelay = 60000, initialDelay = 5000)
    public void synchronizeEndpointCloud() {

        try {

            int cnt = userService.fetchCountSyncUsers(DEVICE_ID);
            log.info("cantidad de registros de usuarios para sincronizacion: {}", cnt);
            if (cnt == 0) return;

            int pages = 1;
            if (cnt > 100) {
                double p = (double)cnt/100;
                double dec = p % 1;
                double num = p - dec;
                pages = (int)num;
                if (dec > 0) pages++;
            }

            log.info("total records for synchronization: {} total pages: {}", cnt, pages);
            List<String> userIdList = new ArrayList<>();
            for (int i=1; i<=pages; i++) {
                Set<String> users = userService.fetchSyncUsers(i, DEVICE_ID);
                userIdList.addAll(users);
            }

            userService.sendSyncUsers(userIdList, DEVICE_ID);

            log.info("synchronization success.. users: {}", userIdList.size());

        } catch (Exception ex) {
            log.error("error synchronize users endpoint cloud.. {}", ex.getMessage());
        }


    }


}
