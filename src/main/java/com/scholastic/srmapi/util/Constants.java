package com.scholastic.srmapi.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {

    // Session Constants
    public static final String LTI = "/lti";
    public static final String ROOT = "/";
    public static final String REDIRECT_URL = "redirectURL";
    public static final String SDM_NAV_CTX = "sdm_nav_ctx";
    public static final String LITPLAT_JWT = "litplat_jwt";
    public static final String CUSTOM_SDM_NAV_CTX = "custom_sdm_nav_ctx";
    public static final String USER_ID = "user_id";
    public static final String COOKIE_DOMAIN_NAME = "scholastic.com";

    // Exception Constants
    public static final String MISSING_SESSION_INFORMATION_MESSAGE = "Missing session information.";

    public static final String NOT_AUTHORIZED_MESSAGE = "Not authorized";
    public static final String NO_CURRENT_ASSESSMENTS_MESSAGE = "No current assessments";

    // Env Constants
    public static final String SHUFFLE_ASSESSMENT_ANSWERS = "shuffleAssessmentAnswers";

    public static final int ZERO = 0;
    public static final int ONE = 1;
    public static final int TWO = 2;
    public static final int THREE = 3;
    public static final String CLAIMS = "claims";

    // Assessment Constants
    public static final String LEXILE_STATUS_CANCELED = "CANCELED";

    // Query Constants

    public static final String TWO_WEEKS_TESTS = " AND DATEDIFF(created_date, NOW()) <= 14 ";

    public static final String ASSESSMENT_CONTROLLER_PATH_PATTERN = "/api/v1/assessment/**";
    public static final String SESSION_ID = "s_id";

}
