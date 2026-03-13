-- "숙소나 항공권 찜할 때는 place_id를 비워둬도(NULL) 에러 내지 마!" 라고 허락해주는 쿼리
ALTER TABLE wishlists MODIFY COLUMN place_id BIGINT NULL;

SET SQL_SAFE_UPDATES = 1;
COMMIT;

-- reservation 테이블에 5개의 새로운 컬럼을 추가합니다.
ALTER TABLE reservation
ADD COLUMN image_url VARCHAR(500),   -- 사진 링크
ADD COLUMN check_in VARCHAR(100),    -- 체크인 (또는 항공 출발 시간)
ADD COLUMN check_out VARCHAR(100),   -- 체크아웃 (또는 항공 도착 시간)
ADD COLUMN address VARCHAR(255),     -- 숙소 주소 (또는 항공 노선)
ADD COLUMN details VARCHAR(255);     -- 인원 및 객실/좌석 정보