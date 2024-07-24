
-- 취업 게시판 게시글 Table

DROP TABLE IF EXISTS job_board;

CREATE TABLE job_board (
    board_id BIGINT NOT NULL AUTO_INCREMENT,
    member_id VARCHAR(30) NOT NULL,
    category VARCHAR(50) NOT NULL CHECK (category IN ('code', 'project', 'activity', 'recruit')),
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    create_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    hit_count INT DEFAULT 0,
    like_count INT DEFAULT 0,
    original_file_name VARCHAR(200),
    saved_file_name VARCHAR(200),
    deadline TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    limit_number INT DEFAULT 0,
    current_number INT DEFAULT 0,
    reported INT DEFAULT 0,
    PRIMARY KEY (board_id),
    FOREIGN KEY (member_id) REFERENCES member_table(member_id) -- member 테이블의 member_id를 FK로 참조
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

SELECT * FROM job_board;



-- 취업 게시판 게시글 신고 Table

DROP TABLE IF EXISTS job_board_reported;

CREATE TABLE job_board_reported (
    report_id BIGINT NOT NULL AUTO_INCREMENT,
    board_id BIGINT NOT NULL,
    member_id VARCHAR(30) NOT NULL, -- 신고 당한 게시글 작성자 ID
    category VARCHAR(200) NOT NULL CHECK (category IN ( 'IllegalContent', 'ViolentContent', 'PrivacyViolation', 'Spam', 'ETC')),
    reason TEXT,
    report_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (report_id),
    FOREIGN KEY (board_id) REFERENCES job_board(board_id), -- job_board 테이블의 board_id를 FK로 참조
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

SELECT * FROM job_board_reported;



-- 취업 게시판 구인 Table

DROP TABLE IF EXISTS job_board_recruit;

CREATE TABLE job_board_recruit (
    recruit_id BIGINT NOT NULL AUTO_INCREMENT,
    board_id BIGINT NOT NULL,
    member_id VARCHAR(30) NOT NULL,
    member_group VARCHAR(20) NOT NULL,
    member_phone VARCHAR(20),
    member_email VARCHAR(200),
    PRIMARY KEY (recruit_id),
    FOREIGN KEY (board_id) REFERENCES job_board(board_id), -- job_board 테이블의 board_id를 FK로 참조
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

SELECT * FROM job_board_recruit;



-- 취업 게시판 댓글 Table

DROP TABLE IF EXISTS job_board_reply;

CREATE TABLE job_board_reply (
    reply_id BIGINT NOT NULL AUTO_INCREMENT,
    board_id BIGINT NOT NULL,
    parent_reply_id BIGINT,
    member_id VARCHAR(30) NOT NULL,
    content VARCHAR(200) NOT NULL,
    create_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    like_count INT DEFAULT 0,
    PRIMARY KEY (reply_id),
    FOREIGN KEY (board_id) REFERENCES job_board(board_id), -- job_board 테이블의 board_id를 FK로 참조
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

SELECT * FROM job_board_reply;


