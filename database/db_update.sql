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


-- wishlists 테이블에 price 컬럼을 추가합니다. 기본값은 0입니다. 
-- >> 찜목록에 저장된 항공권과 숙소의 가격 정보를 저장하여 예산에서 불러오기 위함입니다.
ALTER TABLE wishlists ADD COLUMN price INT DEFAULT 0;

-- 사용자가 짠 예산을 [내 예산 저장하기] 버튼을 눌러 DB(BudgetDto)에 저장해 두고, 
-- 마이페이지나 예산 탭에 다시 들어왔을 때 그대로 불러와 주는 기능을 위한 budgets 테이블입니다.
CREATE TABLE budgets (
    budget_id BIGINT AUTO_INCREMENT PRIMARY KEY, -- 예산 고유 번호
    user_id BIGINT NOT NULL,                     -- 예산 주인(유저) 번호
    transport_cost INT DEFAULT 0,                -- ✈️ 항공/교통비
    hotel_cost INT DEFAULT 0,                    -- 🏨 숙박비
    food_cost INT DEFAULT 0,                     -- 🍱 식비(맛집)
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 마지막 수정 시간
    
    -- (선택) user_id가 기존 users 테이블을 참조하도록 외래키 설정
    -- FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    
    -- 유저 한 명당 예산표를 하나만 가지게 하려면 아래 제약조건을 추가합니다.
    UNIQUE KEY unique_user_budget (user_id)
);