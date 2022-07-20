package cl.architeq.acc;

import cl.architeq.acc.model.Auth;
import cl.architeq.acc.model.Role;
import cl.architeq.acc.repository.AuthRepository;
import cl.architeq.acc.repository.RoleRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AuthRepositoryTest {

    @Autowired
    private AuthRepository authRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Test
    public void contextLoads() {
        assertThat(true).isEqualTo(true);
    }

    //@Test
    public void fetchAuth() {

        Auth emailAuth = authRepo.findByEmail("admin@failapp.dev").orElse(null);
        System.out.println(emailAuth);
        assertThat(emailAuth).isNotNull();

        Auth usernameAuth = authRepo.findByUsername("dev").orElse(null);
        System.out.println(usernameAuth);
        assertThat(usernameAuth).isNotNull();

    }

    //@Test
    public void createAuth(){

        Role role = roleRepo.findById(2).orElse(null);
        System.out.println(role);
        assertThat(role).isNotNull();

        //user.setPassword(passwordEncoder.encode(accountDto.getPassword()));
        CharSequence charSequence = "1234";
        String encodePassword = BCrypt.hashpw(charSequence.toString(), BCrypt.gensalt(4));

        Auth auth = new Auth("walala", "walala@failapp.dev", encodePassword);
        auth.addRole(role);

        authRepo.save(auth);

        Auth emailAuth = authRepo.findByEmail("walala@failapp.dev").orElse(null);
        System.out.println(emailAuth);
        assertThat(emailAuth).isNotNull();

    }

    //@Test
    public void handlerPassword() {

        CharSequence charSequence = "1234";

        String encodePassword = BCrypt.hashpw(charSequence.toString(), BCrypt.gensalt(4));

        System.out.println(encodePassword);

        String dataBasePassword = "$2a$04$qqhhVa/bW.QvJTYuThmbguBzBf26JsAJJeyr4h6dAvTrggkEYPyey"; //1234 ..

        boolean match = BCrypt.checkpw(charSequence.toString(), dataBasePassword);

        assertThat(match).isTrue();
        //assertThat(match).isFalse();

    }



    //@Test
    public void findByRole(){

        Role role = roleRepo.findById(2).orElse(null);
        System.out.println(role);
        assertThat(role).isNotNull();

        List<Auth> authList = authRepo.findByRoles(role).orElse(new ArrayList<>());

        authList.forEach( x -> System.out.println(x));

        assertThat(authList.size()).isGreaterThanOrEqualTo(1);

    }


}
