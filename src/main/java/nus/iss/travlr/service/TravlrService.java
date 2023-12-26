package nus.iss.travlr.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nus.iss.travlr.model.Activity;
import nus.iss.travlr.model.Itinerary;
import nus.iss.travlr.repository.TravlrRepository;

@Service
public class TravlrService {
    @Autowired
    private TravlrRepository travRepo;

    public void addItinerary(String userName, Itinerary itinerary) {
        travRepo.addItinerary(userName, itinerary);
    }

    public Optional<List<Itinerary>> getItinerary(String userName) {
        Optional<List<Itinerary>> optItineraryList = travRepo.getItinerary(userName);
        return optItineraryList;
    }

    public void deleteItinerary() {

    }

    public void addActivity(String userName, Integer iid, Activity activity) {
        travRepo.addActivity(userName, iid, activity);
    }

}
