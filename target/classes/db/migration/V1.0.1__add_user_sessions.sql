DROP TABLE IF EXISTS user_login_session;

CREATE TABLE user_login_session (
  id bigint NOT NULL AUTO_INCREMENT,
  login_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  source_id int DEFAULT NULL,
  last_update_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  time_in_app bigint NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY user_login_session_source_id_idx (source_id),
  KEY user_login_session_source_id_date_idx (source_id, login_date),
  KEY user_login_session_source_id_date_time_idx (source_id, login_date, time_in_app)
 );
 
 ALTER TABLE assessment_test
 ADD COLUMN user_login_session_id bigint NULL COMMENT 'Indicates the session_id that was used when either creating this test or getting the most recent current one',
 CHANGE COLUMN student_id source_id int DEFAULT NULL;