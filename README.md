# Microsoft Speaker Recognition API: Android Client Library
This repo contains the Android client library for the Microsoft Speaker Recognition API, an offering within [Microsoft Cognitive Services](https://www.microsoft.com/cognitive-services), formerly known as Project Oxford.
* [Learn about the Speaker Recognition API](https://www.microsoft.com/cognitive-services/en-us/speaker-recognition-api)
* [Read the documentation](https://www.microsoft.com/cognitive-services/en-us/speaker-recognition-api/documentation)
* [Find more SDKs & Samples](https://www.microsoft.com/cognitive-services/en-us/SDK-Sample?api=speaker%20recognition)

## The client library
This client library is a thin Java client wrapper for the Microsoft Speaker Recognition REST API.

The easiest way to consume the client library is to add com.microsoft.cognitive.speakerrecognition package from Maven Central Repository.

To find the latest version of client library, go to http://search.maven.org, and search for "com.microsoft.cognitive".

To add the client library dependency from build.gradle file, add the following line in dependencies.

```
dependencies {
    //
    // Use the following line to include client library from Maven Central Repository
    // Change the version number from the search.maven.org result
    //
    compile 'com.microsoft.cognitive:speakerrecognition:1.0.0'

    // Your other Dependencies...
}
```

To do add the client library dependency from Android Studio:
 1. From Menu, Choose File \> Project Structure
 2. Click on your app module
 3. Click on Dependencies tab
 4. Click "+" sign to add new dependency
 5. Pick "Library dependency" from the drop down list
 6. Type "com.microsoft.cognitive" and hit the search icon from "Choose Library Dependency" dialog
 7. Pick the Cognitive Services client library that you intend to use.
 8. Click "OK" to add the new dependency

## Using the API
1. First, you must obtain a Speaker Recognition API subscription key by [following the instructions on our website](<https://www.microsoft.com/cognitive-services/en-us/sign-up>).
2. For more details on how to build the URL and make an API request visit this [link](<https://westus.dev.cognitive.microsoft.com/docs/services/563309b6778daf02acc0a508/operations/5645c725ca73070ee8845bd6>)
3. See code samples on how to create a request [here](<https://westus.dev.cognitive.microsoft.com/docs/services/563309b6778daf02acc0a508/operations/5645c725ca73070ee8845bd6#java>)

## Contributing
We welcome contributions. Feel free to file issues and pull requests on the repo and we'll address them as we can. Learn more about how you can help on our [Contribution Rules & Guidelines](</CONTRIBUTING.md>). 

You can reach out to us anytime with questions and suggestions using our communities below:
 - **Support questions:** [StackOverflow](<https://stackoverflow.com/questions/tagged/microsoft-cognitive>)
 - **Feedback & feature requests:** [Cognitive Services UserVoice Forum](<https://cognitive.uservoice.com>)

This project has adopted the [Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/). For more information see the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/) or contact [opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or comments.

## License
All Microsoft Cognitive Services SDKs and samples are licensed with the MIT License. For more details, see
[LICENSE](</LICENSE.md>).

Sample images are licensed separately, please refer to [LICENSE-IMAGE](</LICENSE-IMAGE.md>).

## Developer Code of Conduct
Developers using Cognitive Services, including this client library & sample, are expected to follow the “Developer Code of Conduct for Microsoft Cognitive Services”, found at [http://go.microsoft.com/fwlink/?LinkId=698895](http://go.microsoft.com/fwlink/?LinkId=698895).
