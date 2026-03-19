package com.blog.config;

import com.blog.dto.PlaceDto;
import com.blog.service.PlaceService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * アプリ起動時のシードデータを管理します。
 * - 観光地が一つもない場合はシードデータを挿入
 * - 既存データがある場合は画像URLのみローカルパスにアップデート
 */
@Component
@Order(1)
public class DataLoader implements ApplicationRunner {

    private final PlaceService placeService;

    public DataLoader(PlaceService placeService) {
        this.placeService = placeService;
    }

    // 名前→ローカル画像パス制御
    private static final Map<String, String> IMAGE_MAP = Map.ofEntries(
            Map.entry("浅草寺", "sensoji"),
            Map.entry("渋谷スクランブル交差点", "shibuya"),
            Map.entry("築地場外市場", "tsukiji"),
            Map.entry("大阪城公園", "osaka_castle"),
            Map.entry("道頓堀", "dotonbori"),
            Map.entry("通天閣", "tsutenkaku"),
            Map.entry("伏見稲荷大社", "fushimi_inari"),
            Map.entry("清水寺", "kiyomizu"),
            Map.entry("祇園 花見小路", "gion"),
            Map.entry("太宰府天満宮", "dazaifu"),
            Map.entry("屋台 中洲", "nakasu_yatai"),
            Map.entry("博多ラーメン 一蘭本社", "ichiran"));

    @Override
    public void run(ApplicationArguments args) {
        List<PlaceDto> existing = placeService.getAllPlaces();

        if (existing.isEmpty()) {
            // データ不在時シード挿入
            List<PlaceDto> seeds = List.of(
                    place("浅草寺", "観光", "東京", "東京を代表するお寺。雷門と仲見世通りが人気。", "東京都台東区浅草1-3-1", resolvePath("sensoji")),
                    place("渋谷スクランブル交差点", "観光", "東京", "世界有数の歩行者量を誇る交差点。", "東京都渋谷区道玄坂2-1", resolvePath("shibuya")),
                    place("築地場外市場", "グルメ", "東京", "新鮮な海の幸と食べ歩きが楽しめる市場。", "東京都中央区築地4-10-16", resolvePath("tsukiji")),
                    place("大阪城公園", "観光", "大阪", "大阪のシンボル。桜の名所としても有名。", "大阪府大阪市中央区大阪城1-1", resolvePath("osaka_castle")),
                    place("道頓堀", "グルメ", "大阪", "看板が立ち並ぶ食の街。たこ焼き・串カツが名物。", "大阪府大阪市中央区道頓堀1-9", resolvePath("dotonbori")),
                    place("通天閣", "観光", "大阪", "新世界のシンボルタワー。ビルケンの足をなでると幸運が。", "大阪府大阪市浪速区恵美須東1-18-6",
                            resolvePath("tsutenkaku")),
                    place("伏見稲荷大社", "観光", "京都", "千本鳥居で知られる日本有数の神社。", "京都府京都市伏見区深草藪之内町68", resolvePath("fushimi_inari")),
                    place("清水寺", "観光", "京都", "清水の舞台で知られる世界遺産。", "京都府京都市東山区清水1-294", resolvePath("kiyomizu")),
                    place("祇園 花見小路", "観光", "京都", "風情ある石畳とお茶屋が並ぶ花街。", "京都府京都市東山区祇園町南側", resolvePath("gion")),
                    place("太宰府天満宮", "観光", "福岡", "学問の神様・菅原道真を祀る神社。", "福岡県太宰府市宰府4-7-1", resolvePath("dazaifu")),
                    place("屋台 中洲", "グルメ", "福岡", "ラーメンやおでんなど福岡の屋台グルメ。", "福岡県福岡市博多区中洲", resolvePath("nakasu_yatai")),
                    place("博多ラーメン 一蘭本社", "グルメ", "福岡", "個室スタイルで知られる博多とんこつラーメン。", "福岡県福岡市博多区中洲5-3-2",
                            resolvePath("ichiran")));
            for (PlaceDto p : seeds) {
                placeService.savePlace(p);
            }
        } else {
            // 既存データ存在時画像URLアップデート
            for (PlaceDto p : existing) {
                String baseName = IMAGE_MAP.get(p.getName());
                if (baseName != null) {
                    String newPath = resolvePath(baseName);
                    // 既存パス非一至時アップデート
                    if (!newPath.equals(p.getImageUrl())) {
                        p.setImageUrl(newPath);
                        placeService.savePlace(p);
                    }
                }
            }
        }
    }

    private static PlaceDto place(String name, String category, String region, String description, String address,
            String imageUrl) {
        PlaceDto dto = new PlaceDto();
        dto.setName(name);
        dto.setCategory(category);
        dto.setRegion(region);
        dto.setDescription(description);
        dto.setAddress(address);
        dto.setImageUrl(imageUrl);
        return dto;
    }

    private String resolvePath(String baseName) {
        String[] extensions = { ".png", ".jpg", ".jpeg", ".svg", ".gif" };
        for (String ext : extensions) {
            // 画像存在確認
            if (new org.springframework.core.io.ClassPathResource("static/images/" + baseName + ext).exists()) {
                return "/images/" + baseName + ext;
            }
        }
        // デフォルト: png設定
        return "/images/" + baseName + ".png";
    }
}
