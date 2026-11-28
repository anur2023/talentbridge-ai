package com.talentbridge.opportunity;

import com.talentbridge.common.BaseEntity;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.TextIndexed;

@Document(collection = "opportunities")
public class Opportunity extends BaseEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private String slug;

    @TextIndexed(weight = 3)
    private String title;

    @TextIndexed
    private String description;

    @TextIndexed
    private List<String> skills = new ArrayList<>();

    private OpportunityCategory category;
    private WorkType workType;
    private boolean remote;
    private String experienceBand;
    private String country;
    private List<String> languages = new ArrayList<>();

    private boolean compensationDisclosed;
    private String compensationText;

    private String platformId;
    private ReferralType referralType = ReferralType.PUBLIC_APPLICATION;
    private boolean sponsored;

    private String sourceUrl;
    private String applyUrl;
    private String eligibility;
    private LocalDate deadline;
    private LocalDate verifiedAt;

    @Indexed
    private OpportunityStatus status = OpportunityStatus.DRAFT;

    private String authorUserId;
    private String publishedByUserId;
    private boolean stale;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public OpportunityCategory getCategory() {
        return category;
    }

    public void setCategory(OpportunityCategory category) {
        this.category = category;
    }

    public WorkType getWorkType() {
        return workType;
    }

    public void setWorkType(WorkType workType) {
        this.workType = workType;
    }

    public boolean isRemote() {
        return remote;
    }

    public void setRemote(boolean remote) {
        this.remote = remote;
    }

    public String getExperienceBand() {
        return experienceBand;
    }

    public void setExperienceBand(String experienceBand) {
        this.experienceBand = experienceBand;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public boolean isCompensationDisclosed() {
        return compensationDisclosed;
    }

    public void setCompensationDisclosed(boolean compensationDisclosed) {
        this.compensationDisclosed = compensationDisclosed;
    }

    public String getCompensationText() {
        return compensationText;
    }

    public void setCompensationText(String compensationText) {
        this.compensationText = compensationText;
    }

    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public ReferralType getReferralType() {
        return referralType;
    }

    public void setReferralType(ReferralType referralType) {
        this.referralType = referralType;
    }

    public boolean isSponsored() {
        return sponsored;
    }

    public void setSponsored(boolean sponsored) {
        this.sponsored = sponsored;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getApplyUrl() {
        return applyUrl;
    }

    public void setApplyUrl(String applyUrl) {
        this.applyUrl = applyUrl;
    }

    public String getEligibility() {
        return eligibility;
    }

    public void setEligibility(String eligibility) {
        this.eligibility = eligibility;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public LocalDate getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDate verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public OpportunityStatus getStatus() {
        return status;
    }

    public void setStatus(OpportunityStatus status) {
        this.status = status;
    }

    public String getAuthorUserId() {
        return authorUserId;
    }

    public void setAuthorUserId(String authorUserId) {
        this.authorUserId = authorUserId;
    }

    public String getPublishedByUserId() {
        return publishedByUserId;
    }

    public void setPublishedByUserId(String publishedByUserId) {
        this.publishedByUserId = publishedByUserId;
    }

    public boolean isStale() {
        return stale;
    }

    public void setStale(boolean stale) {
        this.stale = stale;
    }

    public boolean isPubliclyVisible() {
        return status == OpportunityStatus.PUBLISHED
                && (deadline == null || !deadline.isBefore(LocalDate.now()));
    }
}
