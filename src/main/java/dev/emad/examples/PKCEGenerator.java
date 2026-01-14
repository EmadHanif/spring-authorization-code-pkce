package dev.emad.examples;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * @author EmadHanif
 */
public class PKCEGenerator {

  private static final SecureRandom SECURE_RANDOM = new SecureRandom();
  private static final int CODE_VERIFIER_LENGTH = 128;
  private static final String CHARACTERS =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~";

  private static final String AUTHORIZE_URL = "http://localhost:8080/oauth2/authorize";
  private static final String TOKEN_URL = "http://localhost:8080/oauth2/token";

  private static String generateCodeVerifier() {

    StringBuilder codeVerifier = new StringBuilder();

    for (int i = 0; i < CODE_VERIFIER_LENGTH; i++) {
      codeVerifier.append(CHARACTERS.charAt(SECURE_RANDOM.nextInt(CHARACTERS.length())));
    }

    return codeVerifier.toString();
  }

  private static String generateCodeChallenge(String codeVerifier) {
    try {
      byte[] bytes = codeVerifier.getBytes();
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] digest = md.digest(bytes);

      // Base64 URL-safe encoding without padding...
      return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static String encodeBasicString(String clientId, String secret) {
    return Base64.getEncoder()
        .encodeToString((clientId.concat(":").concat(secret).getBytes(StandardCharsets.UTF_8)));
  }

  // Inner Class
  private static class PKCEPair {
    private final String clientId;
    private final String secret;
    private final String scope;
    private final String redirectUri;
    private final String codeVerifier;
    private final String codeChallenge;

    public PKCEPair(String clientId, String secret, String scope, String redirectUri) {
      this.clientId = clientId;
      this.secret = secret;
      this.scope = URLEncoder.encode(scope, StandardCharsets.UTF_8);
      this.redirectUri = redirectUri;
      this.codeVerifier = generateCodeVerifier();
      this.codeChallenge = generateCodeChallenge(this.codeVerifier);
    }
  }

  public static void main(String[] args) {

    PKCEPair pkcePair =
        new PKCEPair("client", "secret", "openid user", "http://localhost:4200/callback");

    System.out.println("=== PKCE Values ===");
    System.out.println("Code Verifier: " + pkcePair.codeVerifier);
    System.out.println("Code Challenge: " + pkcePair.codeChallenge);

    // Use in /oauth2/authorize (Authorization Endpoint)
    System.out.println("=== Authorization Request ===");
    System.out.printf(
        "%s?"
            + "response_type=code&client_id=%s&redirect_uri=%s&scope=%s&code_challenge=%s&code_challenge_method=S256%n",
        AUTHORIZE_URL,
        pkcePair.clientId,
        pkcePair.redirectUri,
        pkcePair.scope,
        pkcePair.codeChallenge);

    // Use in OAuth2 (/oauth2/token) Token Endpoint
    System.out.println("=== Token Request ===");
    System.out.println("curl --request POST \\");
    System.out.println("  --url " + TOKEN_URL + " \\");
    System.out.println("--header 'content-type: application/x-www-form-urlencoded' \\");
    System.out.println(
        "--header 'authorization: Basic "
            + encodeBasicString(pkcePair.clientId, pkcePair.secret)
            + "'\\");
    System.out.println("--data 'grant_type=authorization_code' \\");
    System.out.println("--data 'code=YOUR_AUTH_CODE' \\");
    System.out.println("--data 'code_verifier=" + pkcePair.codeVerifier + "' \\");
    System.out.println("--data 'redirect_uri=" + pkcePair.redirectUri + "'");
    System.out.println();
  }
}
