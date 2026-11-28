package com.talentbridge.candidate.dto;

import com.talentbridge.candidate.ExperienceBand;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

public record UpdateCandidateProfileRequest(
        @Size(max = 120) String displayName,
        @Size(max = 80) String country,
        @Size(max = 80) String city,
        List<@Size(max = 40) String> skills,
        ExperienceBand experienceBand,
        List<@Size(max = 40) String> languages,
        List<@Size(max = 40) String> workPreferences,
        @Size(max = 120) String availability,
        @Pattern(regexp = "^(https://.*)?$", message = "profileUrl must be a secure (https) link") String profileUrl
) {
}
