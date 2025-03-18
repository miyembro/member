package com.rjproj.memberapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.config.location=classpath:/application-test.yml")
class MemberApplicationTests {

	@Test
	void contextLoads() {
	}

}
