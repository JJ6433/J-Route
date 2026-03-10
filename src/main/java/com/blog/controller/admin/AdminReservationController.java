package com.blog.controller.admin;

import com.blog.dto.ReservationDto;
import com.blog.service.ReservationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/reservation")
public class AdminReservationController {

    private final ReservationService reservationService;

    public AdminReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping({"", "/"})
    public String list(Model model) {
        List<ReservationDto> reservations = reservationService.getAllReservationsForAdmin();
        model.addAttribute("reservations", reservations);
        return "admin/reservations";
    }
}
