package nrg.sdnsimulator.web.services;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.util.JSONPObject;

@Service
public class StudyService {

	public JSONPObject runStudy(JSONPObject config) {
		// uses topology and network service to create the set of simulations to be run
		// uses the simulation service to run the simulation and get the results
		// the results can be either singular or the whole (yet to be decided)
		return null;
	}

}
