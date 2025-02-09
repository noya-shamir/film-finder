### Film Finder ###

An app for browsing movies & TV shows using the OMDB API (a free, open-source, widely available IMDB alternative).

#### Description ####
Users are able to search for movies and TV shows by title, browse the results in a RecyclerView, and tap on a specific item to view detailed information.

Users can also mark a movie or TV show as favorite. However, we have yet to set up local persistence for the project, so once a user leaves the app, the "isFavorite" information is lost...

#### Architecture ####
The project uses MVVM architecture, combined with the Repository Pattern and Dependency Injection.

#### 3rd Party Libraries ####
The following libraries are used:
- Retrofit for making API calls
- Coil for loading, caching and displaying images
- Paging 3 for loading and displaying pages of data (for the search results)
- Hilt/Dagger for dependency injection
- Timber for logging

#### Instructions ####
Clone the repository and open the project in Android Studio. You may need to update the AGP version. 

Important: To be able to make successful network calls, toy must add your OMDB key to your local properties file as follows:

API_KEY=<your_key>
