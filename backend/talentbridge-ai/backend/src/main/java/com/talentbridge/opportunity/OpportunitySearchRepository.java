package com.talentbridge.opportunity;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.stereotype.Repository;

/**
 * Dynamic public search: builds a Mongo query from optional filters and
 * always restricts to publicly visible listings (FR-PUB-02 to FR-PUB-06).
 */
@Repository
public class OpportunitySearchRepository {

    private final MongoTemplate mongoTemplate;

    public OpportunitySearchRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Page<Opportunity> search(String keyword, OpportunityCategory category, WorkType workType,
                                     Boolean remote, String experienceBand, String country, String language,
                                     Boolean compensationDisclosed, Pageable pageable) {
        List<Criteria> criteria = new java.util.ArrayList<>();
        criteria.add(Criteria.where("status").is(OpportunityStatus.PUBLISHED));
        criteria.add(new Criteria().orOperator(
                Criteria.where("deadline").is(null),
                Criteria.where("deadline").gte(LocalDate.now())));

        if (category != null) criteria.add(Criteria.where("category").is(category));
        if (workType != null) criteria.add(Criteria.where("workType").is(workType));
        if (remote != null) criteria.add(Criteria.where("remote").is(remote));
        if (experienceBand != null && !experienceBand.isBlank()) {
            criteria.add(Criteria.where("experienceBand").is(experienceBand));
        }
        if (country != null && !country.isBlank()) criteria.add(Criteria.where("country").is(country));
        if (language != null && !language.isBlank()) criteria.add(Criteria.where("languages").in(language));
        if (compensationDisclosed != null) {
            criteria.add(Criteria.where("compensationDisclosed").is(compensationDisclosed));
        }

        Query query = new Query();
        if (keyword != null && !keyword.isBlank()) {
            query.addCriteria(TextCriteria.forDefaultLanguage().matchingAny(keyword.split("\\s+")));
        }
        query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[0])));

        long total = mongoTemplate.count(query, Opportunity.class);
        query.with(pageable);
        List<Opportunity> content = mongoTemplate.find(query, Opportunity.class);
        return new org.springframework.data.domain.PageImpl<>(content, pageable, total);
    }
}
