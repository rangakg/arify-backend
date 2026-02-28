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
 * 
 * npm create vite@latest arify-web
 * 
 * 
 * git init
 * git add .
 * git commit -m "Initial React setup"
 * cd arify-web
 * npm install
 * npm run dev
 * npm run build
 * git init
 * git add .
 * git commit -m "Initial React setup"
 * git remote add origin https://github.com/yourusername/arify-frontend.git
 * git branch -M main
 * git push -u origin main
 * git remote remove origin
 * git remote add origin https://github.com/rangakg/arify-frontend.git
 * git branch -M main
 * git push -u origin main
 * git remote -v
 * 
 * ------------------------------------
 * ssh arify@37.27.82.89
 * 
 * arify@ubuntu-4gb-hel1-1:~$ sudo -u postgres psql
 * [sudo] password for arify:
 * psql (16.11 (Ubuntu 16.11-0ubuntu0.24.04.1))
 * Type "help" for help.
 * 
 * postgres=# \c arify
 * You are now connected to database "arify" as user "postgres".
 * arify=#
 * 
 * arify password is Pomi12345
 * ----------------------------------
 * sudo nano /etc/systemd/system/arify.service
 * 
 * --------------------------
 * NGINX configuration
 * sudo nano /etc/nginx/sites-available/arify
 * sudo nginx -t
 * sudo systemctl reload nginx
 */