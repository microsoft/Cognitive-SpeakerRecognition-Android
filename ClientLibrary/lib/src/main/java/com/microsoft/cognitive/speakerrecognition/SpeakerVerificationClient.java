//
// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license.
//
// Microsoft Cognitive Services (formerly Project Oxford): https://www.microsoft.com/cognitive-services
//
// Microsoft Cognitive Services (formerly Project Oxford) GitHub:
// https://github.com/Microsoft/Cognitive-SpeakerRecognition-Android
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

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

/**
 * An interface for the speaker verification service client related methods
 */
public interface SpeakerVerificationClient {

    /**
     * Creates a new speaker profile
     *
     * @param locale The speaker profile locale
     * @return The Profile object encapsulating the speaker profile response
     * @throws CreateProfileException Thrown in case of internal server error or an invalid locale
     * @throws IOException Signals an invalid locale encoding, a connection abortion, or an invalid response content
     */
    CreateProfileResponse createProfile(String locale) throws CreateProfileException, IOException;

    /**
     * Retrieves a given speaker profile as specified by the id parameter
     *
     * @param id The speaker profile ID
     * @return The requested speaker profile
     * @throws GetProfileException Thrown in case of internal server error or an invalid ID
     * @throws IOException Signals a connection abortion, or an invalid response content
     */
    Profile getProfile(UUID id) throws GetProfileException, IOException;

    /**
     * Retrieves all available speaker profiles
     *
     * @return A list of speaker profiles
     * @throws GetProfileException Thrown in case of internal server error or an invalid ID
     * @throws IOException Signals a connection abortion, or an invalid response content
     */
    List<Profile> getProfiles() throws GetProfileException, IOException;

    /**
     * Deletes a given speaker profile
     *
     * @param id The ID of the speaker profile to be deleted
     * @throws DeleteProfileException Thrown in case of internal server error, an invalid ID or failure to delete the profile
     * @throws IOException Signals a connection abortion, or an invalid response content
     */
    void deleteProfile(UUID id) throws DeleteProfileException, IOException;

    /**
     * Gets a list of all available phrases for enrollments
     *
     * @param locale The locale of the phrases
     * @return A list of all verification phrases
     * @throws PhrasesException Thrown in case of invalid locale or internal server error
     * @throws IOException Signals an invalid locale encoding, a connection abortion, or an invalid response content
     * @throws URISyntaxException Signals that the endpoint string could not be parsed as a URI reference
     */
    List<VerificationPhrase> getPhrases(String locale) throws PhrasesException, IOException, URISyntaxException;

    /**
     * Enrolls a new stream for a given speaker
     *
     * @param audioStream The stream to enroll
     * @param id The speaker profile speaker ID
     * @return Enrollment object encapsulating the enrollment response
     * @throws EnrollmentException Thrown in case of internal server error, wrong ID or an invalid audio format
     * @throws IOException Signals an I/O issue while reading the audio stream, a connection abortion, or an invalid response content
     */
    Enrollment enroll(InputStream audioStream, UUID id) throws EnrollmentException, IOException;

    /**
     * Verifies a given speaker using the speaker ID and audio stream
     *
     * @param audioStream The stream of audio to be verified
     * @param id The speaker ID
     * @return A verification object encapsulating the verification result
     * @throws VerificationException Thrown in case of invalid ID, invalid audio format or internal server error
     * @throws IOException Signals an I/O issue while reading the audio stream, a connection abortion, or an invalid response content
     */
    Verification verify(InputStream audioStream, UUID id) throws VerificationException, IOException;

    /**
     * Deletes all enrollments associated with the given speaker verification profile permanently from the service
     *
     * @param id The speaker ID
     * @throws ResetEnrollmentsException Thrown in case of invalid ID, failure to reset the profile or an internal server error
     * @throws IOException Signals a connection abortion, or an invalid response content
     */
    void resetEnrollments(UUID id) throws ResetEnrollmentsException, IOException;
}
