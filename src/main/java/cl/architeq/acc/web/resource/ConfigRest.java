package cl.architeq.acc.web.resource;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("ws/v1")
public class ConfigRest {


    public ResponseEntity<Void> fetchConfig() {
        return null;
    }

    public ResponseEntity<Void> saveConfig() {
        return null;
    }

}
