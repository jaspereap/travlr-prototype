package nus.iss.travlr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.RedisTemplate;

import nus.iss.travlr.repository.ActivityRepository;
import nus.iss.travlr.repository.ItineraryRepository;
import nus.iss.travlr.repository.UserRepository;
import nus.iss.travlr.service.ItineraryService;

@SpringBootApplication
public class TravlrApplication implements CommandLineRunner {

	@Autowired @Qualifier("redisTemplate")
	RedisTemplate<String, String> template;
	@Value("${spring.redis.host}")
	String redisHost;
	@Value("${spring.redis.port}")
	String redisPort;
	@Value("${spring.redis.username}")
	String redisUsername;
	@Value("${spring.redis.password}")
	String redisPassword;
    @Value("${google.api.key}")
    private String API_KEY;
    @Value("${geocode.url}")
    private String GEOCODE_URL;

	@Autowired
	UserRepository userRepo;

	@Autowired
	ItineraryService itiSvc;
	@Autowired
	ItineraryRepository itiRepo;
	@Autowired
	ActivityRepository actRepo;

	public static void main(String[] args) {
		SpringApplication.run(TravlrApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("\tApplication Started");
		System.out.println("\tRedis running on " + redisHost + ":" + redisPort);
		System.out.println("\tRedis credential: " + redisUsername + ":" + redisPassword);
		System.out.println("\tAPI_KEY: " + API_KEY);
		System.out.println("\tGEOCODE_URL: " + GEOCODE_URL);
	}

}
