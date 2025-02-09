### Film Finder ###

An app for bowsing movies & TV shows using the OMDB API (a free, open-source, widely available IMDB alternative).

#### Description ####
Users are able to search for movies and TV shows by title, browse the results in a RecyclerView, and tap on a specific item to view detailed information. 

#### Architecture ####
The project uses MVVM architecture, combined with the Repository Pattern and Dependency Injection.

#### 3rd Party Libraries ####
The following libraries are used:
- Retrofit for making API calls
- Coil for loading, caching and displaying images
- Paging 3 for loading and displaying pages of data (for the search results)
- Hilt/Dagger for dependency injection
- Timber for logging

