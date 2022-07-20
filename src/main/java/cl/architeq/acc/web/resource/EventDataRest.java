package cl.architeq.acc.web.resource;

import cl.architeq.acc.model.AntiPassback;
import cl.architeq.acc.model.EventData;
import cl.architeq.acc.service.EventDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("ws/v1")
public class EventDataRest {

    @Autowired
    private EventDataService eventDataService;


    @GetMapping(path = "/eventdata/page/{page}", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<List<EventData>> fetchDataAntiPassback(@PathVariable(value = "page") Integer page,
                                                                 @RequestParam(value="from", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime from,
                                                                 @RequestParam(value="to", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime to) {

        LocalDateTime fromDate = LocalDateTime.now().minusDays(1L).minusHours( LocalDateTime.now().getHour() ).minusMinutes( LocalDateTime.now().getMinute() );
        LocalDateTime toDate = LocalDateTime.now();
        if (from != null && to != null) {
            fromDate = from;
            toDate = to;
        }

        List<EventData> dataList = eventDataService.fetchEventData(fromDate, toDate, page);
        if (dataList.size() > 0) {
            return new ResponseEntity<>(dataList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(dataList, HttpStatus.NO_CONTENT);
        }

    }


}
