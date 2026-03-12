package com.blog.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
public class GeminiServiceTest {

    @Autowired
    private GeminiService geminiService;

    @MockBean
    private ApiLogService apiLogService;

    @Test
    public void testShortTripPrompt() {
        // 3 days trip
        String prompt = geminiService.buildPrompt("大阪", "2026-04-01", "2026-04-03", 2, 0, "観光", "友達", "帝国ホテル", "大阪府");

        // Should contain hotel info
        assertTrue(prompt.contains("帝国ホテル"));

        // Should NOT contain day-trip instructions for nearby cities (since it's < 5
        // days)
        assertFalse(prompt.contains("周辺の都市"));

        // Should contain strict hotel start/end instructions
        assertTrue(prompt.contains("最初の項目(出発)と最後の項目(帰着)"));
    }

    @Test
    public void testLongTripPrompt() {
        // 7 days trip
        String prompt = geminiService.buildPrompt("大阪", "2026-04-01", "2026-04-07", 2, 0, "観光", "友達", "제국호텔", "오사카부");

        // Should contain hotel info
        assertTrue(prompt.contains("제국호텔"));

        // Should CONTAIN day-trip instructions (since it's >= 5 days)
        assertTrue(prompt.contains("周辺の都市"));
        assertTrue(prompt.contains("京都、神戸、奈良"));

        // Should contain strict hotel start/end instructions
        assertTrue(prompt.contains("最初の項目(出発)と最後の項目(帰着)"));
    }
}
