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

Songs and albums have 3 visibility types: PUBLIC, PRIVATE, PROTECTED. Public songs/albums are accessible by everyone, protected songs/albums
are accessible by only ones with the link, private songs/albums are only accessible by the user

There are 3 types of account: ADMIN, USER, PREMIUM_USER. There are no special admin features implemented yet, in the future 
this will change(f.e.: delete songs/albums/users, access to private songs/albums, etc.)

A table called user_recommendation is updated at midnight every day with a scheduled method. The method first looks at 
the listen history of users, creates graph nodes out of the data, then connects the nodes by same user nodes and by same song nodes.
Then for every song a user listened to we'll get all the users who listened to the same song and go through the songs they listened
to, put it in a hashmap and start counting how many times we've seen the song. At the end the algorithm will sort the results by
number of occurrences and take the top 100. Finally, all records from the user_recommendation table will be 
deleted(this could be improved) and the new top 100 result for every user will be written into the database. There is place 
for improvement, the algorithm could take into consideration many other factors too. When a user is signed in the application will first
try to get the top 100 songs, then it will put the songs in order by listen count and serve them to the frontend, in case the recommended songs
are not enough

Currently, in progress:
--
- Improve/bugfix the analyzing process, take into consideration other variables than only the number of occurrences
- Improve/bugfix the cache system
- Try to improve the efficiency and number of db transactions
- Create a better algorithm to calculate cacheIndex for songs
- Review and improve thread safety and general thread usage
- Write more tcs, add functional tests too
- Review RuntimeException usage(maybe sometimes we could throw a more suitable one) 