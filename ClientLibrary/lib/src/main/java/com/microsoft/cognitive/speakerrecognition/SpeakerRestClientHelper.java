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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;

/**
 * A package-private helper class
 */
class SpeakerRestClientHelper {

    /**
     * The header for the subscription key
     */
    private static final String OCP_SUBSCRIPTION_KEY_HEADER = "Ocp-Apim-Subscription-Key";

    /**
     * Json accept header
     */
    private static final String JSON_HEADER_ACCEPT = "accept";

    /**
     * Json accept header value
     */
    private static final String JSON_HEADER_VALUE_ACCEPT = "application/json";

    /**
     * Subscription key
     */
    private String subscriptionKey;

    //----------------------------------------------------------------------------------------------

    SpeakerRestClientHelper(String subscriptionKey) {
        this.subscriptionKey = subscriptionKey;
    }

    //----------------------------------------------------------------------------------------------

    /**
     * Retrieves the status code of an HTTP response
     *
     * @param response HTTP response
     * @return HTTP status code
     */
    int getStatusCode(HttpResponse response) {
        return response.getStatusLine().getStatusCode();
    }

    /**
     * Creates an HTTP request
     *
     * @param resourceURL HTTP resource address
     * @param requestType HTTP request type
     * @return HTTP request
     */
    HttpUriRequest createHttpRequest(String resourceURL, RequestType requestType) {
        HttpUriRequest request;

        switch (requestType) {
            case GET:
                request = new HttpGet(resourceURL);
                break;
            case POST:
                request = new HttpPost(resourceURL);
                break;
            case DELETE:
                request = new HttpDelete(resourceURL);
                break;
            default:
                return null;
        }

        request.addHeader(JSON_HEADER_ACCEPT, JSON_HEADER_VALUE_ACCEPT);
        request.addHeader(OCP_SUBSCRIPTION_KEY_HEADER, subscriptionKey);
        return request;
    }

    /**
     * Converts an HTTP response to a string
     *
     * @param response HTTP response
     * @return A string representation of the HTTP response
     * @throws IOException Signals that a content stream couldn't be created from the HTTP response entity
     */
    String httpResponseToString(HttpResponse response) throws IOException {
        InputStream responseStream = response.getEntity().getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(responseStream));
        String str;
        StringBuilder stringResponse = new StringBuilder();
        while ((str = reader.readLine()) != null) {
            stringResponse.append(str);
        }
        return stringResponse.toString();
    }

    /**
     * Adds a stream to an HTTP entity
     *
     * @param someStream Input stream to be added to an HTTP entity
     * @param fieldName A description of the entity content
     * @param fileName Name of the file attached as an entity
     * @return HTTP entity
     * @throws IOException Signals a failure while reading the input stream
     */
    HttpEntity addStreamToEntity(InputStream someStream, String fieldName, String fileName) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int bytesRead;
        byte[] bytes = new byte[1024];
        while ((bytesRead = someStream.read(bytes)) > 0) {
            byteArrayOutputStream.write(bytes, 0, bytesRead);
        }
        byte[] data = byteArrayOutputStream.toByteArray();

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.setStrictMode();
        builder.addBinaryBody(fieldName, data, ContentType.MULTIPART_FORM_DATA, fileName);
        return builder.build();
    }

    /**
     * Converts a list of profile IDs to a single string
     *
     * @param ids List of profile IDs
     * @return String of profile IDs separated by a comma(",")
     */
    String buildProfileIdsString(List<UUID> ids) {
        StringBuilder builder = new StringBuilder();
        Iterator<?> iter = ids.iterator();
        while (iter.hasNext()) {
            builder.append(iter.next());
            if (!iter.hasNext()) {
                break;
            }
            builder.append(",");
        }
        return builder.toString();
    }
}
