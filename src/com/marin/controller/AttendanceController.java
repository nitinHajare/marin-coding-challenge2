package com.marin.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.marin.common.GlobalConstants;
import com.marin.model.Sansad;
import com.marin.util.AttendanceLoaderSingleton;

@Controller
@RequestMapping("/marin")
public class AttendanceController {
	// Logger to log messages
	private Logger logger = Logger.getLogger("AttendanceController");

	@RequestMapping(value = { "/highest-attendance/{topN}" }, method = { RequestMethod.GET })
	public @ResponseBody Map<String, Integer> getHighestAttendance(
			@PathVariable int topN, HttpServletRequest request) {

		logger.info("getHighestAttendance started...");

		GlobalConstants.SPREADSHEET_DATA_FOLDER_PATH = request
				.getSession()
				.getServletContext()
				.getRealPath(
						GlobalConstants.SPREADSHEET_DATA_FOLDER_PLACEHOLDER);

		AttendanceLoaderSingleton singleton = AttendanceLoaderSingleton
				.getInstance();

		Map<Integer, Sansad> sansads = singleton.getAttendance();

		List<Map.Entry<Integer, Sansad>> entryList = new ArrayList<Map.Entry<Integer, Sansad>>(
				sansads.entrySet());

		Collections.sort(entryList,
				new Comparator<Map.Entry<Integer, Sansad>>() {
					@Override
					public int compare(Map.Entry<Integer, Sansad> sansad1,
							Map.Entry<Integer, Sansad> sansad2) {
						if (sansad1.getValue().getAttendance() >= sansad2
								.getValue().getAttendance()) {
							return -1;
						} else {
							return 1;
						}
					}
				});

		logger.info(String.format(
				"Fetching top %d Sansad with highest attendance", topN));

		Map<String, Integer> sansadAttendanceMap = new LinkedHashMap<>();

		int fetchCount = 0;
		if (topN > entryList.size()) {
			fetchCount = entryList.size();
		} else {
			fetchCount = topN;
		}
		for (int index = 0; index < fetchCount; index++) {
			Sansad sansad = entryList.get(index).getValue();
			sansadAttendanceMap.put(sansad.getMemberName(),
					sansad.getAttendance());
		}

		logger.info("getHighestAttendance finished...");

		return sansadAttendanceMap;
	}

	@RequestMapping(value = { "/sansad-by-state" }, method = { RequestMethod.GET })
	public @ResponseBody Map<String, Integer> sansadByStates(
			HttpServletRequest request) {

		logger.info("sansadByStates started...");

		GlobalConstants.SPREADSHEET_DATA_FOLDER_PATH = request
				.getSession()
				.getServletContext()
				.getRealPath(
						GlobalConstants.SPREADSHEET_DATA_FOLDER_PLACEHOLDER);

		Map<String, Integer> stateWiseSansads = new HashMap<>();
		AttendanceLoaderSingleton singleton = AttendanceLoaderSingleton
				.getInstance();
		logger.info("Fetching states with number of LS seats");
		Map<Integer, Sansad> sansads = singleton.getAttendance();
		for (Map.Entry<Integer, Sansad> entry : sansads.entrySet()) {
			Sansad sansad = entry.getValue();
			if (sansad != null) {
				String state = sansad.getState();
				if (state != null) {
					Integer count = stateWiseSansads.get(state);
					if (count == null) {
						stateWiseSansads.put(state, 1);
					} else {
						stateWiseSansads.put(state, ++count);
					}
				}
			}
		}

		logger.info("sansadByStates finished...");

		return stateWiseSansads;
	}

