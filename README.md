# OpenLibraryAndroidAssessment

 This is my submission for the OpenLibraryAndroidAssessment.

## Front End Overview
  The application follows the MVVM pattern and features a declarative UI built with Jetpack Compose. Any composables that collect on live datas have stateful and stateless versions; the stateful composables observe the ViewModel's LiveData flows and provide data to their stateless counterparts. Each stateless composable includes a preview. Navigation is managed through a composable navigation graph, which communicates user selections to the ViewModel via route parameters.

## Back End Overview
  OpenLibrary book data is stored in an SQLite database that is cleared and repopulated upon each launch of the app. A reference to the database helper is maintained in the main activity to ensure the database can be closed when the activity is destroyed. The database helper manages all insert and lookup operations.
  
  The data repo fetches and processes information from the OpenLibrary API, holding mutable LiveData that the ViewModel references. When the ViewModel detects user actions, it triggers appropriate repository flows on background threads. All database and repository operations are performed on background coroutines (unless I missed any! 😬). A polymorphic deserializer is used to read book descriptions from the OpenLibrary API, accommodating the varying locations of descriptions within API responses. A placeholder image is provided for any book that does not have a `cover_i` field or whose image url does not resolve to a valid image.
 
