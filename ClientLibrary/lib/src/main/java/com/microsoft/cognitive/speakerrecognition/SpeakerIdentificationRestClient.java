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
import com.microsoft.cognitive.speakerrecognition.contract.identification.CreateProfileResponse;
import com.microsoft.cognitive.speakerrecognition.contract.identification.EnrollmentOperation;
import com.microsoft.cognitive.speakerrecognition.contract.identification.IdentificationException;
import com.microsoft.cognitive.speakerrecognition.contract.identification.IdentificationOperation;
import com.microsoft.cognitive.speakerrecognition.contract.identification.OperationLocation;
import com.microsoft.cognitive.speakerrecognition.contract.identification.Profile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
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
 * This class abstracts all the identification service calls
 */
public class SpeakerIdentificationRestClient implements SpeakerIdentificationClient {

    /**
     * The Http client used to communicate with the service
     */
    private HttpClient defaultHttpClient;

    /**
     * Address of the identification profiles API
     */
    private static final String IDENTIFICATION_PROFILE_URI = "https://api.projectoxford.ai/spid/v1.0/identificationProfiles";

    /**
     * Address of the identification API
     */
    private static final String IDENTIFICATION_URI = "https://api.projectoxford.ai/spid/v1.0/identify";

    /**
     * The operation location header field
     */
    private static final String _OPERATION_LOCATION_HEADER = "Operation-Location";

    /**
     * Json Serializer / deserializer
     */
    private Gson gson;

    /**
     * The locale parameter
     */
    private static final String LOCALE_PARAM = "locale";

    /**
     * The short audio parameter name
     */
    private static final String SHORT_AUDIO_PARAM = "shortAudio";

    /**
     * Speaker client clientHelper
     */
    private SpeakerRestClientHelper clientHelper;

    //----------------------------------------------------------------------------------------------

    /**
     * Initializes an instance of the service client
     *
     * @param subscriptionKey The subscription key to use
     */
    public SpeakerIdentificationRestClient(String subscriptionKey) {
        defaultHttpClient = new DefaultHttpClient();
        gson =  new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:SS.SSS").create();
        clientHelper = new SpeakerRestClientHelper(subscriptionKey);
    }

    //----------------------------------------------------------------------------------------------

