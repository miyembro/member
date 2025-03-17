package com.rjproj.memberapp.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rjproj.memberapp.dto.GoogleInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class GoogleService {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    private final String redirectUri = "http://localhost:4200";

    public GoogleInfo getGoogleInfo(String googleCode) {
        try {
            HttpTransport transport = new NetHttpTransport();
            JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

            GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                    transport, jsonFactory, "https://oauth2.googleapis.com/token", googleClientId, googleClientSecret, googleCode, redirectUri
            ).execute();

            String accessToken = tokenResponse.getAccessToken();

            String userInfoUrl = "https://www.googleapis.com/oauth2/v3/userinfo";
            HttpRequestFactory requestFactory = transport.createRequestFactory();
            com.google.api.client.http.HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(userInfoUrl));
            request.getHeaders().setAuthorization("Bearer " + accessToken);

            HttpResponse response = request.execute();

            String userInfoJson = response.parseAsString();

            JsonObject userInfoObject = JsonParser.parseString(userInfoJson).getAsJsonObject();
            String name = getInfoValue(userInfoObject, "name");
            String email = getInfoValue(userInfoObject, "email");
            String photoUrl = getInfoValue(userInfoObject, "picture");
            String firstName = getInfoValue(userInfoObject, "given_name");
            String lastName = getInfoValue(userInfoObject, "family_name");

            String peopleApiUrl = "https://people.googleapis.com/v1/people/me?personFields=addresses,birthdays,locations,phoneNumbers";
            HttpRequestFactory peopleRequestFactory = transport.createRequestFactory();
            com.google.api.client.http.HttpRequest peopleRequest = peopleRequestFactory.buildGetRequest(new GenericUrl(peopleApiUrl));
            peopleRequest.getHeaders().setAuthorization("Bearer " + accessToken);

            HttpResponse peopleResponse = peopleRequest.execute();

            String peopleJson = peopleResponse.parseAsString();

            System.out.println("People API Response: " + peopleJson);

            JsonObject peopleInfoObject = JsonParser.parseString(peopleJson).getAsJsonObject();
            LocalDate birthdate = getInfoDateValue(peopleInfoObject, "birthdays");

            String address = getAddressValue(peopleInfoObject);

            return new GoogleInfo(
                    email,
                    firstName,
                    lastName,
                    birthdate,
                    photoUrl
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getAddressValue(JsonObject peopleInfoObject) {
        JsonArray addressesArray = Optional.ofNullable(peopleInfoObject.getAsJsonArray("addresses")).orElse(new JsonArray());

        if (addressesArray.size() > 0) {
            JsonObject addressObject = addressesArray.get(0).getAsJsonObject();
            return Optional.ofNullable(addressObject.get("formattedValue"))
                    .map(JsonElement::getAsString)
                    .orElse(null); // Return null if no address is available
        }

        return null;
    }

    private String getInfoValue(JsonObject obj, String key) {
        return Optional.ofNullable(obj.get(key))
                .map(JsonElement::getAsString)
                .orElse(null);
    }

    private LocalDate getInfoDateValue(JsonObject peopleInfoObject, String key) {
        String birthdayString = Optional.ofNullable(peopleInfoObject.getAsJsonArray(key))
                .filter(birthdays -> birthdays.size() > 0)
                .map(birthdays -> birthdays.get(0).getAsJsonObject())
                .map(birthdayObject -> birthdayObject.getAsJsonObject("date"))
                .map(dateObject -> {
                    int year = Optional.ofNullable(dateObject.get("year")).map(JsonElement::getAsInt).orElse(0);
                    int month = Optional.ofNullable(dateObject.get("month")).map(JsonElement::getAsInt).map(m -> m - 1).orElse(0); // Month is 0-based
                    int day = Optional.ofNullable(dateObject.get("day")).map(JsonElement::getAsInt).orElse(0);
                    return (year > 0 && month >= 0 && day > 0) ? String.format("%d-%02d-%02d", year, month + 1, day) : "Unknown";
                })
                .orElse("Unknown");

        if ("Unknown".equals(birthdayString)) {
            return null; // Return null if the birthday is "Unknown"
        }

        try {
            // Parse the string into LocalDate
            return LocalDate.parse(birthdayString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception e) {
            e.printStackTrace(); // Log or handle the exception as necessary
            return null; // Return null if parsing fails
        }
    }

}
