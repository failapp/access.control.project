package cl.architeq.acc.web.resource;

import cl.architeq.acc.model.User;
import cl.architeq.acc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("ws/v1")
public class UserRest {

    @Autowired
    private UserService userService;


    @PostMapping(path = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> saveUser(@RequestBody User user) {

        User u = userService.saveUser(user);
        if (u != null)
            return new ResponseEntity<>(u, HttpStatus.OK);
        else
            return new ResponseEntity<>(new User(), HttpStatus.NO_CONTENT);

    }

    @GetMapping(path = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> fetchUser(@RequestParam(value = "userId") String userId) {

        User u = userService.fetchUser(userId);
        if (u != null)
            return new ResponseEntity<>(u, HttpStatus.OK);
        else
            return new ResponseEntity<>(new User(), HttpStatus.NO_CONTENT);
    }

    @GetMapping (path = "/users/page/{page}", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<List<User>>fetchUsers(@PathVariable(value = "page") Integer page) {

        List<User> userList = userService.fetchUsers(page);
        if (userList.size() > 0) {
            return new ResponseEntity<>(userList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(userList, HttpStatus.NO_CONTENT);
        }

    }

}
