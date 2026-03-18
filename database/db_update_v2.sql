-- wishlists 테이블의 api_id 컬럼 길이를 늘립니다. (항공권 URL 등이 길어지는 경우 대비)
ALTER TABLE wishlists MODIFY COLUMN api_id VARCHAR(1000);

-- item_name과 image_url도 혹시 모르니 넉넉하게 늘려줍니다.
ALTER TABLE wishlists MODIFY COLUMN item_name VARCHAR(500);
ALTER TABLE wishlists MODIFY COLUMN image_url VARCHAR(1000);

COMMIT;
