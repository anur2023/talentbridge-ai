package com.talentbridge.candidate;

import com.talentbridge.common.BaseEntity;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Deliberately does NOT collect date of birth, home address, or ID numbers
 * (see Day 13 goal / blueprint privacy minimisation).
 */
@Document(collection = "candidateProfiles")
public class CandidateProfile extends BaseEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private String userId;

    private String displayName;
    private String country;
    private String city;
    private List<String> skills = new ArrayList<>();
    private ExperienceBand experienceBand;
    private List<String> languages = new ArrayList<>();
    private List<String> workPreferences = new ArrayList<>();
    private String availability;
    private String profileUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public ExperienceBand getExperienceBand() {
        return experienceBand;
    }

    public void setExperienceBand(ExperienceBand experienceBand) {
        this.experienceBand = experienceBand;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public List<String> getWorkPreferences() {
        return workPreferences;
    }

    public void setWorkPreferences(List<String> workPreferences) {
        this.workPreferences = workPreferences;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    /** Non-coercive completion indicator shown on the dashboard (Day 13). */
    public int completionPercent() {
        int total = 7;
        int filled = 0;
        if (displayName != null && !displayName.isBlank()) filled++;
        if (country != null && !country.isBlank()) filled++;
        if (city != null && !city.isBlank()) filled++;
        if (!skills.isEmpty()) filled++;
        if (experienceBand != null) filled++;
        if (!languages.isEmpty()) filled++;
        if (!workPreferences.isEmpty()) filled++;
        return (int) Math.round((filled / (double) total) * 100);
    }
}
