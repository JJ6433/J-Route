package com.blog.controller.admin;

import com.blog.dto.PlaceDto;
import com.blog.service.PlaceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * 관리자 여행지 CRUD
 * 목록, 등록/수정 폼, 이미지 업로드
 */
@Controller
@RequestMapping("/admin/place")
public class AdminPlaceController {

    private final PlaceService placeService;

    // WebConfig와 맞추기 위해 "upload" (슬래시 뺌) 로 변경
    private static final String UPLOAD_DIR = "upload";

    public AdminPlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @GetMapping("/list")
    public String list(Model model) {
        List<PlaceDto> places = placeService.getAllPlaces();
        model.addAttribute("places", places);
        return "admin/place/list";
    }

    @GetMapping("/edit")
    public String editForm(@RequestParam(value = "id", required = false) Long id, Model model) {
        if (id != null) {
            PlaceDto place = placeService.getById(id);
            if (place != null) {
                model.addAttribute("place", place);
                return "admin/place/edit";
            }
        }
        model.addAttribute("place", new PlaceDto());
        return "admin/place/edit";
    }

    @PostMapping("/save")
    public String save(@RequestParam(value = "placeId", required = false) Long placeId,
            @RequestParam("name") String name,
            @RequestParam("category") String category,
            @RequestParam("region") String region,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttributes) {
        PlaceDto dto = new PlaceDto();
        dto.setPlaceId(placeId);
        dto.setName(name);
        dto.setCategory(category);
        dto.setRegion(region);
        dto.setDescription(description);
        dto.setAddress(address);

        if (imageFile != null && !imageFile.isEmpty()) {
            String savedPath = saveUploadFile(imageFile);
            if (savedPath != null) {
                // DB에는 "/upload/파일명.jpg" 형태로 예쁘게 저장됨
                dto.setImageUrl("/" + UPLOAD_DIR + "/" + savedPath);
            }
        } else if (placeId != null) {
            PlaceDto existing = placeService.getById(placeId);
            if (existing != null && existing.getImageUrl() != null) {
                dto.setImageUrl(existing.getImageUrl());
            }
        }

        placeService.savePlace(dto);
        redirectAttributes.addFlashAttribute("message", placeId == null ? "登録しました。" : "更新しました。");
        return "redirect:/admin/place/list";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        placeService.deletePlace(id);
        redirectAttributes.addFlashAttribute("message", "削除しました。");
        return "redirect:/admin/place/list";
    }

    private String saveUploadFile(MultipartFile file) {
        String ext = file.getOriginalFilename() != null && file.getOriginalFilename().contains(".")
                ? file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."))
                : "";
        String filename = UUID.randomUUID() + ext;
        
        // ⭐️ 핵심 수정 부분: 절대 경로로 멱살(?) 잡고 정확한 위치에 저장시킴
        String projectPath = System.getProperty("user.dir") + "/" + UPLOAD_DIR;
        File dir = new File(projectPath);
        
        if (!dir.exists()) {
            dir.mkdirs(); // 폴더 없으면 생성
        }
        
        File target = new File(dir, filename);
        try {
            file.transferTo(target); // 진짜로 파일 복사
            return filename;
        } catch (IOException e) {
            e.printStackTrace(); // 혹시라도 에러나면 콘솔에 빨간 글씨로 띄워줌
            return null;
        }
    }
}