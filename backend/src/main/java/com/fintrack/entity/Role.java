package com.fintrack.entity;

/**
 * A user's role. Used for role-based authorization.
 * Spring Security will see these as "ROLE_USER" / "ROLE_ADMIN".
 */
public enum Role {
    USER,
    ADMIN
}
