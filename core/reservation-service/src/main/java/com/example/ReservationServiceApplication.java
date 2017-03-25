package com.example;

import java.util.Collection;
import java.util.stream.Stream;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class ReservationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReservationServiceApplication.class, args);
	}
}


@Component
class NexonHealthIndicator implements HealthIndicator {

	@Override
	public Health health() {
		return Health.status("I <3 Spring Clouds!!").build();
	}
}


@RestController
@RefreshScope
class MessageRestController {

	@RequestMapping(method = RequestMethod.GET, value = "/message")
	public String read() {
		return this.value;
	}

	private final String value;

	@Autowired
	public MessageRestController(
			@Value("${message}") String value) {
		this.value = value;
	}
}

@Component
class SampleRecordsCLR implements CommandLineRunner {

	private final ReservationRepository reservationRepository;

	@Autowired
	public SampleRecordsCLR(ReservationRepository reservationRepository) {
		this.reservationRepository = reservationRepository;
	}

	@Override
	public void run(String... args) throws Exception {
		Stream.of("Josh", "Jungryeol", "Nosung", "Hyobeom",
				"Soeun", "Seunghue", "Peter", "Jooyong")
				.forEach(name -> reservationRepository.save(new Reservation(name)));

		reservationRepository.findAll().forEach(System.out::println);
	}
}

@RepositoryRestResource
interface ReservationRepository extends JpaRepository<Reservation, Long> {

	@RestResource(path = "by-name")
	Collection<Reservation> findByReservationName(@Param("rn") String rn);
}

@Entity
class Reservation {

	@Id
	@GeneratedValue
	private Long id;  // id

	private String reservationName;  // reservation_name

	public Long getId() {
		return id;
	}

	public String getReservationName() {
		return reservationName;
	}

	@Override
	public String toString() {
		return "Reservation{" +
				"id=" + id +
				", reservationName='" + reservationName + '\'' +
				'}';
	}

	Reservation() {// why JPA why???
	}

	public Reservation(String reservationName) {

		this.reservationName = reservationName;
	}
}