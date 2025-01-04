import { useNavigate } from "react-router-dom";
import React from "react";
import SongItem from "../../song/songItem/SongItem";
import AlbumItem from "../../album/albumItem/AlbumItem";

function User(props) {
  const navigate = useNavigate();
  const {
    albums,
    userSongs,
    playMusic,
    getImageURL
  } = props;

  return (
    <div className="container container-fluid">
      <div className="row">
        <div className="col">
          <h1>Your albums:</h1>
          <br />
          {albums !== null && albums.length > 0 ? (
            albums.map((item) => (
              <AlbumItem item={item} playMusic={playMusic} getImageURL={getImageURL}/>
            ))
          ) : (
            <p>There are no albums we can show</p>
          )}
        </div>
      </div>
      <div className="row">
        <div className="col">
          <button onClick={() => navigate("/album/add")}>
            Create a new album
          </button>
        </div>
      </div>
      <div className="row">
        <div className="col">
          <h1>Your songs:</h1>
          <br />
          {userSongs !== null && userSongs.length > 0 ? (
            userSongs.map((item) => (
              <SongItem item={item} playMusic={playMusic} getImageURL={getImageURL}/>
            ))
          ) : (
            <p>There are no songs we can show</p>
          )}
        </div>
      </div>
      <div className="row">
        <div className="col">
          <button onClick={() => navigate("/song/add")}>
            Upload a new song
          </button>
        </div>
      </div>
    </div>
  );
}

export default User;
