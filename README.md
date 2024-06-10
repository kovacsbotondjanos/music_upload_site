# music_upload_site

### Backend of a website similar to Spotify or Soundcloud, users can create accounts and albums, upload songs and listen their and other user's songs

After starting the application it will automatically check if the database is empty, if yes then it will seed the 
database with data using Java faker, download pictures for users, songs and albums and generate random audio files

Besides the basic JPA classes, a database cache has been implemented to improve performance. When a song is 
queried it will be cached for an amount of time calculated by the number of listens on the song

When a user listens to a song it will be saved into the database in batches, with a scheduled job. This will
enable the application to analyze the data later on and recommend songs to the user based on the listen history.

Currently, in progress:
--
- Albums cache
- Improve/bugfix the analyzing process