	@RequestMapping(value = { "/states-by-attendance" }, method = { RequestMethod.GET })
	public @ResponseBody Map<String, Double> getStateByAttendance(
			HttpServletRequest request,
			@RequestParam(required = false, value = "sortby") String sortBy) {

		logger.info("getStateByAttendance started...");

		GlobalConstants.SPREADSHEET_DATA_FOLDER_PATH = request
				.getSession()
				.getServletContext()
				.getRealPath(
						GlobalConstants.SPREADSHEET_DATA_FOLDER_PLACEHOLDER);

		// Map key would be state where as value would be integer array with
		// size 2 where first element is number of MPs per state and second one
		// is sum of attendance of all MPs
		Map<String, int[]> stateWiseSansads = new HashMap<>();

		AttendanceLoaderSingleton singleton = AttendanceLoaderSingleton
				.getInstance();
		Map<Integer, Sansad> sansads = singleton.getAttendance();

		for (Map.Entry<Integer, Sansad> entry : sansads.entrySet()) {
			Sansad sansad = entry.getValue();
			if (sansad != null) {
				String state = sansad.getState();
				if (state != null) {
					int[] sansadCountAndAttendanceArray = stateWiseSansads
							.get(state);
					if (sansadCountAndAttendanceArray == null) {
						stateWiseSansads.put(state,
								new int[] { 1, sansad.getAttendance() });
					} else {
						int sansadCount = sansadCountAndAttendanceArray[0];
						int attendanceCount = sansadCountAndAttendanceArray[1];
						stateWiseSansads.put(state, new int[] { ++sansadCount,
								attendanceCount + sansad.getAttendance() });

					}
				}
			}
		}

		// states by average attendance
		Map<String, Double> avarageStateAttendance = new HashMap<>();

		for (Entry<String, int[]> enrty : stateWiseSansads.entrySet()) {
			String state = enrty.getKey();
			int[] sansadCountAndAttendanceArray = enrty.getValue();
			int sansadCount = sansadCountAndAttendanceArray[0];
			int attendanceCount = sansadCountAndAttendanceArray[1];

			double avg = attendanceCount / sansadCount;
			avarageStateAttendance.put(state, avg);
		}

		Set<Entry<String, Double>> set = avarageStateAttendance.entrySet();
		List<Entry<String, Double>> entryList = new ArrayList<Entry<String, Double>>(
				set);

		if (sortBy != null && sortBy.equalsIgnoreCase("asc")) {

			logger.info(String.format("Fetching states attendance by %s order",
					sortBy));

			Collections.sort(entryList,
					new Comparator<Map.Entry<String, Double>>() {
						@Override
						public int compare(Map.Entry<String, Double> o1,
								Map.Entry<String, Double> o2) {
							return (o1.getValue()).compareTo(o2.getValue());
						}
					});
		} else {

			logger.info(String
					.format("Fetching states attendance by descending order"));

			Collections.sort(entryList,
					new Comparator<Map.Entry<String, Double>>() {
						@Override
						public int compare(Map.Entry<String, Double> o1,
								Map.Entry<String, Double> o2) {
							return (o2.getValue()).compareTo(o1.getValue());
						}
					});
		}

		logger.info("getStateByAttendance finished...");

		Map<String, Double> sansadAttendanceMap = new LinkedHashMap<>();

		for (Map.Entry<String, Double> entry : entryList) {
			sansadAttendanceMap.put(entry.getKey(), entry.getValue());
		}

		return sansadAttendanceMap;
	}

