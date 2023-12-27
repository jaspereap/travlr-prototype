package nus.iss.travlr;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.RedisTemplate;

import jakarta.json.JsonArray;
import nus.iss.travlr.model.Activity;
import nus.iss.travlr.model.Itinerary;
import nus.iss.travlr.repository.UserRepository;
import nus.iss.travlr.service.TravlrService;

// Travlr - Itinerary Planning App
// Key Functions:
// 1. Add destinations -> Google Maps Geocoding API to query destination names, return coordinates
// 2. Generate a map containing all the destinations

// Details
// 1. User login page
// 2. User's itineraries page
// 3. User's itinerary workspace page
// 4. View itinerary on a map -> Thymeleaf Javascript inlining


@SpringBootApplication
public class TravlrApplication implements CommandLineRunner {

	@Autowired @Qualifier("redisTemplate")
	RedisTemplate<String, String> template;

	@Autowired
	UserRepository userRepo;

	@Autowired
	TravlrService travSvc;
	

	public static void main(String[] args) {
		SpringApplication.run(TravlrApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("\tApplication Started");
		// travSvc.getGeocode("causeway point");
	}

}
