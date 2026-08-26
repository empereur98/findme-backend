package com.dhi.findme_backend.security;

import java.util.UUID;

public interface SecurityUtilsInterface {
    UUID getCurrentUserId();
    String getCurrentUserEmail();
    boolean isAdmin();
}
