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

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

/**
 * An interface for a speaker identification service client related methods
 */
public interface SpeakerIdentificationClient {

    /**
     * Identifies a given speaker using the speaker ID and audio stream
     *
     * @param audioStream The audio stream to identify
     * @param ids The list of possible speaker profile IDs to identify from
     * @return An object encapsulating the Url that can be used to query the identification operation status
     * @throws IdentificationException Thrown in case of an internal server error, invalid IDs or a wrong audio format
     * @throws IOException Signals an I/O issue while reading the audio stream, a connection abortion, or an invalid response content
     */
    OperationLocation identify(InputStream audioStream, List<UUID> ids) throws IdentificationException, IOException;

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
    OperationLocation identify(InputStream audioStream, List<UUID> ids, boolean forceShortAudio) throws IdentificationException, IOException;

    /**
     * Creates a new speaker profile
     *
     * @param locale The speaker profile locale
     * @return The profile object encapsulating the response object of the create request
     * @throws CreateProfileException Thrown on cases of internal server error or an invalid locale
     * @throws IOException Signals an invalid locale encoding, a connection abortion, or an invalid response content
     */
    CreateProfileResponse createProfile(String locale) throws CreateProfileException, IOException;

    /**
     * Deletes a given speaker profile
     *
     * @param id The ID of the speaker profile to be deleted
     * @throws DeleteProfileException Thrown on case of an internal server error or an invalid ID
     * @throws IOException Signals a connection abortion, or an invalid response content
     */
    void deleteProfile(UUID id) throws DeleteProfileException, IOException;

    /**
     * Retrieves a speaker profile from the service
     *
     * @param id The ID of the speaker profile to get
     * @return The requested profile
     * @throws GetProfileException Thrown in cases of invalid ID or an internal server error
     * @throws IOException Signals a connection abortion, or an invalid response content
     */
    Profile getProfile(UUID id) throws GetProfileException, IOException;

    /**
     * Gets all speaker profiles from the service
     *
     * @return An array containing a list of all profiles
     * @throws GetProfileException Thrown in case of an internal server error
     * @throws IOException Signals a connection abortion, or an invalid response content
     */
    List<Profile> getProfiles() throws GetProfileException, IOException;

    /**
     * Enrolls a speaker profile from an audio stream
     *
     * @param audioStream The audio stream to use for enrollment
     * @param id The speaker profile ID to enroll
     * @return An object encapsulating the Url that can be used to query the enrollment operation status
     * @throws EnrollmentException Thrown in case of an invalid audio format, internal server error or an invalid ID
     * @throws IOException Signals an I/O issue while reading the audio stream, a connection abortion, or an invalid response content
     */
    OperationLocation enroll(InputStream audioStream, UUID id) throws EnrollmentException, IOException;

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
    OperationLocation enroll(InputStream audioStream, UUID id, boolean forceShortAudio) throws EnrollmentException, IOException;

    /**
     * Gets the enrollment operation status or result
     *
     * @param location The Url returned upon calling the enrollment operation
     * @return The enrollment operation object encapsulating the result
     * @throws EnrollmentException Thrown in case of an internal server error or an invalid URL
     * @throws IOException Signals a connection abortion, or an invalid response content
     */
    EnrollmentOperation checkEnrollmentStatus(OperationLocation location) throws EnrollmentException, IOException;

    /**
     * Gets the identification operation status or result
     *
     * @param location The Url returned upon calling the identification operation
     * @return The identification operation object encapsulating the result
     * @throws IdentificationException Thrown in case of an internal server error or a wrong URL
     * @throws IOException Signals a connection abortion, or an invalid response content
     */
    IdentificationOperation checkIdentificationStatus(OperationLocation location) throws IdentificationException, IOException;

    /**
     * Deletes all enrollments associated with the given speaker identification profile permanently from the service
     *
     * @param id The speaker ID
     * @throws ResetEnrollmentsException Thrown in case of internal server error or an invalid ID
     * @throws IOException Signals a connection abortion, or an invalid response content
     */
    void resetEnrollments(UUID id) throws ResetEnrollmentsException, IOException;
}
