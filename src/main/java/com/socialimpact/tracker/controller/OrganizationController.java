package com.socialimpact.tracker.controller;

import com.socialimpact.tracker.entity.Organization;
import com.socialimpact.tracker.repository.OrganizationRepository;
import com.socialimpact.tracker.repository.PositiveNewsRepository;
import com.socialimpact.tracker.repository.DonationRepository;
import com.socialimpact.tracker.repository.EmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrganizationController {
    private final OrganizationRepository orgRepo;
    private final PositiveNewsRepository newsRepo;
    private final DonationRepository donationRepo;
    private final EmissionRepository emissionRepo;

    @GetMapping
    public List<OrganizationDTO> list() {
        return orgRepo.findAll().stream()
                .map(org -> new OrganizationDTO(
                        org.getId(),
                        org.getName(),
                        org.getType(),
                        org.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    /**
     * 뉴스 데이터가 있는 조직만 반환 (성능 최적화)
     */
    @GetMapping("/with-data/news")
    public List<OrganizationDTO> getOrganizationsWithNews() {
        Set<Long> orgIdsWithNews = newsRepo.findAll().stream()
                .filter(n -> n.getOrganization() != null)
                .map(n -> n.getOrganization().getId())
                .collect(Collectors.toSet());
        
        return orgRepo.findAll().stream()
                .filter(org -> orgIdsWithNews.contains(org.getId()))
                .map(org -> new OrganizationDTO(org.getId(), org.getName(), org.getType(), org.getCreatedAt()))
                .collect(Collectors.toList());
    }

    /**
     * 기부금 데이터가 있는 조직만 반환 (성능 최적화)
     */
    @GetMapping("/with-data/donations")
    public List<OrganizationDTO> getOrganizationsWithDonations() {
        Set<Long> orgIdsWithDonations = donationRepo.findAll().stream()
                .filter(d -> d.getOrganization() != null)
                .map(d -> d.getOrganization().getId())
                .collect(Collectors.toSet());
        
        return orgRepo.findAll().stream()
                .filter(org -> orgIdsWithDonations.contains(org.getId()))
                .map(org -> new OrganizationDTO(org.getId(), org.getName(), org.getType(), org.getCreatedAt()))
                .collect(Collectors.toList());
    }

    /**
     * 배출량 데이터가 있는 조직만 반환 (성능 최적화)
     */
    @GetMapping("/with-data/emissions")
    public List<OrganizationDTO> getOrganizationsWithEmissions() {
        Set<Long> orgIdsWithEmissions = emissionRepo.findAll().stream()
                .filter(e -> e.getOrganization() != null)
                .map(e -> e.getOrganization().getId())
                .collect(Collectors.toSet());
        
        return orgRepo.findAll().stream()
                .filter(org -> orgIdsWithEmissions.contains(org.getId()))
                .map(org -> new OrganizationDTO(org.getId(), org.getName(), org.getType(), org.getCreatedAt()))
                .collect(Collectors.toList());
    }

    // DTO 클래스
    record OrganizationDTO(
            Long id,
            String name,
            String type,
            java.time.LocalDateTime createdAt
    ) {}
}