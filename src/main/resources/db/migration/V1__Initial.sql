CREATE TABLE assessment_questions (
  id int NOT NULL AUTO_INCREMENT,
  source_id varchar(40) DEFAULT NULL,
  answer_2 longtext NOT NULL,
  answer_3 longtext NOT NULL,
  answer_4 longtext NOT NULL,
  answer_correct longtext NOT NULL,
  credit longtext,
  lexile double NOT NULL,
  passage longtext NOT NULL,
  pool varchar(32) NOT NULL,
  question longtext NOT NULL,
  active tinyint NOT NULL DEFAULT '1',
  suppressed tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (id),
  UNIQUE KEY source_id (source_id),
  KEY ix_assessment_questions_lexile_pool_active (lexile,pool,active,suppressed)
);

CREATE TABLE assessment_test (
  id bigint NOT NULL AUTO_INCREMENT,
  current_question int NOT NULL,
  assigned_date datetime DEFAULT NULL,
  created_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  modified_date datetime DEFAULT NULL,
  description varchar(255) DEFAULT NULL,
  end_lexile int NOT NULL,
  end_sigma decimal(10,6) NOT NULL,
  lexile double NOT NULL,
  number_skips_possible int NOT NULL,
  num_skipped_questions int DEFAULT NULL,
  practice_questions_possible int NOT NULL,
  num_practice_questions int DEFAULT NULL,
  school_year int DEFAULT NULL,
  start_lexile int NOT NULL,
  start_sigma decimal(10,6) NOT NULL,
  student_id bigint DEFAULT NULL,
  test_state varchar(255) DEFAULT NULL,
  test_type varchar(255) DEFAULT NULL,
  uncertainity decimal(8,4) DEFAULT NULL,
  PRIMARY KEY (id),
  KEY assessment_test_student_id_idx (student_id)
 );

CREATE TABLE assessment_test_state (
  assessment_id bigint NOT NULL,
  value varchar(255) DEFAULT NULL,
  name varchar(255) NOT NULL,
  PRIMARY KEY (assessment_id,name),
  CONSTRAINT fk_ats_assessment_id_assessment_test_id FOREIGN KEY (assessment_id) REFERENCES assessment_test (id)
);

CREATE TABLE assessment_issued_questions (
  id int NOT NULL AUTO_INCREMENT,
  answer_type int NOT NULL,
  answered_time datetime DEFAULT NULL,
  correct_ans_display int NOT NULL,
  correct_ans_index int NOT NULL,
  created datetime DEFAULT NULL,
  end_lexile int NOT NULL,
  end_sigma decimal(10,6) NOT NULL,
  modified datetime DEFAULT NULL,
  question_lexile int NOT NULL,
  question_seq int NOT NULL,
  question_type int NOT NULL,
  skipped bit(1) NOT NULL,
  start_lexile int NOT NULL,
  start_sigma decimal(10,6) NOT NULL,
  student_ans_display int NOT NULL,
  student_ans_index int NOT NULL,
  success bit(1) NOT NULL,
  time_elapsed_seconds int NOT NULL,
  assessment_id bigint DEFAULT NULL,
  assessment_question_id int DEFAULT NULL,
  PRIMARY KEY (id),
  KEY ix_assess_id (assessment_id),
  KEY ix_assessment_question_id (assessment_question_id),
  CONSTRAINT fk_aiq_asessment_question_id_assessment_questions FOREIGN KEY (assessment_question_id) REFERENCES assessment_questions (id),
  CONSTRAINT fk_aiq_assessment_id_assessment_test_id FOREIGN KEY (assessment_id) REFERENCES assessment_test (id)
);

CREATE TABLE default_prior_assessment_lexiles (
  id int NOT NULL AUTO_INCREMENT,
  above_level int DEFAULT NULL,
  below_level int DEFAULT NULL,
  grade_code varchar(255) DEFAULT NULL,
  on_level int DEFAULT NULL,
  school_year_state varchar(255) DEFAULT NULL,
  PRIMARY KEY (id)
);