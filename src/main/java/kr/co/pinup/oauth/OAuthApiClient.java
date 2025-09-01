package kr.co.pinup.oauth;

import kr.co.pinup.oauth.OAuthLoginParams;
import kr.co.pinup.oauth.OAuthProvider;
import kr.co.pinup.oauth.OAuthResponse;
import kr.co.pinup.oauth.OAuthToken;
import org.apache.commons.lang3.tuple.Pair;

public interface OAuthApiClient {
    OAuthProvider oAuthProvider();
    OAuthToken requestAccessToken(OAuthLoginParams params);
    Pair<OAuthResponse, OAuthToken> requestUserInfo(OAuthToken token);
    OAuthResponse isAccessTokenExpired(String accessToken);
    OAuthToken refreshAccessToken(String refreshToken);
    boolean revokeAccessToken(String accessToken);
}
