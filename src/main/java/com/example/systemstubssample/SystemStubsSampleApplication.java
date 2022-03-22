package com.example.systemstubssample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class SystemStubsSampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(SystemStubsSampleApplication.class, args);
	}

}

@RestController
class SampleController {

	private final String originalValue;

	public SampleController() {
		// as part of applicationContext initialization
		this.originalValue = System.getenv("SOME_KEY");
	}

	@GetMapping("/")
	public String result() {
		return "original: " + this.originalValue + ", live: " + System.getenv("SOME_KEY");
	}
}
