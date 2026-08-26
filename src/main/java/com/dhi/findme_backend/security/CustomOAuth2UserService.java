package com.dhi.findme_backend.security;

import com.dhi.findme_backend.service.OAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final OAuth2UserService oAuth2UserService;

    public CustomOAuth2UserService(OAuth2UserService oAuth2UserService) {
        this.oAuth2UserService = oAuth2UserService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        
        // Traiter l'utilisateur OAuth2 (créer ou mettre à jour dans la base de données)
        oAuth2UserService.processOAuth2User(oAuth2User, userRequest.getClientRegistration().getRegistrationId());
        
        return oAuth2User;
    }
}
