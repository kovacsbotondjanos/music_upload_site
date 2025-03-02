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
  fetchRecommendedSongs,
  fetchUserData,
  fetchAlbums,
  fetchSongsForUser,
} from "./services/controller";

const App = () => {
  const [loggedIn, setLoggedIn] = useState(false);
  const [username, setUsername] = useState(null);
  const [profilePic, setProfilePic] = useState(null);
  const [songs, setSongs] = useState([]);
  const [albums, setAlbums] = useState([]);
  const [userSongs, setUserSongs] = useState([]);
  const [audio, setAudio] = useState(null);
  
  //TODO: find a proper player to control current songs
  const playMusic = (nameHashed) => {
    if (nameHashed != null) {
      const audioPlayer = document.getElementById("audio-player");
      const audioSource = document.getElementById("audio-source");

      fetch("http://localhost:8080/api/v1/files/music/" + nameHashed, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include",
      })
        .then((response) => {
          if (!response.ok) {
            throw new Error(
              "Network response was not ok " + response.statusText
            );
          }
          return response.text();
        })
        .then((data) => {
          setAudio(data);
        })
        .catch((error) => {
          console.error("There was a problem with the fetch operation:", error);
        });

      audioSource.src = audio;

      audioPlayer.removeEventListener("canplaythrough", handlePlay);
      function handlePlay() {
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

  //TODO: put these in their respective files
  useEffect(() => {
    const fetch = async () =>
      await fetchRecommendedSongs((data) => {
        if (data) {
          setSongs(data);
        }
      });
    fetch();
  }, []);

  useEffect(() => {
    const fetch = async () =>
      await fetchUserData((data) => {
        if (data) {
          setLoggedIn(true);
          setUsername(data.username);
          setProfilePic(data.profilePicture);
        }
      });
    fetch();
  }, []);

  useEffect(() => {
    const fetch = async () =>
      await fetchAlbums((data) => {
        if (data) {
          setAlbums(data);
        }
      });
    fetch();
  }, []);

  useEffect(() => {
    const fetch = async () =>
      await fetchSongsForUser((data) => {
        setUserSongs(data);
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
          <Route
            index
            element={<HomeComponent songs={songs} playMusic={playMusic} />}
          />
          <Route
            path="/profile"
            element={
              <UserComponent
                loggedIn={loggedIn}
                profilePic={profilePic}
                username={username}
                albums={albums}
                userSongs={userSongs}
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
