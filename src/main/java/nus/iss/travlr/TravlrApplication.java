package nus.iss.travlr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.RedisTemplate;

import nus.iss.travlr.repository.TravlrRepository;
import nus.iss.travlr.repository.UserRepository;
import nus.iss.travlr.service.TravlrService;

@SpringBootApplication
public class TravlrApplication implements CommandLineRunner {

	@Autowired @Qualifier("redisTemplate")
	RedisTemplate<String, String> template;

	@Autowired
	UserRepository userRepo;

	@Autowired
	TravlrService travSvc;
	@Autowired
	TravlrRepository travRepo;

	public static void main(String[] args) {
		SpringApplication.run(TravlrApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("\tApplication Started");
		// travSvc.getGeocode("causeway point");
	}

}
