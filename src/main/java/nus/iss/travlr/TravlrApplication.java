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

	

	public static void main(String[] args) {
		SpringApplication.run(TravlrApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("\tApplication Started");
		// Itinerary iti = new Itinerary("test", "malaysia", "lolol");
		// Activity act = new Activity("lol", LocalDateTime.now(), "www.test.com", "");
		// iti.add(act);
		// iti.add(act);
		// System.out.println(iti);
		// JsonArray jarr = iti.serializeActivity();
		// System.out.println("Serialized jarr: " + jarr);
		// System.out.println("Jsonobject to string: " + iti.toJsonObject());
	}

}
