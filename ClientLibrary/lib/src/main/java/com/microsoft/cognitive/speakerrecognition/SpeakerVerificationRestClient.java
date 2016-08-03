//
// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license.
//
// Microsoft Cognitive Services (formerly Project Oxford): https://www.microsoft.com/cognitive-services
//
// Microsoft Cognitive Services (formerly Project Oxford) GitHub:
// https://github.com/Microsoft/ProjectOxford-ClientSDK
//
// Copyright (c) Microsoft Corporation
// All rights reserved.
//
// MIT License:
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
//
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED ""AS IS"", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
package com.microsoft.cognitive.speakerrecognition;

import com.microsoft.cognitive.speakerrecognition.contract.CreateProfileException;
import com.microsoft.cognitive.speakerrecognition.contract.DeleteProfileException;
import com.microsoft.cognitive.speakerrecognition.contract.EnrollmentException;
import com.microsoft.cognitive.speakerrecognition.contract.GetProfileException;
import com.microsoft.cognitive.speakerrecognition.contract.ResetEnrollmentsException;
import com.microsoft.cognitive.speakerrecognition.contract.verification.CreateProfileResponse;
import com.microsoft.cognitive.speakerrecognition.contract.verification.Enrollment;
import com.microsoft.cognitive.speakerrecognition.contract.verification.PhrasesException;
import com.microsoft.cognitive.speakerrecognition.contract.verification.Profile;
import com.microsoft.cognitive.speakerrecognition.contract.verification.Verification;
import com.microsoft.cognitive.speakerrecognition.contract.verification.VerificationException;
import com.microsoft.cognitive.speakerrecognition.contract.verification.VerificationPhrase;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

/**
 * A service client class to perform all the verification service calls
 */
public class SpeakerVerificationRestClient implements SpeakerVerificationClient {

    /**
     * Address of the verification profiles API
     */
    private static final String BASE_URI = "https://api.projectoxford.ai/spid/v1.0/verificationProfiles";

    /**
     * Address of the verification API
     */
    private static final String VERIFY_ENDPOINT = "https://api.projectoxford.ai/spid/v1.0/verify";

    /**
     * Address of the verification phrases API
     */
    private static final String PHRASES_ENDPOINT = "https://api.projectoxford.ai/spid/v1.0/verificationPhrases?locale=";
    
    /**
     * The locale parameter
     */
    private static final String LOCALE_PARAM = "locale";

    /**
     * The Http client used to communicate with the service
     */
    private HttpClient defaultHttpClient;

    /**
     * Json Serializer / deserializer
     */
    private Gson gson;

    /**
     * Speaker client clientHelper
     */
    private SpeakerRestClientHelper clientHelper;
    
    //----------------------------------------------------------------------------------------------

    /**
     * Creates a new service client using a subscription key
     *
     * @param subscriptionKey The subscription key
     */
    public SpeakerVerificationRestClient(String subscriptionKey) {
        defaultHttpClient = new DefaultHttpClient();
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:SS.SSS").create();
        clientHelper = new SpeakerRestClientHelper(subscriptionKey);
    }

    //----------------------------------------------------------------------------------------------

    /**
     * Creates a new speaker profile
     *
     * @param locale The speaker profile locale
     * @return The Profile object encapsulating the speaker profile response
     * @throws CreateProfileException Thrown in case of internal server error or an invalid locale
     * @throws IOException Signals an invalid locale encoding, a connection abortion, or an invalid response content
     */
    @Override
    public CreateProfileResponse createProfile(String locale) throws CreateProfileException, IOException {

        HttpPost request = (HttpPost) clientHelper.createHttpRequest(BASE_URI, RequestType.POST);

        List<NameValuePair> paramsList = new ArrayList<>();
        paramsList.add(new BasicNameValuePair(LOCALE_PARAM, locale));
        request.setEntity(new UrlEncodedFormEntity(paramsList));

        HttpResponse response = defaultHttpClient.execute(request);

        int statusCode = clientHelper.getStatusCode(response);
        String stringResponse = clientHelper.httpResponseToString(response);

        if (statusCode == HttpStatus.SC_OK) {
            return gson.fromJson(stringResponse, CreateProfileResponse.class);
        } else {
            ErrorResponse errorResponse = gson.fromJson(stringResponse, ErrorResponse.class);
            if (errorResponse != null) {
                throw new CreateProfileException(errorResponse.error.message);
            } else {
                throw new CreateProfileException(String.valueOf(statusCode));
            }
        }
    }

