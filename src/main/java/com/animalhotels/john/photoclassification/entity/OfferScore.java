package com.animalhotels.john.photoclassification.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
public class OfferScore {
    @Id
    private Long offerId;

    @Column
    private Boolean mainPhotoHasFace;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    public static OfferScore create(Long offerId, boolean hasFace) {
        OfferScore score = new OfferScore();
        score.setOfferId(offerId);
        score.setMainPhotoHasFace(hasFace);

        return score;
    }

    public Long getOfferId() {
        return offerId;
    }

    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }

    public Boolean getMainPhotoHasFace() {
        return mainPhotoHasFace;
    }

    public void setMainPhotoHasFace(Boolean mainPhotoHasFace) {
        this.mainPhotoHasFace = mainPhotoHasFace;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
