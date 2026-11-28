package com.talentbridge.opportunity.support;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "contactMessages")
public class ContactMessage {
    @Id
    private String id;
    private String name;
    private String email;
    private String message;
    private Instant createdAt = Instant.now();

    public ContactMessage() {
    }

    public ContactMessage(String name, String email, String message) {
        this.name = name;
        this.email = email;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getMessage() {
        return message;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
