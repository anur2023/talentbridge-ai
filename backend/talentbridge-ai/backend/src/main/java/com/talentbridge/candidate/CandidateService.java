package com.talentbridge.candidate;

import com.talentbridge.candidate.dto.UpdateCandidateProfileRequest;
import com.talentbridge.common.ApiException;
import java.time.Duration;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class CandidateService {

    private static final Duration PRIVACY_REQUEST_SLA = Duration.ofDays(30);

    private final CandidateProfileRepository profileRepository;
    private final SavedOpportunityRepository savedOpportunityRepository;
    private final PrivacyRequestRepository privacyRequestRepository;

    public CandidateService(CandidateProfileRepository profileRepository,
                             SavedOpportunityRepository savedOpportunityRepository,
                             PrivacyRequestRepository privacyRequestRepository) {
        this.profileRepository = profileRepository;
        this.savedOpportunityRepository = savedOpportunityRepository;
        this.privacyRequestRepository = privacyRequestRepository;
    }

    public CandidateProfile getOrCreate(String userId) {
        return profileRepository.findByUserId(userId).orElseGet(() -> {
            CandidateProfile profile = new CandidateProfile();
            profile.setUserId(userId);
            return profileRepository.save(profile);
        });
    }

    public CandidateProfile update(String userId, UpdateCandidateProfileRequest request) {
        CandidateProfile profile = getOrCreate(userId);
        profile.setDisplayName(request.displayName());
        profile.setCountry(request.country());
        profile.setCity(request.city());
        profile.setSkills(request.skills() != null ? request.skills() : java.util.List.of());
        profile.setExperienceBand(request.experienceBand());
        profile.setLanguages(request.languages() != null ? request.languages() : java.util.List.of());
        profile.setWorkPreferences(request.workPreferences() != null ? request.workPreferences() : java.util.List.of());
        profile.setAvailability(request.availability());
        profile.setProfileUrl(request.profileUrl());
        return profileRepository.save(profile);
    }

    public SavedOpportunity save(String userId, String opportunityId) {
        return savedOpportunityRepository.findByUserIdAndOpportunityId(userId, opportunityId)
                .orElseGet(() -> {
                    try {
                        return savedOpportunityRepository.save(new SavedOpportunity(userId, opportunityId));
                    } catch (org.springframework.dao.DuplicateKeyException e) {
                        // Lost a race to save the same listing twice; treat as already saved.
                        return savedOpportunityRepository.findByUserIdAndOpportunityId(userId, opportunityId)
                                .orElseThrow(() -> e);
                    }
                });
    }

    public void unsave(String userId, String opportunityId) {
        savedOpportunityRepository.deleteByUserIdAndOpportunityId(userId, opportunityId);
    }

    public java.util.List<SavedOpportunity> listSaved(String userId) {
        return savedOpportunityRepository.findByUserId(userId);
    }

    public PrivacyRequest fileRequest(String userId, PrivacyRequestType type, String details) {
        PrivacyRequest request = new PrivacyRequest(userId, type, details, Instant.now().plus(PRIVACY_REQUEST_SLA));
        return privacyRequestRepository.save(request);
    }

    public CandidateProfile requireProfile(String userId) {
        return profileRepository.findByUserId(userId)
                .orElseThrow(() -> ApiException.notFound("PROFILE_NOT_FOUND", "Profile not found."));
    }
}
