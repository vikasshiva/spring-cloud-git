package com.example;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@EnableZuulProxy
@EnableDiscoveryClient
@SpringBootApplication
public class ReservationClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReservationClientApplication.class, args);
	}
	@Bean
	@LoadBalanced
	RestTemplate restTemplate() {
		return new RestTemplate();
	}
}


@RestController
@RequestMapping("/reservations")
class ReservationApiGatewayRestController {
	
	private final RestTemplate restTemplate;
	
	
	@Autowired
	public ReservationApiGatewayRestController( RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
	
	public Collection<String> fallback() {
		return new ArrayList<>();
	}
	
	@HystrixCommand(fallbackMethod = "fallback")
	@RequestMapping(method = RequestMethod.GET, value = "/names",produces="application/json")
	public Collection<String> names() {

		ParameterizedTypeReference<Resources<Reservation>> ptr =
				new ParameterizedTypeReference<Resources<Reservation>>() {
				};

		ResponseEntity<Resources<Reservation>> responseEntity =
				this.restTemplate.exchange("http://reservation-service/reservations",
						HttpMethod.GET,
						null,
						ptr
				);


		return responseEntity
				.getBody()
				.getContent()
				.stream()
				.map(Reservation::getReservationName)
				.collect(Collectors.toList());
	}
}

class Reservation {
	private String reservationName;

	public String getReservationName() {
		return reservationName;
	}
}