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
 * 
 * 
 * new BCryptPasswordEncoder().encode("admin123")
 * 
 * INSERT INTO staff_users (username, password, role)
 * VALUES (
 * 'admin',
 * '$2a$10$yourbcryptvaluehere',
 * 'ADMIN'
 * );
 * 
 * 
 * 
 * --------------------------------------------------------------------
 * DB Schema
 * arify=# \dt
 * List of relations
 * Schema | Name | Type | Owner
 * --------+-----------------+-------+-----------
 * public | appointments | table | arify_app
 * public | doctor_schedule | table | arify_app
 * public | doctors | table | arify_app
 * public | services | table | arify_app
 * public | slots | table | arify_app
 * public | staff_users | table | postgres
 * public | users | table | arify_app
 * (7 rows)
 * 
 * arify=# \d+ services
 * Table "public.services"
 * Column | Type | Collation | Nullable | Default | Storage | Compression |
 * Stats target | Description
 * ------------+--------------------------+-----------+----------+--------------------------------------+----------+-------------+--------------+-------------
 * id | bigint | | not null | nextval('services_id_seq'::regclass) | plain | | |
 * name | text | | not null | | extended | | |
 * active | boolean | | | true | plain | | |
 * created_at | timestamp with time zone | | | now() | plain | | |
 * Indexes:
 * "services_pkey" PRIMARY KEY, btree (id)
 * "services_name_key" UNIQUE CONSTRAINT, btree (name)
 * Referenced by:
 * TABLE "doctors" CONSTRAINT "doctors_service_id_fkey" FOREIGN KEY (service_id)
 * REFERENCES services(id) ON DELETE SET NULL
 * Access method: heap
 * 
 * arify=# \d+ doctors
 * Table "public.doctors"
 * Column | Type | Collation | Nullable | Default | Storage | Compression |
 * Stats target | Description
 * ------------+---------+-----------+----------+-------------------------------------+----------+-------------+--------------+-------------
 * id | bigint | | not null | nextval('doctors_id_seq'::regclass) | plain | | |
 * name | text | | not null | | extended | | |
 * service_id | bigint | | | | plain | | |
 * phone | text | | | | extended | | |
 * active | boolean | | | true | plain | | |
 * Indexes:
 * "doctors_pkey" PRIMARY KEY, btree (id)
 * "doctors_phone_key" UNIQUE CONSTRAINT, btree (phone)
 * Foreign-key constraints:
 * "doctors_service_id_fkey" FOREIGN KEY (service_id) REFERENCES services(id) ON
 * DELETE SET NULL
 * Referenced by:
 * TABLE "appointments" CONSTRAINT "appointments_doctor_id_fkey" FOREIGN KEY
 * (doctor_id) REFERENCES doctors(id)
 * TABLE "doctor_schedule" CONSTRAINT "doctor_schedule_doctor_id_fkey" FOREIGN
 * KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE
 * TABLE "slots" CONSTRAINT "slots_doctor_id_fkey" FOREIGN KEY (doctor_id)
 * REFERENCES doctors(id) ON DELETE CASCADE
 * Access method: heap
 * 
 * arify=# \d+ slots
 * Table "public.slots"
 * Column | Type | Collation | Nullable | Default | Storage | Compression |
 * Stats target | Description
 * --------------+--------------------------+-----------+----------+-----------------------------------+----------+-------------+--------------+-------------
 * id | bigint | | not null | nextval('slots_id_seq'::regclass) | plain | | |
 * doctor_id | bigint | | not null | | plain | | |
 * slot_time | timestamp with time zone | | not null | | plain | | |
 * status | text | | not null | | extended | | |
 * locked_until | timestamp with time zone | | | | plain | | |
 * Indexes:
 * "slots_pkey" PRIMARY KEY, btree (id)
 * "idx_slots_doctor_time" btree (doctor_id, slot_time)
 * "idx_slots_status" btree (status)
 * Check constraints:
 * "slots_status_check" CHECK (status = ANY (ARRAY['AVAILABLE'::text,
 * 'LOCKED'::text, 'BOOKED'::text]))
 * Foreign-key constraints:
 * "slots_doctor_id_fkey" FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON
 * DELETE CASCADE
 * Referenced by:
 * TABLE "appointments" CONSTRAINT "appointments_slot_id_fkey" FOREIGN KEY
 * (slot_id) REFERENCES slots(id)
 * Access method: heap
 * 
 * arify=# \d+ appointments
 * Table "public.appointments"
 * Column | Type | Collation | Nullable | Default | Storage | Compression |
 * Stats target | Description
 * --------------+--------------------------+-----------+----------+---------+----------+-------------+--------------+-------------
 * phone | text | | not null | | extended | | |
 * doctor_id | bigint | | not null | | plain | | |
 * slot_id | bigint | | not null | | plain | | |
 * status | text | | not null | | extended | | |
 * order_id | text | | | | extended | | |
 * created_at | timestamp with time zone | | | now() | plain | | |
 * confirmed_at | timestamp with time zone | | | | plain | | |
 * Indexes:
 * "appointments_pkey" PRIMARY KEY, btree (phone)
 * "idx_appointments_slot" UNIQUE, btree (slot_id)
 * Check constraints:
 * "appointments_status_check" CHECK (status = ANY (ARRAY['LOCKED'::text,
 * 'PAID'::text, 'CANCELLED'::text]))
 * Foreign-key constraints:
 * "appointments_doctor_id_fkey" FOREIGN KEY (doctor_id) REFERENCES doctors(id)
 * "appointments_phone_fkey" FOREIGN KEY (phone) REFERENCES users(phone) ON
 * DELETE CASCADE
 * "appointments_slot_id_fkey" FOREIGN KEY (slot_id) REFERENCES slots(id)
 * Access method: heap
 * 
 * arify=# \d+ users
 * Table "public.users"
 * Column | Type | Collation | Nullable | Default | Storage | Compression |
 * Stats target | Description
 * ------------+--------------------------+-----------+----------+---------+----------+-------------+--------------+-------------
 * phone | text | | not null | | extended | | |
 * name | text | | not null | | extended | | |
 * created_at | timestamp with time zone | | | now() | plain | | |
 * Indexes:
 * "users_pkey" PRIMARY KEY, btree (phone)
 * Referenced by:
 * TABLE "appointments" CONSTRAINT "appointments_phone_fkey" FOREIGN KEY (phone)
 * REFERENCES users(phone) ON DELETE CASCADE
 * Access method: heap
 * 
 * arify=# \d+ staff_users
 * Table "public.staff_users"
 * Column | Type | Collation | Nullable | Default | Storage | Compression |
 * Stats target | Description
 * ----------+------------------------+-----------+----------+-----------------------------------------+----------+-------------+--------------+-------------
 * id | bigint | | not null | nextval('staff_users_id_seq'::regclass) | plain |
 * | |
 * username | character varying(50) | | not null | | extended | | |
 * password | character varying(255) | | not null | | extended | | |
 * role | character varying(20) | | not null | | extended | | |
 * active | boolean | | not null | true | plain | | |
 * Indexes:
 * "staff_users_pkey" PRIMARY KEY, btree (id)
 * "staff_users_username_key" UNIQUE CONSTRAINT, btree (username)
 * Check constraints:
 * "staff_users_role_check" CHECK (role::text = ANY (ARRAY['ADMIN'::character
 * varying, 'FRONT_DESK'::character varying]::text[]))
 * Access method: heap
 * 
 * arify=# select * from staff-users
 * arify-# select * from staff_users
 * arify-# select * from staff_users;
 * ERROR: syntax error at or near "-"
 * LINE 1: select * from staff-users
 * ^
 * arify=# select * from staff_users;
 * id | username | password | role | active
 * ----+-----------+--------------------------------------------------------------+------------+--------
 * 1 | admin | $2a$10$EENEW8wZN5j/f97hoyIdjez./caNZ4uhK/HsPO60KEHyVsWJqRlm2 |
 * ADMIN | t
 * 2 | frontdesk | $2a$10$TPr9JHKcpoxtnjSqHwLPbe/rlZG3Bau732RwiZDoEvG6Avqdn60mi
 * | FRONT_DESK | t
 * (2 rows)
 * 
 * arify=# CREATE TABLE appointments_history (
 * id BIGSERIAL PRIMARY KEY,
 * 
 * appointment_phone TEXT NOT NULL,
 * doctor_id BIGINT NOT NULL,
 * slot_id BIGINT NOT NULL,
 * 
 * previous_status TEXT,
 * new_status TEXT NOT NULL,
 * 
 * changed_by TEXT, -- STAFF_USER / SYSTEM / WHATSAPP
 * 
 * order_id TEXT,
 * 
 * event_time TIMESTAMPTZ DEFAULT now(),
 * 
 * note TEXT
 * );
 * CREATE TABLE
 * arify=# ALTER TABLE appointments_history
 * ADD CONSTRAINT fk_history_phone
 * FOREIGN KEY (appointment_phone)
 * REFERENCES users(phone)
 * ON DELETE CASCADE;
 * 
 * ALTER TABLE appointments_history
 * ADD CONSTRAINT fk_history_doctor
 * FOREIGN KEY (doctor_id)
 * REFERENCES doctors(id);
 * 
 * ALTER TABLE appointments_history
 * ADD CONSTRAINT fk_history_slot
 * FOREIGN KEY (slot_id)
 * REFERENCES slots(id);
 * ALTER TABLE
 * ALTER TABLE
 * ALTER TABLE
 * arify=#
 * CREATE INDEX idx_history_phone
 * ON appointments_history(appointment_phone);
 * 
 * CREATE INDEX idx_history_slot
 * ON appointments_history(slot_id);
 * 
 * CREATE INDEX idx_history_doctor
 * ON appointments_history(doctor_id);
 * 
 * CREATE INDEX idx_history_time
 * ON appointments_history(event_time);
 * 
 * 
 * 
 * ----------------------------------------------
 * GIT SSH Configuration
 * arify@ubuntu-4gb-hel1-1:~$ ssh-keygen -t ed25519 -C "ranga.kg@gmail.com"
 * Generating public/private ed25519 key pair.
 * Enter file in which to save the key (/home/arify/.ssh/id_ed25519):
 * Enter passphrase (empty for no passphrase):
 * Enter same passphrase again:
 * Your identification has been saved in /home/arify/.ssh/id_ed25519
 * Your public key has been saved in /home/arify/.ssh/id_ed25519.pub
 * The key fingerprint is:
 * SHA256:uWdt0PvwOO8a4iGxo6esIYhfRTSQSvcfu7G6OT5cWGw ranga.kg@gmail.com
 * The key's randomart image is:
 * +--[ED25519 256]--+
 * | .oo |
 * | . o. . |
 * | . o ... |
 * | . .. E. . |
 * | .=So. . |
 * |.. .. =+ o . |
 * |o . o. .=+= * |
 * | . o o+o+* +.* |
 * | . .oOB . o== |
 * +----[SHA256]-----+
 * arify@ubuntu-4gb-hel1-1:~$ cat ~/.ssh/id_ed25519.pub
 * ssh-ed25519
 * AAAAC3NzaC1lZDI1NTE5AAAAIBOs4NGKeLQCeKdaUvelw0GbcebVSzLEdnzZ8mYdOH3x
 * ranga.kg@gmail.com
 * arify@ubuntu-4gb-hel1-1:~$
 * arify@ubuntu-4gb-hel1-1:~$
 * arify@ubuntu-4gb-hel1-1:~$ cd arify-frontend/
 * arify@ubuntu-4gb-hel1-1:~/arify-frontend$ git remote set-url origin
 * git@github.com:rangakg/arify-frontend.git
 * arify@ubuntu-4gb-hel1-1:~/arify-frontend$ git remote -v
 * origin git@github.com:rangakg/arify-frontend.git (fetch)
 * origin git@github.com:rangakg/arify-frontend.git (push)
 * arify@ubuntu-4gb-hel1-1:~/arify-frontend$ ssh -T git@github.com
 * The authenticity of host 'github.com (140.82.121.4)' can't be established.
 * ED25519 key fingerprint is
 * SHA256:+DiY3wvvV6TuJJhbpZisF/zLDA0zPMSvHdkr4UvCOqU.
 * This key is not known by any other names.
 * Are you sure you want to continue connecting (yes/no/[fingerprint])?
 * Host key verification failed.
 * arify@ubuntu-4gb-hel1-1:~/arify-frontend$ ssh -T git@github.com
 * The authenticity of host 'github.com (140.82.121.3)' can't be established.
 * ED25519 key fingerprint is
 * SHA256:+DiY3wvvV6TuJJhbpZisF/zLDA0zPMSvHdkr4UvCOqU.
 * This key is not known by any other names.
 * Are you sure you want to continue connecting (yes/no/[fingerprint])? yes
 * Warning: Permanently added 'github.com' (ED25519) to the list of known hosts.
 * Hi rangakg! You've successfully authenticated, but GitHub does not provide
 * shell access.
 * arify@ubuntu-4gb-hel1-1:~/arify-frontend$ git push origin main
 * Enumerating objects: 31, done.
 * Counting objects: 100% (31/31), done.
 * Delta compression using up to 2 threads
 * Compressing objects: 100% (14/14), done.
 * Writing objects: 100% (18/18), 3.08 KiB | 787.00 KiB/s, done.
 * Total 18 (delta 4), reused 0 (delta 0), pack-reused 0
 * remote: Resolving deltas: 100% (4/4), completed with 4 local objects.
 * To github.com:rangakg/arify-frontend.git
 * 3aabdd3..6da982d main -> main
 * 
 */