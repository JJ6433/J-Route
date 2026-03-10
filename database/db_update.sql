-- "숙소나 항공권 찜할 때는 place_id를 비워둬도(NULL) 에러 내지 마!" 라고 허락해주는 쿼리
ALTER TABLE wishlists MODIFY COLUMN place_id BIGINT NULL;

SET SQL_SAFE_UPDATES = 1;
COMMIT;