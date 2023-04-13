package com.itmsd.medical;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SpringBootApplication
public class MyhealthApplication extends SpringBootServletInitializer {


	public static void main(String[] args) {

		SpringApplication.run(MyhealthApplication.class, args);
		
	}
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(MyhealthApplication.class);
	}
	@RestController
	public class TomcatController {

		@GetMapping("/hello")
		public Collection<String> sayHello() {
			return IntStream.range(0, 10)
					.mapToObj(i -> "Hello number " + i)
					.collect(Collectors.toList());
		}
	}
}