    /**
     * Retrieves a given speaker profile as specified by the id parameter
     *
     * @param id The speaker profile ID
     * @return The requested speaker profile
     * @throws GetProfileException Thrown in case of internal server error or an invalid ID
     * @throws IOException Signals a connection abortion, or an invalid response content
     */
    @Override
    public Profile getProfile(UUID id) throws GetProfileException, IOException {

        HttpGet request = (HttpGet) clientHelper.createHttpRequest(BASE_URI + "/" + id.toString(), RequestType.GET);

        HttpResponse response = defaultHttpClient.execute(request);

        int statusCode = clientHelper.getStatusCode(response);
        String stringResponse = clientHelper.httpResponseToString(response);

        if (statusCode == HttpStatus.SC_OK) {
            return gson.fromJson(stringResponse, Profile.class);
        } else {
            ErrorResponse errorResponse = gson.fromJson(stringResponse, ErrorResponse.class);
            if (errorResponse != null) {
                throw new GetProfileException(errorResponse.error.message);
            } else {
                throw new GetProfileException(String.valueOf(statusCode));
            }
        }
    }

    /**
     * Retrieves all available speaker profiles
     *
     * @return A list of speaker profiles
     * @throws GetProfileException Thrown in case of internal server error or an invalid ID
     * @throws IOException Signals a connection abortion, or an invalid response content
     */
    @Override
    public List<Profile> getProfiles() throws GetProfileException, IOException {

        HttpGet request = (HttpGet) clientHelper.createHttpRequest(BASE_URI, RequestType.GET);

        HttpResponse response = defaultHttpClient.execute(request);

        int statusCode = clientHelper.getStatusCode(response);
        String stringResponse = clientHelper.httpResponseToString(response);

        if (statusCode == HttpStatus.SC_OK) {
            Type listType = new TypeToken<List<Profile>>() {}.getType();
            return gson.fromJson(stringResponse, listType);
        } else {
            ErrorResponse errorResponse = gson.fromJson(stringResponse, ErrorResponse.class);
            if (errorResponse != null) {
                throw new GetProfileException(errorResponse.error.message);
            } else {
                throw new GetProfileException(String.valueOf(statusCode));
            }
        }
    }

    /**
     * Deletes a given speaker profile
     *
     * @param id The ID of the speaker profile to be deleted
     * @throws DeleteProfileException Thrown in case of internal server error, an invalid ID or failure to delete the profile
     * @throws IOException Signals a connection abortion, or an invalid response content
     */
    @Override
    public void deleteProfile(UUID id) throws DeleteProfileException, IOException {

        HttpDelete request = (HttpDelete) clientHelper.createHttpRequest(BASE_URI + "/" + id.toString(), RequestType.DELETE);

        HttpResponse response = defaultHttpClient.execute(request);

        int statusCode = clientHelper.getStatusCode(response);
        String stringResponse = clientHelper.httpResponseToString(response);

        if (statusCode != HttpStatus.SC_OK) {
            ErrorResponse errorResponse = gson.fromJson(stringResponse, ErrorResponse.class);
            if (errorResponse != null) {
                throw new DeleteProfileException(errorResponse.error.message);
            } else {
                throw new DeleteProfileException(String.valueOf(statusCode));
            }
        }
    }

    /**
     * Gets a list of all available phrases for enrollments
     *
     * @param locale The locale of the phrases
     * @return A list of all verification phrases
     * @throws PhrasesException Thrown in case of invalid locale or internal server error
     * @throws IOException Signals an invalid locale encoding, a connection abortion, or an invalid response content
     * @throws URISyntaxException Signals that the endpoint string could not be parsed as a URI reference
     */
    @Override
    public List<VerificationPhrase> getPhrases(String locale) throws PhrasesException, IOException, URISyntaxException {

        URL url = new URL(PHRASES_ENDPOINT + locale);
        String requestURI = url.toURI().toString();
        HttpGet request = (HttpGet) clientHelper.createHttpRequest(requestURI, RequestType.GET);

        HttpResponse response = defaultHttpClient.execute(request);

        int statusCode = clientHelper.getStatusCode(response);
        String stringResponse = clientHelper.httpResponseToString(response);

        if (statusCode == HttpStatus.SC_OK) {
            Type listType = new TypeToken<List<VerificationPhrase>>() {}.getType();
            return gson.fromJson(stringResponse, listType);
        } else {
            ErrorResponse errorResponse = gson.fromJson(stringResponse, ErrorResponse.class);
            if (errorResponse != null) {
                throw new PhrasesException(errorResponse.error.message);
            } else {
                throw new PhrasesException(String.valueOf(statusCode));
            }
        }
    }

