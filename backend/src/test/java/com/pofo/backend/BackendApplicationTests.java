package com.pofo.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = BackendApplicationTests.class)
@SpringBootTest
class BackendApplicationTests {

	@Test
	void contextLoads() {
		System.out.println("--contextLoads--");
	}

}
