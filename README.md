# music_upload_site

### Backend for a website similar to Spotify or Soundcloud, where users can create accounts and albums, upload songs and listen to their and other users' music

#### Disclaimer: this project is under development

After starting the application it will automatically check if the database is empty, if yes then it will seed the 
database with dummy data using Java faker, download pictures for users and song/album covers from a [website](https://picsum.photos/200), create users,
songs and albums and generate random audio files

Besides the basic JPA classes, a database cache has been implemented to improve performance. When a song/album is 
queried it will be cached for an amount of time calculated based on the number of listens on the song

When a user listens to a song it will be stored in the database, writing them into the db in batches with a scheduled job.
This will enable the application to analyze the data later on and recommend songs to the users based on their listen history

Currently, in progress:
--
- Improve/bugfix the analyzing process, take into consideration other variables than only the number of occurrences
- Improve/bugfix the cache system
- Try to improve the efficiency and number of db transactions
- Create a better algorithm to calculate cacheIndex for songs
- Review and improve thread safety and general thread usage