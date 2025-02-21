import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import Login from "./components/login/Login";
import Navbar from "./components/navbar/Navbar";
import HomeComponent from "./components/home/HomeComponent";
import Register from "./components/register/Register";
import UserComponent from "./components/user/UserComponent";
import UploadSong from "./components/uploda_music/UploadSong";
import UploadAlbum from "./components/upload_album/UploadAlbum";
import SongDetail from "./components/song/SongDetails";
import React, { useState, useEffect } from "react";
import AlbumDetail from "./components/album/AlbumDetails";
import SongEditor from "./components/edit_song/SongEditor";
import AlbumEditor from "./components/edit_album/AlbumEditor";
import Player from "./components/player/Player";
import Search from "./components/search/Search";

function App() {
  const [loggedIn, setLoggedIn] = useState(false);
  const [username, setUsername] = useState(null);
  const [profilePic, setProfilePic] = useState(null);
  const [songs, setSongs] = useState([]);
  const [albums, setAlbums] = useState([]);
  const [userSongs, setUserSongs] = useState([]);
  const [audio, setAudio] = useState(null);

  //TODO: put these in their respective files
  useEffect(() => {
    fetchSongs();
  }, []);

  useEffect(() => {
    fetchUserData();
  }, []);

  useEffect(() => {
    fetchAlbums();
  }, []);

  useEffect(() => {
    fetchUserSongs();
  }, []);

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
          throw new Error("Network response was not ok " + response.statusText);
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
  }

  const fetchUserData = () => {
    fetch("http://localhost:8080/api/v1/users", {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include",
    })
    .then((response) => {
      if (!response.ok) {
        throw new Error("Network response was not ok " + response.statusText);
      }
      return response.json();
    })
    .then((data) => {
      setLoggedIn(true);
      setUsername(data.username);
      setProfilePic(data.profilePicture);
    })
    .catch((error) => {
      console.error("There was a problem with the fetch operation:", error);
    });
  };

  const fetchAlbums = () => {
    fetch("http://localhost:8080/api/v1/albums", {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include",
    })
    .then((response) => {
      if (!response.ok) {
        throw new Error("Network response was not ok " + response.statusText);
      }
      return response.json();
    })
    .then((data) => {
      setAlbums(data);
    })
    .catch((error) => {
      console.error("There was a problem with the fetch operation:", error);
    });
  };

  const fetchUserSongs = () => {
    fetch("http://localhost:8080/api/v1/songs", {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include",
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error("Network response was not ok " + response.statusText);
        }
        return response.json();
      })
      .then((data) => {
        setUserSongs(data);
      })
      .catch((error) => {
        console.error("There was a problem with the fetch operation:", error);
      });
  };

  const fetchSongs = () => {
    fetch("http://localhost:8080/api/v1/users/recommended", {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include",
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error("Network response was not ok " + response.statusText);
        }
        return response.json();
      })
      .then((data) => {
        setSongs(data);
      })
      .catch((error) => {
        console.error("There was a problem with the fetch operation:", error);
      });
  };

  const getImageURL = async (imageName) => {
    const resp = await fetch("http://localhost:8080/api/v1/files/image/" + imageName, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include",
    });
    return await resp.text();
  }

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
          getImageURL={getImageURL}
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
          element={<HomeComponent songs={songs} playMusic={playMusic} getImageURL={getImageURL}/>} />
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
                getImageURL={getImageURL}
              />
            }
          />
          <Route path="/search" element={<Search playMusic={playMusic} getImageURL={getImageURL}/>} />
          <Route path="/song/add" element={<UploadSong/>} />
          <Route path="/album/add" element={<UploadAlbum/>} />
          <Route path="/songs/:songId" element={<SongDetail playMusic={playMusic} getImageURL={getImageURL}/>} />
          <Route path="/albums/:albumId" element={<AlbumDetail playMusic={playMusic} getImageURL={getImageURL}/>} />
          <Route path="songs/edit/:songId" element={<SongEditor playMusic={playMusic} getImageURL={getImageURL}/>} />
          <Route path="albums/edit/:albumId" element={<AlbumEditor playMusic={playMusic} getImageURL={getImageURL}/>} />
        </Routes>
        <Player/>
      </div>
    </Router>
  );
}

export default App;
