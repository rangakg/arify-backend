package com.arify.pomi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PomiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PomiApplication.class, args);
	}

}

/**
 * 
 * cd /home/arify/arify-backend
 * git pull
 * ./mvnw clean package -DskipTests
 * sudo systemctl restart arify
 * ------------------------------------------
 * sudo vi /etc/systemd/system/arify.service
 * 
 * [Unit]
 * Description=Arify Spring Boot Backend
 * After=network.target
 * 
 * [Service]
 * User=arify
 * WorkingDirectory=/home/arify/arify-backend
 * ExecStart=/usr/bin/java -jar
 * /home/arify/arify-backend/target/pomi-0.0.1-SNAPSHOT.jar
 * SuccessExitStatus=143
 * Restart=always
 * RestartSec=5
 * 
 * Environment=SPRING_PROFILES_ACTIVE=prod
 * 
 * [Install]
 * WantedBy=multi-user.target
 * -------------------------------------------
 * hertzner start springboot service, show logs
 * sudo systemctl start arify
 * sudo journalctl -u arify -f
 * ---------------------------------------------
 */