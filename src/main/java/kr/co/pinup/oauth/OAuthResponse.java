package kr.co.pinup.oauth;

import kr.co.pinup.oauth.OAuthProvider;

public interface OAuthResponse {
    String getId();
    String getName();
    String getEmail();
    OAuthProvider getOAuthProvider();
}
