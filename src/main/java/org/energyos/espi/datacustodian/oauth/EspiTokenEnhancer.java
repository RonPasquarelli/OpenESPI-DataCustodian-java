package org.energyos.espi.datacustodian.oauth;

import org.energyos.espi.common.domain.Authorization;
import org.energyos.espi.common.domain.Routes;
import org.energyos.espi.common.domain.Subscription;
import org.energyos.espi.common.service.AuthorizationService;
import org.energyos.espi.common.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;
import java.util.Map;

public class EspiTokenEnhancer implements TokenEnhancer {

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private AuthorizationService authorizationService;

    private String baseURL;

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {

        DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) accessToken;
        Map<String, Object> additionalInformation = new HashMap<>();

        Subscription subscription = subscriptionService.createSubscription(authentication);
        additionalInformation.put("resourceURI", baseURL + Routes.buildDataCustodianSubscription(subscription.getHashedId()));

        Authorization authorization = authorizationService.createAuthorization(subscription, "accessToken");
        additionalInformation.put("authorizationURI", baseURL + Routes.DATA_CUSTODIAN_AUTHORIZATION.replace("{AuthorizationID}", authorization.getUUID().toString()));

        token.setAdditionalInformation(additionalInformation);

        return token;
    }

    public void setSubscriptionService(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    public void setAuthorizationService(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }
}
