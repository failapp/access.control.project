package cl.architeq.acc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccessControlApplicationTests {

	@Test
	public void contextLoads() {

		assertThat(true).isEqualTo(true);
	}

}
