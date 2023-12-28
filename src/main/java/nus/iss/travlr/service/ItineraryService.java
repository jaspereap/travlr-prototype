package nus.iss.travlr.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import nus.iss.travlr.model.Itinerary;
import nus.iss.travlr.repository.ItineraryRepository;

@Service
public class ItineraryService {
    @Autowired
    private ItineraryRepository itiRepo;

    public void addItinerary(String userName, Itinerary itinerary) {
        itiRepo.addItinerary(userName, itinerary);
    }

    public Optional<List<Itinerary>> getItinerary(String userName) {
        Optional<List<Itinerary>> optItineraryList = itiRepo.getItinerary(userName);
        return optItineraryList;
    }

    public void deleteItinerary(String userName, Integer iid) {
        itiRepo.deleteItinerary(userName, iid);
    }

}
