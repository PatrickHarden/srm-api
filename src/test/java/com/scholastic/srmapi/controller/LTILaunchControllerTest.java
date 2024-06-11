package com.scholastic.srmapi.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.scholastic.srmapi.service.LtiService;
import com.scholastic.srmapi.util.Constants;
import com.scholastic.srmapi.util.JwtTokenProvider;

@WebMvcTest(LTILaunchController.class)
class LTILaunchControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LtiService ltiService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private final String SDM_NAV_CTX_JSON_MOCK = "{\"key\": \"value\"}";

    @Test
    void authenticateUserReturnsUnauthorized() throws Exception {
        when(ltiService.validateLtiRequest(any())).thenReturn(false);

        mockMvc.perform(post(Constants.LTI)).andExpect(status().isUnauthorized());
    }

    @Test
    void authenticateUserRedirectsToRootWithCookie() throws Exception {
        when(ltiService.validateLtiRequest(any())).thenReturn(true);

        mockMvc.perform(post(Constants.LTI)
                .param(Constants.CUSTOM_SDM_NAV_CTX, SDM_NAV_CTX_JSON_MOCK))
                .andExpect(redirectedUrl(Constants.ROOT))
                .andExpect(cookie().exists(Constants.SDM_NAV_CTX))
                .andExpect(cookie().domain(Constants.SDM_NAV_CTX, Constants.COOKIE_DOMAIN_NAME))
                .andExpect(cookie().secure(Constants.SDM_NAV_CTX, true));
    }

    @Test
    void authenticateUserSetsSrmJwtCookie() throws Exception {
        var jwt = "jwt";
        when(ltiService.validateLtiRequest(any())).thenReturn(true);
        when(jwtTokenProvider.generateJwt(anyString(), anyString())).thenReturn(jwt);

        mockMvc.perform(post(Constants.LTI)
                .param(Constants.CUSTOM_SDM_NAV_CTX, SDM_NAV_CTX_JSON_MOCK)
                .param(Constants.USER_ID, "123456"))
                .andExpect(cookie().exists(Constants.LITPLAT_JWT))
                .andExpect(cookie().secure(Constants.LITPLAT_JWT, true))
                .andExpect(cookie().domain(Constants.LITPLAT_JWT, Constants.COOKIE_DOMAIN_NAME))
                .andExpect(cookie().value(Constants.LITPLAT_JWT, jwt));
    }

    @Test
    void authenticateUserRedirectsToRootWhenEmptyRedirectUrl() throws Exception {
        when(ltiService.validateLtiRequest(any())).thenReturn(true);

        mockMvc.perform(post(Constants.LTI)
                .param(Constants.REDIRECT_URL, "")
                .param(Constants.CUSTOM_SDM_NAV_CTX, SDM_NAV_CTX_JSON_MOCK))
                .andExpect(redirectedUrl(Constants.ROOT));
    }

    @Test
    void authenticateUserRedirectsToRedirectUrl() throws Exception {
        var redirectUrl = "/redirect";
        when(ltiService.validateLtiRequest(any())).thenReturn(true);

        mockMvc.perform(post(Constants.LTI)
                .param(Constants.REDIRECT_URL, redirectUrl)
                .param(Constants.CUSTOM_SDM_NAV_CTX, SDM_NAV_CTX_JSON_MOCK))
                .andExpect(redirectedUrl(redirectUrl));
    }
}