    /**
     * Creates a new speaker profile
     *
     * @param locale The speaker profile locale
     * @return The profile object encapsulating the response object of the create request
     * @throws CreateProfileException Thrown on cases of internal server error or an invalid locale
     * @throws IOException Signals an invalid locale encoding, a connection abortion, or an invalid response content
     */
    @Override
    public CreateProfileResponse createProfile(String locale) throws CreateProfileException, IOException {

        HttpPost request = (HttpPost) clientHelper.createHttpRequest(IDENTIFICATION_PROFILE_URI, RequestType.POST);

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
     * Retrieves a speaker profile from the service
     *
     * @param id The ID of the speaker profile to get
     * @return The requested profile
     * @throws GetProfileException Thrown in cases of invalid ID or an internal server error
     * @throws IOException Signals a connection abortion, or an invalid response content
     */
    @Override
    public Profile getProfile(UUID id) throws GetProfileException, IOException {

        HttpGet request = (HttpGet) clientHelper.createHttpRequest(IDENTIFICATION_PROFILE_URI + "/" + id.toString(), RequestType.GET);

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
     * Gets all speaker profiles from the service
     *
     * @return An array containing a list of all profiles
     * @throws GetProfileException Thrown in case of an internal server error
     * @throws IOException Signals a connection abortion, or an invalid response content
     */
    @Override
    public List<Profile> getProfiles() throws GetProfileException, IOException {

        HttpGet request = (HttpGet) clientHelper.createHttpRequest(IDENTIFICATION_PROFILE_URI, RequestType.GET);

        HttpResponse response = defaultHttpClient.execute(request);

        int statusCode = clientHelper.getStatusCode(response);
        String stringResponse = clientHelper.httpResponseToString(response);

        if (statusCode == HttpStatus.SC_OK) {
            Type listType = new TypeToken<List<Profile>>(){}.getType();
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
     * @throws DeleteProfileException Thrown on case of an internal server error or an invalid ID
     * @throws IOException Signals a connection abortion, or an invalid response content
     */
    @Override
    public void deleteProfile(UUID id) throws DeleteProfileException, IOException {

        HttpDelete request = (HttpDelete) clientHelper.createHttpRequest(IDENTIFICATION_PROFILE_URI + "/" + id.toString(), RequestType.DELETE);

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
     * Enrolls a speaker profile from an audio stream
     *
     * @param audioStream The audio stream to use for enrollment
     * @param id The speaker profile ID to enroll
     * @return An object encapsulating the Url that can be used to query the enrollment operation status
     * @throws EnrollmentException Thrown in case of an invalid audio format, internal server error or an invalid ID
     * @throws IOException Signals an I/O issue while reading the audio stream, a connection abortion, or an invalid response content
     */
    @Override
    public OperationLocation enroll(InputStream audioStream, UUID id) throws EnrollmentException, IOException {

        return enroll(audioStream, id, false);
    }

    /**
     * Enrolls a speaker profile from an audio stream
     *
     * @param audioStream The audio stream to use for enrollment
     * @param id The speaker profile ID to enroll
     * @param forceShortAudio Instruct the service to waive the recommended minimum audio limit needed for enrollment
     * @return An object encapsulating the Url that can be used to query the enrollment operation status
     * @throws EnrollmentException Thrown in case of an invalid audio format, internal server error or an invalid ID
     * @throws IOException Signals an I/O issue while reading the audio stream, a connection abortion, or an invalid response content
     */
    @Override
    public OperationLocation enroll(InputStream audioStream, UUID id, boolean forceShortAudio) throws EnrollmentException, IOException {

        String requestUrl = IDENTIFICATION_PROFILE_URI + "/" + id.toString() + "/enroll?" + SHORT_AUDIO_PARAM + "=" + forceShortAudio;
        HttpPost request = (HttpPost) clientHelper.createHttpRequest(requestUrl, RequestType.POST);

        String fileName = id.toString() + "_" + new Date();
        HttpEntity entity = clientHelper.addStreamToEntity(audioStream, "enrollmentData", fileName);
        request.setEntity(entity);

        HttpResponse response = defaultHttpClient.execute(request);

        int statusCode = clientHelper.getStatusCode(response);

        if (statusCode == HttpStatus.SC_ACCEPTED) { //  202 Accepted (HTTP/1.0 - RFC 1945)
            String header = response.getFirstHeader(_OPERATION_LOCATION_HEADER).getValue();
            if (header.trim().length() != 0) {
                OperationLocation opLoc = new OperationLocation();
                opLoc.Url = header;
                return opLoc;
            } else {
                throw new EnrollmentException("Incorrect server response");
            }
        } else {
            String stringResponse = clientHelper.httpResponseToString(response);
            ErrorResponse errorResponse = gson.fromJson(stringResponse, ErrorResponse.class);
            if (errorResponse != null) {
                throw new EnrollmentException(errorResponse.error.message);
            } else {
                throw new EnrollmentException(String.valueOf(statusCode));
            }
        }
    }

    /**
     * Gets the enrollment operation status or result
     *
     * @param location The Url returned upon calling the enrollment operation
     * @return The enrollment operation object encapsulating the result
     * @throws EnrollmentException Thrown in case of an internal server error or an invalid URL
     * @throws IOException Signals a connection abortion, or an invalid response content
     */
    @Override
    public EnrollmentOperation checkEnrollmentStatus(OperationLocation location) throws EnrollmentException, IOException {

        HttpGet request = (HttpGet) clientHelper.createHttpRequest(location.Url, RequestType.GET);

        HttpResponse response = defaultHttpClient.execute(request);

        int statusCode = clientHelper.getStatusCode(response);
        String stringResponse = clientHelper.httpResponseToString(response);

        if (statusCode == HttpStatus.SC_OK) {
            return gson.fromJson(stringResponse, EnrollmentOperation.class);
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
     * Deletes all enrollments associated with the given speaker identification profile permanently from the service
     *
     * @param id The speaker ID
     * @throws ResetEnrollmentsException Thrown in case of internal server error or an invalid ID
     * @throws IOException Signals a connection abortion, or an invalid response content
     */
    @Override
    public void resetEnrollments(UUID id) throws ResetEnrollmentsException, IOException {

        String requestUrl = IDENTIFICATION_PROFILE_URI + "/" + id.toString() + "/reset";
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

    /**
     * Gets the identification operation status or result
     *
     * @param location The Url returned upon calling the identification operation
     * @return The identification operation object encapsulating the result
     * @throws IdentificationException Thrown in case of an internal server error or a wrong URL
     * @throws IOException Signals a connection abortion, or an invalid response content
     */
    @Override
    public IdentificationOperation checkIdentificationStatus(OperationLocation location) throws IdentificationException, IOException {

        HttpGet request = (HttpGet) clientHelper.createHttpRequest(location.Url, RequestType.GET);

        HttpResponse response = defaultHttpClient.execute(request);

        int statusCode = clientHelper.getStatusCode(response);
        String stringResponse = clientHelper.httpResponseToString(response);

        if (statusCode == HttpStatus.SC_OK) {
            return gson.fromJson(stringResponse, IdentificationOperation.class);
        } else {
            ErrorResponse errorResponse = gson.fromJson(stringResponse, ErrorResponse.class);
            if (errorResponse != null) {
                throw new IdentificationException(errorResponse.error.message);
            } else {
                throw new IdentificationException(String.valueOf(statusCode));
            }
        }
    }

    /**
     * Identifies a given speaker using the speaker ID and audio stream
     *
     * @param audioStream The audio stream to identify
     * @param ids The list of possible speaker profile IDs to identify from
     * @return An object encapsulating the Url that can be used to query the identification operation status
     * @throws IdentificationException Thrown in case of an internal server error, invalid IDs or a wrong audio format
     * @throws IOException Signals an I/O issue while reading the audio stream, a connection abortion, or an invalid response content
     */
    @Override
    public OperationLocation identify(InputStream audioStream, List<UUID> ids) throws IdentificationException, IOException {

        return identify(audioStream, ids, false);
    }

    /**
     * Identifies a given speaker using the speaker ID and audio stream
     *
     * @param audioStream The audio stream to identify
     * @param ids The list of possible speaker profile IDs to identify from
     * @param forceShortAudio Instruct the service to waive the recommended minimum audio limit needed for identification
     * @return An object encapsulating the Url that can be used to query the identification operation status
     * @throws IdentificationException Thrown in case of an internal server error, invalid IDs or a wrong audio format
     * @throws IOException Signals an I/O issue while reading the audio stream, a connection abortion, or an invalid response content
     */
    @Override
    public OperationLocation identify(InputStream audioStream, List<UUID> ids, boolean forceShortAudio) throws IdentificationException, IOException {

        String testProfileIds = clientHelper.buildProfileIdsString(ids);
        String requestUrl = IDENTIFICATION_URI + "?identificationProfileIds=" + testProfileIds.toString() + "&" + SHORT_AUDIO_PARAM + "=" + forceShortAudio;
        HttpPost request = (HttpPost) clientHelper.createHttpRequest(requestUrl, RequestType.POST);

        String fileName = "identificationsIds" + "_" + new Date();
        HttpEntity entity = clientHelper.addStreamToEntity(audioStream, "identificationData", fileName);
        request.setEntity(entity);

        HttpResponse response = defaultHttpClient.execute(request);

        int statusCode = clientHelper.getStatusCode(response);

        if (statusCode == HttpStatus.SC_ACCEPTED) { //  202 Accepted (HTTP/1.0 - RFC 1945)
            String header = response.getFirstHeader(_OPERATION_LOCATION_HEADER).getValue();
            if (header.trim().length() != 0) {
                OperationLocation opLoc = new OperationLocation();
                opLoc.Url = header;
                return opLoc;
            } else {
                throw new IdentificationException("Incorrect server response");
            }
        } else {
            String stringResponse = clientHelper.httpResponseToString(response);
            ErrorResponse errorResponse = gson.fromJson(stringResponse, ErrorResponse.class);
            if (errorResponse != null) {
                throw new IdentificationException(errorResponse.error.message);
            } else {
                throw new IdentificationException(String.valueOf(statusCode));
            }
        }
    }
}