    /**
     * Enrolls a new stream for a given speaker
     *
     * @param audioStream The stream to enroll
     * @param id The speaker profile speaker ID
     * @return Enrollment object encapsulating the enrollment response
     * @throws EnrollmentException Thrown in case of internal server error, wrong ID or an invalid audio format
     * @throws IOException Signals an I/O issue while reading the audio stream, a connection abortion, or an invalid response content
     */
    @Override
    public Enrollment enroll(InputStream audioStream, UUID id) throws EnrollmentException, IOException {

        String requestUrl = BASE_URI + "/" + id.toString() + "/enroll";
        HttpPost request = (HttpPost) clientHelper.createHttpRequest(requestUrl, RequestType.POST);

        String fileName = id.toString() + "_" + new Date();
        HttpEntity entity = clientHelper.addStreamToEntity(audioStream, "enrollmentData", fileName);
        request.setEntity(entity);

        HttpResponse response = defaultHttpClient.execute(request);

        int statusCode = clientHelper.getStatusCode(response);
        String stringResponse = clientHelper.httpResponseToString(response);

        if (statusCode == HttpStatus.SC_OK) {
            return gson.fromJson(stringResponse, Enrollment.class);
        } else {
            ErrorResponse errorResponse = gson.fromJson(stringResponse, ErrorResponse.class);
            if (errorResponse != null) {
                throw new EnrollmentException(errorResponse.error.message);
            } else {
                throw new EnrollmentException(String.valueOf(statusCode));
            }
        }
    }

    /**
     * Verifies a given speaker using the speaker ID and audio stream
     *
     * @param audioStream The stream of audio to be verified
     * @param id The speaker ID
     * @return A verification object encapsulating the verification result
     * @throws VerificationException Thrown in case of invalid ID, invalid audio format or internal server error
     * @throws IOException Signals an I/O issue while reading the audio stream, a connection abortion, or an invalid response content
     */
    @Override
    public Verification verify(InputStream audioStream, UUID id) throws VerificationException, IOException {

        String requestUrl = VERIFY_ENDPOINT + "?verificationProfileId=" + id.toString();
        HttpPost request = (HttpPost) clientHelper.createHttpRequest(requestUrl, RequestType.POST);

        String fileName = id.toString() + "_" + new Date();
        HttpEntity entity = clientHelper.addStreamToEntity(audioStream, "verificationData", fileName);
        request.setEntity(entity);

        HttpResponse response = defaultHttpClient.execute(request);

        int statusCode = clientHelper.getStatusCode(response);
        String stringResponse = clientHelper.httpResponseToString(response);

        if (statusCode == HttpStatus.SC_OK) {
            return gson.fromJson(stringResponse, Verification.class);
        } else {
            ErrorResponse errorResponse = gson.fromJson(stringResponse, ErrorResponse.class);
            if (errorResponse != null) {
                throw new VerificationException(errorResponse.error.message);
            } else {
                throw new VerificationException(String.valueOf(statusCode));
            }
        }
    }

    /**
     * Deletes all enrollments associated with the given speaker verification profile permanently from the service
     *
     * @param id The speaker ID
     * @throws ResetEnrollmentsException Thrown in case of invalid ID, failure to reset the profile or an internal server error
     * @throws IOException Signals a connection abortion, or an invalid response content
     */
    @Override
    public void resetEnrollments(UUID id) throws ResetEnrollmentsException, IOException {

        String requestUrl = BASE_URI + "/" + id.toString() + "/reset";
        HttpPost request = (HttpPost) clientHelper.createHttpRequest(requestUrl, RequestType.POST);

        HttpResponse response = defaultHttpClient.execute(request);

        int statusCode = clientHelper.getStatusCode(response);
        String stringResponse = clientHelper.httpResponseToString(response);

        if (statusCode != HttpStatus.SC_OK) {
            ErrorResponse errorResponse = gson.fromJson(stringResponse, ErrorResponse.class);
            if (errorResponse != null) {
                throw new ResetEnrollmentsException(errorResponse.error.message);
            } else {
                throw new ResetEnrollmentsException(String.valueOf(statusCode));
            }
        }
    }
}