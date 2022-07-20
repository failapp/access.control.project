package cl.architeq.acc;


import cl.architeq.acc.model.EventData;
import cl.architeq.acc.repository.EventDataRepository;
import cl.architeq.acc.util.Util;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EventDataRepositoryTest {

    @Autowired
    private EventDataRepository eventDataRepo;

    @Test
    public void contextLoads() {
        assertThat(true).isEqualTo(true);
    }

    //@Test
    public void findByDateBetween() {

        Pageable pageable = PageRequest.of(0, 100);

        LocalDateTime from = LocalDateTime.now().minusDays(1);
        LocalDateTime to = LocalDateTime.now().plusDays(1);

        List<EventData> dataList = eventDataRepo.findByDateTimeBetween(from, to, pageable).orElse(new ArrayList<>());

        System.out.println("from -> " + from.format(Util.formatDateTime));
        System.out.println("to -> "   + to.format(Util.formatDateTime));
        System.out.println("dataList.size() -> " + dataList.size());

        assertThat(dataList.size()).isGreaterThanOrEqualTo(1);

    }





}
