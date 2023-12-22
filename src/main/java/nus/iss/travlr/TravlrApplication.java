package nus.iss.travlr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.RedisTemplate;

import nus.iss.travlr.model.User;
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

		// User user = new User();
		// user.setUserId("1");
		// user.setPassword("lolol");
		// user.setUserName("jas");
		// System.out.println(user.toString());

		// System.out.println(userRepo.getUser("jas"));
	}

}
