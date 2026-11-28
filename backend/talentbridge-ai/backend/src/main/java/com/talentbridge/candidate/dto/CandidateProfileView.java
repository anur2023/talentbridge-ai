package com.talentbridge.candidate.dto;

import com.talentbridge.candidate.CandidateProfile;
import com.talentbridge.candidate.ExperienceBand;
import java.util.List;

public record CandidateProfileView(
        String displayName,
        String country,
        String city,
        List<String> skills,
        ExperienceBand experienceBand,
        List<String> languages,
        List<String> workPreferences,
        String availability,
        String profileUrl,
        int completionPercent
) {
    public static CandidateProfileView from(CandidateProfile profile) {
        return new CandidateProfileView(profile.getDisplayName(), profile.getCountry(), profile.getCity(),
                profile.getSkills(), profile.getExperienceBand(), profile.getLanguages(),
                profile.getWorkPreferences(), profile.getAvailability(), profile.getProfileUrl(),
                profile.completionPercent());
    }
}
