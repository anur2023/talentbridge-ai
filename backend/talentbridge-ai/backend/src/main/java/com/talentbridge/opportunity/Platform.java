package com.talentbridge.opportunity;

import com.talentbridge.common.BaseEntity;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "platforms")
public class Platform extends BaseEntity {

    @Id
    private String id;
    private String name;
    private String website;
    private String officialTermsUrl;
    private ReferralProgrammeStatus referralProgrammeStatus = ReferralProgrammeStatus.NOT_REVIEWED;
    private LocalDate lastTermsReviewDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getOfficialTermsUrl() {
        return officialTermsUrl;
    }

    public void setOfficialTermsUrl(String officialTermsUrl) {
        this.officialTermsUrl = officialTermsUrl;
    }

    public ReferralProgrammeStatus getReferralProgrammeStatus() {
        return referralProgrammeStatus;
    }

    public void setReferralProgrammeStatus(ReferralProgrammeStatus referralProgrammeStatus) {
        this.referralProgrammeStatus = referralProgrammeStatus;
    }

    public LocalDate getLastTermsReviewDate() {
        return lastTermsReviewDate;
    }

    public void setLastTermsReviewDate(LocalDate lastTermsReviewDate) {
        this.lastTermsReviewDate = lastTermsReviewDate;
    }
}
