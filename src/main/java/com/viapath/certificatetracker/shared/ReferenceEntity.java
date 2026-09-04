package com.viapath.certificatetracker.shared;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class ReferenceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Column(nullable = false)
    private boolean active = true;

    protected ReferenceEntity() {}
    protected ReferenceEntity(String displayName) { this.displayName = displayName; }
    public Long getId() { return id; }
    public String getDisplayName() { return displayName; }
    public boolean isActive() { return active; }
    public void deactivate() { active = false; }
}
