package com.pls.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pls.exceptions.VehicleNotFoundException;
import com.pls.model.Car;
import com.pls.model.ParkingResponse;
import com.pls.model.ParkingStatus;
import com.pls.model.Vehicle;
import com.pls.model.VehicleType;
import com.pls.service.DataService;
import com.pls.service.ParkingService;
import com.pls.service.VehicleInfoHelper;

import junit.framework.Assert;

@SuppressWarnings("deprecation")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ParkingController.class, ParkingService.class, VehicleInfoHelper.class })
public class ParkingControllerTests {

	private static final Logger LOGGER = LogManager.getLogger(ParkingControllerTests.class);


	private static ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private ParkingService parkingService;

	@MockBean
	private VehicleInfoHelper vehicleInfoHelper;

	@Test
	public void parkVehicle() {
		
		Mockito.when(vehicleInfoHelper.getVehicleDetails("CAR-0001")).thenReturn(getVehicle("CAR-0001"));
		Mockito.when(vehicleInfoHelper.getVehicleDetails("BIKE-0001")).thenReturn(getVehicle("BIKE-0001"));
		try {
			
			Vehicle vehicle=vehicleInfoHelper.getVehicleDetails("CAR-0001");
			Assert.assertEquals(VehicleType.CAR,vehicle.getVehicleType() );
			String carkParkResponse = parkingService.park("CAR-0001");
			String bikeParkResponse = parkingService.park("BIKE-0001");
			Assert.assertNotNull(carkParkResponse);
			Assert.assertNotNull(bikeParkResponse);
			
			try {
				ParkingResponse parkingResponse = objectMapper.readValue(carkParkResponse, ParkingResponse.class);
				Assert.assertEquals(ParkingStatus.P, parkingResponse.getParkingStatus());
				parkingResponse = objectMapper.readValue(bikeParkResponse, ParkingResponse.class);
				Assert.assertEquals(ParkingStatus.P, parkingResponse.getParkingStatus());
				
			} catch (IOException e) {
				e.printStackTrace();
			}

			
		} catch (VehicleNotFoundException e) {
			e.printStackTrace();
		}


	}


	private Vehicle getVehicle(String vehicleNumber) {
			
		return DataService.getVehicleInfoFromCache(vehicleNumber);
	}


	@SuppressWarnings("deprecation")
	@Test
	public void unParkVehicle() {

		String carkParkResponse = parkingService.unPark("CAR-0001");
		String bikeParkResponse = parkingService.unPark("BIKE-0001");
		Assert.assertNotNull(carkParkResponse);
		Assert.assertNotNull(bikeParkResponse);
		
		try {
			ParkingResponse parkingResponse = objectMapper.readValue(carkParkResponse, ParkingResponse.class);
			Assert.assertEquals(ParkingStatus.UNP, parkingResponse.getParkingStatus());
			parkingResponse = objectMapper.readValue(bikeParkResponse, ParkingResponse.class);
			Assert.assertEquals(ParkingStatus.UNP, parkingResponse.getParkingStatus());
			
		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	@Before
	public void prepareVehicleData() {
		Vehicle car1 = new Car("CAR-0001");
		car1.setColor("BLUE");
		car1.setName("MARUTI SWIFT");
		car1.setVehicleType(VehicleType.CAR);

		Vehicle bike1 = new Car("BIKE-0001");
		bike1.setColor("BLUE");
		bike1.setName("MARUTI SWIFT");
		bike1.setVehicleType(VehicleType.BIKE);

		DataService.saveInVehicleInfoCache(car1.getNumber(), car1);
		DataService.saveInVehicleInfoCache(bike1.getNumber(), bike1);

	}

}
