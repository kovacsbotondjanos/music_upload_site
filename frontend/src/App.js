import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import Login from "./components/login/Login";
import Navbar from "./components/navbar/Navbar";
import HomeComponent from "./components/home/HomeComponent";
import Register from "./components/register/Register";
import UserComponent from "./components/user/UserComponent";
import UploadSong from "./components/upload_music/UploadSong";
import UploadAlbum from "./components/upload_album/UploadAlbum";
import SongDetail from "./components/song/SongDetails";
import React, { useState, useEffect } from "react";
import AlbumDetail from "./components/album/AlbumDetails";
import SongEditor from "./components/edit_song/SongEditor";
import AlbumEditor from "./components/edit_album/AlbumEditor";
import Player from "./components/player/Player";
import Search from "./components/search/Search";
import {
  getUserData,
  getMusic,
} from "./services/controller";
import { resolve } from "./services/utils";

const App = () => {
  const [loggedIn, setLoggedIn] = useState(false);
  const [username, setUsername] = useState(null);
  const [profilePic, setProfilePic] = useState(null);
  const [audio, setAudio] = useState(null);

  //TODO: find a proper player to control current songs
  const playMusic = (nameHashed) => {
    if (nameHashed != null) {
      const audioPlayer = document.getElementById("audio-player");
      const audioSource = document.getElementById("audio-source");

      const fetch = async () =>
        getMusic(nameHashed, resolve(setAudio));
      fetch();

      audioSource.src = audio;

      audioPlayer.removeEventListener("canplaythrough", handlePlay);
      const handlePlay = () => {
        audioPlayer
          .play()
          .then(() => {
            console.info("Audio is playing.");
          })
          .catch((error) => {
            console.error(error);
          });
      }
      audioPlayer.addEventListener("canplaythrough", handlePlay);
      audioPlayer.load();
    }
  };

  useEffect(() => {
    const fetch = async () =>
      getUserData((data) => {
        if (data) {
          setLoggedIn(true);
          setUsername(data.username);
          setProfilePic(data.profilePicture);
        }
      });
    fetch();
  }, []);

  return (
    <Router>
      <div className="App black-bg">
        <Navbar
          loggedIn={loggedIn}
          profilePic={profilePic}
          username={username}
          setLoggedIn={setLoggedIn}
          setUsername={setUsername}
          setProfilePic={setProfilePic}
        />
        <Routes>
          <Route
            path="/login"
            element={
              <Login
                setLoggedIn={setLoggedIn}
                setUsername={setUsername}
                setProfilePic={setProfilePic}
              />
            }
          />
          <Route path="/register" element={<Register />} />
          <Route index element={<HomeComponent playMusic={playMusic} />} />
          <Route
            path="/profile"
            element={
              <UserComponent
                loggedIn={loggedIn}
                profilePic={profilePic}
                username={username}
                playMusic={playMusic}
              />
            }
          />
          <Route path="/search" element={<Search playMusic={playMusic} />} />
          <Route path="/song/add" element={<UploadSong />} />
          <Route path="/album/add" element={<UploadAlbum />} />
          <Route
            path="/songs/:songId"
            element={<SongDetail playMusic={playMusic} />}
          />
          <Route
            path="/albums/:albumId"
            element={<AlbumDetail playMusic={playMusic} />}
          />
          <Route
            path="songs/edit/:songId"
            element={<SongEditor playMusic={playMusic} />}
          />
          <Route path="albums/edit/:albumId" element={<AlbumEditor />} />
        </Routes>
        <Player />
      </div>
    </Router>
  );
};

export default App;
