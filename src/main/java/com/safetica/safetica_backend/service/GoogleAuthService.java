package com.safetica.safetica_backend.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.safetica.safetica_backend.dto.GoogleUser;

import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class GoogleAuthService {

    public GoogleUser decodeGoogleToken(String idToken) throws Exception {
        // Google ID Token doğrulayıcıyı oluştur
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList("1059572776717-ahjsdm4f2h7qlovekt3moc0ohcdqed9m.apps.googleusercontent.com")) // Google Client ID
                .setClock(com.google.api.client.util.Clock.SYSTEM)
                .build();

        // Token'ı doğrula
        GoogleIdToken googleIdToken = verifier.verify(idToken);

        if (googleIdToken != null) {
            GoogleIdToken.Payload payload = googleIdToken.getPayload();

            // Google ID ve email'i döndür
            GoogleUser googleUser = new GoogleUser();
            googleUser.setGoogleId(payload.getSubject());
            googleUser.setEmail(payload.getEmail());
            return googleUser;
        } else {
            throw new Exception("Invalid Google ID Token");
        }
    }
    
    
}
