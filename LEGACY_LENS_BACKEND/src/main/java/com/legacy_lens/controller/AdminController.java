package com.legacy_lens.controller;

import com.legacy_lens.dto.response.PagedResponseDto;
import com.legacy_lens.dto.response.UserResponseDto;
import com.legacy_lens.enums.Role;
import com.legacy_lens.repository.UserRepository;
import com.legacy_lens.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("legacylens/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final UserRepository userRepository;

    //Get all users...
    @GetMapping
    public ResponseEntity<PagedResponseDto<UserResponseDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search
    ) {
        return ResponseEntity.ok(userService.getAllUsers(page, size, search));
    }

    //Export all users data in CSV format...
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportUsers(
            @RequestParam(required = false) String search
    ) {
        byte[] csv = userService.exportUsersCsv(search);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"users_export.csv\"")
                .body(csv);
    }

    //Admin dashboard stats
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getAdminStats() {

        long totalUsers = userRepository.countByRoleAndDeletedAtIsNull(Role.USER);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("activeSessions", 0);
        stats.put("reposIndexed", 0);
        stats.put("errorRate", 0.0);

        return ResponseEntity.ok(stats);
    }
}