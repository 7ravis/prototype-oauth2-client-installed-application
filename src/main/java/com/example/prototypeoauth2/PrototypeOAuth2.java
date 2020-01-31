package com.example.prototypeoauth2;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

/**
 * Prototype for obtaining an OAuth2 authorization code from a desktop
 * application. This requires an OAuth2 client id but not the client secret. Be
 * sure to update the client id field below, or provide it as a program argument
 * using the key below.
 */
public class PrototypeOAuth2 {

    private static final String CLIENT_ID_ARG_KEY = "clientid=";
    private static String oauth2ClientId = "";

    public static void main(String... args) {
	for (String arg : args) {
	    if (arg.startsWith(CLIENT_ID_ARG_KEY))
		oauth2ClientId = arg.replaceFirst(CLIENT_ID_ARG_KEY, "");
	}

	VerificationCodeReceiver receiver = null;
	try {

	    // 1. create receiver that will provide the redirect url and listen for the auth
	    // code sent there as a query parameter
	    receiver = new LocalServerReceiver();
	    String redirectUri = receiver.getRedirectUri();

	    // 2. create url (with necessary query parameters) for requesting the
	    // authorization code
	    List<String> scopes = Arrays.asList("openid", "email");
	    JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
	    String clientSecret = null;
	    HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
	    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow(transport, jsonFactory, oauth2ClientId,
		    clientSecret, scopes);
	    GoogleAuthorizationCodeRequestUrl authUrlBuilder = flow.newAuthorizationUrl().setRedirectUri(redirectUri);
	    String authCodeUrl = authUrlBuilder.build();

	    // 3. open the url in a browser for the user to authenticate (sign in)
	    Desktop desktop = Desktop.getDesktop();
	    desktop.browse(URI.create(authCodeUrl));

	    // 4. listen for authorization code
	    String code = receiver.waitForCode();
	    System.out.println("Authorization code = " + code);
	    System.out.println("Redirect URI = " + redirectUri);

	} catch (Exception e) {
	    System.err.println(e.getMessage());
	    e.printStackTrace();
	} finally {
	    try {
		receiver.stop();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
    }

}
