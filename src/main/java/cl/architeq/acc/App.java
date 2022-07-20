package cl.architeq.acc;


import cl.architeq.acc.service.SerialPortService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


import javax.annotation.PreDestroy;

@Slf4j
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class App {

	@Autowired
	private SerialPortService serialPortService;


	public static void main(String[] args) {
		// ..
		SpringApplication.run(App.class, args);
	}

	@Bean
	CommandLineRunner runner() {

		return args ->  {

			log.info ("Servicio Control Acceso - versión 1.4.4 ..");

			//TimeZone tz = TimeZone.getDefault();

			/*
			TimeZone tz = TimeZone.getTimeZone(zonedDateTime.getZone());
			for (final String id : TimeZone.getAvailableIDs(tz.getRawOffset())) {
				System.out.println("id " + id);
			}
			*/

			this.serialPortService.handlerFlowIN();
			this.serialPortService.handlerFlowOUT();

		};
	}

	@PreDestroy
	private void preDestroy() {
		//this.userService.setCancelTask(true);
		log.info("service predestroy event ..");
	}


}