	@RequestMapping(value = { "/lowest-attendance/{topN}" }, method = { RequestMethod.GET })
	public @ResponseBody Map<String, Integer> getLowestAttendance(
			@PathVariable int topN, HttpServletRequest request) {

		logger.info("getLowestAttendance started...");

		GlobalConstants.SPREADSHEET_DATA_FOLDER_PATH = request
				.getSession()
				.getServletContext()
				.getRealPath(
						GlobalConstants.SPREADSHEET_DATA_FOLDER_PLACEHOLDER);

		AttendanceLoaderSingleton singleton = AttendanceLoaderSingleton
				.getInstance();

		Map<Integer, Sansad> sansads = singleton.getAttendance();

		List<Map.Entry<Integer, Sansad>> entryList = new ArrayList<Map.Entry<Integer, Sansad>>(
				sansads.entrySet());

		Collections.sort(entryList,
				new Comparator<Map.Entry<Integer, Sansad>>() {
					@Override
					public int compare(Map.Entry<Integer, Sansad> sansad1,
							Map.Entry<Integer, Sansad> sansad2) {
						if (sansad1.getValue().getAttendance() <= sansad2
								.getValue().getAttendance()) {
							return -1;
						} else {
							return 1;
						}
					}
				});

		logger.info(String.format(
				"Fetching top %d Sansad with lowest attendance", topN));

		Map<String, Integer> sansadAttendanceMap = new LinkedHashMap<>();

		int fetchCount = 0;
		if (topN > entryList.size()) {
			fetchCount = entryList.size();
		} else {
			fetchCount = topN;
		}
		for (int index = 0; index < fetchCount; index++) {
			Sansad sansad = entryList.get(index).getValue();
			sansadAttendanceMap.put(sansad.getMemberName(),
					sansad.getAttendance());
		}

		logger.info("getLowestAttendance finished...");

		return sansadAttendanceMap;
	}

	@RequestMapping(value = { "/{state}/attendance" }, method = { RequestMethod.GET })
	public @ResponseBody Map<String, Integer> topAttendaceWithinState(
			@PathVariable String state,
			@RequestParam(required = false, value = "sortby") String sortBy,
			HttpServletRequest request) {

		logger.info("topAttendaceWithinState started...");

		GlobalConstants.SPREADSHEET_DATA_FOLDER_PATH = request
				.getSession()
				.getServletContext()
				.getRealPath(
						GlobalConstants.SPREADSHEET_DATA_FOLDER_PLACEHOLDER);

		Map<String, Integer> stateWiseSansads = new HashMap<>();

		AttendanceLoaderSingleton singleton = AttendanceLoaderSingleton
				.getInstance();

		Map<Integer, Sansad> sansads = singleton.getAttendance();

		for (Map.Entry<Integer, Sansad> entry : sansads.entrySet()) {
			Sansad sansad = entry.getValue();
			if (sansad != null) {
				String stateName = sansad.getState();
				if (stateName != null && stateName.equalsIgnoreCase(state)) {
					String memberName = sansad.getMemberName();
					Integer attendance = stateWiseSansads.get(memberName);
					if (attendance == null) {
						stateWiseSansads
								.put(memberName, sansad.getAttendance());
					} else {
						stateWiseSansads.put(memberName,
								attendance + sansad.getAttendance());
					}
				}
			}
		}

		Set<Entry<String, Integer>> set = stateWiseSansads.entrySet();
		List<Entry<String, Integer>> entryList = new ArrayList<Entry<String, Integer>>(
				set);

		if (sortBy != null && sortBy.equalsIgnoreCase("asc")) {

			logger.info(String.format("Fetching states attendance by %s order",
					sortBy));

			Collections.sort(entryList,
					new Comparator<Map.Entry<String, Integer>>() {
						@Override
						public int compare(Map.Entry<String, Integer> o1,
								Map.Entry<String, Integer> o2) {
							return (o1.getValue()).compareTo(o2.getValue());
						}
					});
		} else {

			logger.info(String
					.format("Fetching states attendance by descending order"));

			Collections.sort(entryList,
					new Comparator<Map.Entry<String, Integer>>() {
						@Override
						public int compare(Map.Entry<String, Integer> o1,
								Map.Entry<String, Integer> o2) {
							return (o2.getValue()).compareTo(o1.getValue());
						}
					});
		}

		Map<String, Integer> sansadAttendanceMap = new LinkedHashMap<>();

		for (Map.Entry<String, Integer> entry : entryList) {
			sansadAttendanceMap.put(entry.getKey(), entry.getValue());
		}

		logger.info("topAttendaceWithinState finished...");

		return sansadAttendanceMap;
	}
}