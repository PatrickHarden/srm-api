package com.scholastic.srmapi.config;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
class BaWebClientConfigTest {

    public BaWebClientConfig config = new BaWebClientConfig();
    @Spy
    private static WebClient.Builder spiedWebClientBuilder = Mockito.spy(WebClient.builder());

    @BeforeAll
    static void setUp() {
        MockedStatic<WebClient> webClientStatic = Mockito.mockStatic(WebClient.class);
        webClientStatic.when(WebClient::builder).thenReturn(spiedWebClientBuilder);
    }

    @Test
    void batWebClientCreation_clientSetupCorrect() {
        WebClient client = config.baWebClient();

        verify(spiedWebClientBuilder).defaultHeader("authorization",
                "Bearer " + config.getKongToken());
        verify(spiedWebClientBuilder).baseUrl(config.getBaServiceBaseUrl());
        verify(spiedWebClientBuilder).build();
        assertThat(client).isNotNull();

    }

}
