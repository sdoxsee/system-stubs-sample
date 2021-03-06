package com.example.systemstubssample;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({SystemStubsExtension.class})
class SystemStubsSampleApplicationTests {

	// sets the environment before Spring even starts
	@SystemStub
	private static EnvironmentVariables environmentVariables =
			new EnvironmentVariables("SOME_KEY", "original value");

	@Nested
	@SpringBootTest
	@AutoConfigureMockMvc
	@AutoConfigureWebTestClient
	class SpringBootTestMockMvcTest implements CommonEnvironmentTest {
		@Test
		void springBootTestMockMvc(@Autowired MockMvc mockMvc, EnvironmentVariables environmentVariables) {
			environmentTest(MockMvcWebTestClient.bindTo(mockMvc).build(), environmentVariables);
		}
	}

	@Nested
	@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
	@AutoConfigureWebTestClient
	class SpringBootTestRandomPortTest implements CommonEnvironmentTest {
		@Test
		void springBootTest(@Autowired WebTestClient webTestClient, EnvironmentVariables environmentVariables) {
			environmentTest(webTestClient, environmentVariables);
		}
	}

	@Nested
	@WebMvcTest(SampleController.class)
	@AutoConfigureWebTestClient
	class WebMvcTestTest implements CommonEnvironmentTest {
		@Test
		void webMvcTest(@Autowired WebTestClient webTestClient, EnvironmentVariables environmentVariables) {
			environmentTest(webTestClient, environmentVariables);
		}
	}

	interface CommonEnvironmentTest {

		default void environmentTest(WebTestClient webTestClient, EnvironmentVariables environmentVariables) {
			environmentVariables.set("SOME_KEY", "new value");
			assertThat(System.getenv("SOME_KEY")).isEqualTo("new value");

			webTestClient
					.get()
					.uri("/")
					.exchange()
					.expectBody(String.class)
					.value(Matchers.containsString("original: original value, live: new value"));

			environmentVariables.set("SOME_KEY", "newer value");
			assertThat(System.getenv("SOME_KEY")).isEqualTo("newer value");

			webTestClient
					.get()
					.uri("/")
					.exchange()
					.expectBody(String.class)
					.value(Matchers.containsString("original: original value, live: newer value"));
		}
	}
}
