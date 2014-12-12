
DROP TABLE IF EXISTS Course;
DROP TABLE IF EXISTS Degree;

DROP TABLE IF EXISTS UserPlan;
DROP TABLE IF EXISTS User;

CREATE TABLE Course (
  course_id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
  subject VARCHAR(8),   #--CSCE
  number VARCHAR(8),    #--155A
  title VARCHAR(255),   
  description VARCHAR(4096),
  prerequisite_text VARCHAR(4096),
  adviser_comment VARCHAR(4096),  
  credit_hours INT,
  schedule VARCHAR(255),
  ui_group VARCHAR(255) NOT NULL DEFAULT 'Other'
)Engine=InnoDB,COLLATE=latin1_general_cs;

CREATE TABLE Degree (
  degree_id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
  title VARCHAR(255) NOT NULL
)Engine=InnoDB,COLLATE=latin1_general_cs;

INSERT INTO Degree (degree_id,title) VALUES (1, 'Computer Science');
INSERT INTO Degree (degree_id,title) VALUES (2, 'Computer Engineering');

CREATE TABLE User (
  user_id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
  first_name VARCHAR(255),
  last_name VARCHAR(255),
  nuid VARCHAR(8),
  my_unl_login VARCHAR(24),
  cse_login VARCHAR(16),
  is_admin boolean not null default false,
  CONSTRAINT unique_key_nuid UNIQUE INDEX(nuid),
  CONSTRAINT unique_key_my_unl_login UNIQUE INDEX(my_unl_login),
  CONSTRAINT unique_key_cse_login UNIQUE INDEX(cse_login)
)Engine=InnoDB,COLLATE=latin1_general_cs;

INSERT INTO User (user_id, first_name, last_name, nuid, my_unl_login, cse_login, is_admin) VALUES
	(1, 'Chris', 'Bourke', '35140602', 'cbourke3', 'cbourke', true);

CREATE TABLE UserPlan(
  user_plan_id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
  user_id INT NOT NULL,
  json_plan VARCHAR(4096),
  create_date VARCHAR(255),
  update_date VARCHAR(255),  
  FOREIGN KEY (user_id) REFERENCES User(user_id),
  CONSTRAINT unique_key_user_id UNIQUE INDEX(user_id)
)Engine=InnoDB,COLLATE=latin1_general_cs;

