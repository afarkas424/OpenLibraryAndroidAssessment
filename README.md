# Open Library Android Assessment

 This is my submission for the Open Library Android Assessment.

## Front End Overview
  The application follows the MVVM pattern and features a declarative UI built with Jetpack Compose. Any composables that collect on live datas have stateful and stateless versions; the stateful composables observe the ViewModel's LiveData flows and provide data to their stateless counterparts. Each stateless composable includes a preview. Navigation is managed through a composable navigation graph that observes navigation events posted from the view model in response to user interaction.

## Back End Overview
  OpenLibrary book data is stored in an SQLite database that is cleared and repopulated upon each launch of the app. A reference to the database helper is maintained in the main activity to ensure the database can be closed when the activity is destroyed. The database helper manages all insert and lookup operations.
  
  The data repo fetches and processes information from the OpenLibrary API, holding mutable LiveData that the ViewModel references. When the ViewModel detects user actions, it triggers appropriate repository flows on background threads. All database and repository operations are performed on background coroutines (unless I missed any! 😬). A polymorphic deserializer is used to read book descriptions from the OpenLibrary API, accommodating the varying locations of descriptions within API responses-- any book that lacks a description (or has a description that is missed by the deserializer) reports a missing description. A placeholder image is provided for any book that does not have a `cover_i` field or whose image url does not resolve to a valid image.

## Screenshots

![Screenshot 2024-09-25 232457](https://github.com/user-attachments/assets/ef339406-6c4b-4bab-85df-2777190881e6)

![Screenshot 2024-09-25 232509](https://github.com/user-attachments/assets/928f180c-40ff-42de-8254-d2d7cf3fda4c)

![Screenshot 2024-09-25 232519](https://github.com/user-attachments/assets/2cb4fb08-5c8a-4bd1-93a6-7d6519ff1ce3)
