package com.legacy_lens.service.impl;

import com.legacy_lens.dto.response.PagedResponseDto;
import com.legacy_lens.dto.response.UserResponseDto;
import com.legacy_lens.entity.User;
import com.legacy_lens.enums.Role;
import com.legacy_lens.repository.UserRepository;
import com.legacy_lens.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private static final DateTimeFormatter CSV_DATE =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public PagedResponseDto<UserResponseDto> getAllUsers(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        var userPage = userRepository.findAllByRoleAndNotDeleted(
                Role.USER,
                (search != null && !search.isBlank()) ? search.trim() : null,
                pageable
        );

        var content = userPage.getContent().stream()
                .map(user -> UserResponseDto.builder()
                        .publicId(user.getPublicId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .status(user.getStatus())
                        .isVerified(user.isVerified())
                        .createdAt(user.getCreatedAt())
                        .build())
                .toList();

        return PagedResponseDto.<UserResponseDto>builder()
                .content(content)
                .page(userPage.getNumber())
                .size(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .last(userPage.isLast())
                .build();
    }

    public byte[] exportUsersCsv(String search) {
        List<User> users = userRepository.findAllByRoleAndNotDeletedUnpaged(
                Role.USER,
                normaliseSearch(search)
        );

        StringBuilder csv = new StringBuilder();
        csv.append("Public ID,Name,Email,Status,Verified,Joined\n");

        for (User u : users) {
            csv.append(escapeCsv(u.getPublicId())).append(',')
                    .append(escapeCsv(u.getName())).append(',')
                    .append(escapeCsv(u.getEmail())).append(',')
                    .append(u.getStatus().name()).append(',')
                    .append(u.isVerified() ? "Yes" : "No").append(',')
                    .append(u.getCreatedAt() != null ? u.getCreatedAt().format(CSV_DATE) : "")
                    .append('\n');
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    // Helpers

    private UserResponseDto toDto(User user) {
        return UserResponseDto.builder()
                .publicId(user.getPublicId())
                .name(user.getName())
                .email(user.getEmail())
                .status(user.getStatus())
                .isVerified(user.isVerified())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private String normaliseSearch(String search) {
        return (search != null && !search.isBlank()) ? search.trim() : null;
    }

}