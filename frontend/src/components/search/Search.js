import React, {useState} from "react";
import { useNavigate } from "react-router-dom";
import SongItem from "../song/songItem/SongItem";
import AlbumItem from "../album/albumItem/AlbumItem";
import UserItem from "../user/userItem/UserItem";

function Search(props) {
  const navigate = useNavigate();
  const [searchTerm, setSearchTerm] = useState('');
  const [songs, setSongs] = useState([]);
  const [albums, setAlbums] = useState([]);
  const [users, setUsers] = useState([]);
  const { playMusic, getImageURL } = props;

  function getSongs(name){
    fetch(`http://localhost:8080/api/v1/songs/search/${name}`,{
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
        console.log(data);
        setSongs(data);
    })
    .catch((error) => {
      console.error("There was a problem with the fetch operation:", error);
    });
  }

  function getAlbums(name){
    fetch(`http://localhost:8080/api/v1/albums/search/${name}`,{
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
        console.log(data);
        setAlbums(data);
    })
    .catch((error) => {
      console.error("There was a problem with the fetch operation:", error);
    });
  }

  function getUsers(name){
    fetch(`http://localhost:8080/api/v1/users/search/${name}`,{
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
        console.log(data);
        setUsers(data);
    })
    .catch((error) => {
      console.error("There was a problem with the fetch operation:", error);
    });
  }

  const handleInputChange = (event) => {
    setSearchTerm(event.target.value);
    const name = event.target.value;
    if(name !== ""){
        console.log(name)
        getSongs(name);
        getAlbums(name);
        getUsers(name);
    }
  };

  const handleSubmit = (event) => {
    setSearchTerm(event.target.value);
    event.preventDefault();
    if(searchTerm !== ""){         
        console.log(searchTerm)                 
        getSongs(searchTerm);
        getAlbums(searchTerm);
        getUsers(searchTerm);
    }
  };

  const follow = (userId) => {
    fetch(`http://localhost:8080/api/v1/users/follow?userId=${userId}`,{
      method: "POST",
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
          console.log(data);
          setUsers(data);
      })
      .catch((error) => {
        console.error("There was a problem with the fetch operation:", error);
      });
  }

  return (
    <div className="container container-fluid">
      <div className="row">
        <div className="col">
            <h1>Search</h1>
            <div>
              <div className="row">
                <form onSubmit={handleSubmit} encType="multipart/form-data">
                    <div className="row">
                    <div className="col">
                        <div className="input-box">
                        <label className="button-label">Name</label>
                        <br />
                        <input type="text" id="search" name="search" value={searchTerm} onChange={handleInputChange}/>
                        </div>
                    </div>
                    </div>
                </form>
              </div>
            </div>
            <h2>Songs:</h2>
            <div>
                {songs.map((item) => (
                    <SongItem item={item} playMusic={playMusic} getImageURL={getImageURL}/>
                ))}
            </div>
            <h2>Albums:</h2>
            <div>
              {albums.map((item) => (
                <AlbumItem item={item} playMusic={playMusic} getImageURL={getImageURL}/>
              ))}
            </div>
            <h2>Users:</h2>
            <div>
              {users.map((item) => (
                <UserItem item={item} playMusic={playMusic} getImageURL={getImageURL} follow={follow}/>
              ))}
            </div>
        </div>
      </div>
    </div>
  );
}

export default Search;
