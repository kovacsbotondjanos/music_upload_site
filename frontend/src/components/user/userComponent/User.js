import { useNavigate } from "react-router-dom";
import { React, useState, useEffect } from "react";
import SongItem from "../../song/songItem/SongItem";
import AlbumItem from "../../album/albumItem/AlbumItem";
import { getSongs, getAlbums } from "../../../services/controller";

const User = (props) => {
  const navigate = useNavigate();
  const [userSongs, setUserSongs] = useState([]);
  const [albums, setAlbums] = useState([]);
  const { playMusic } = props;

  useEffect(() => {
    const fetch = async () =>
      await getSongs((data) => {
        setUserSongs(data);
      });
    fetch();
  }, []);

  useEffect(() => {
    const fetch = async () =>
      await getAlbums((data) => {
        if (data) {
          setAlbums(data);
        }
      });
    fetch();
  }, []);

  return (
    <div className="container container-fluid">
      <div className="row">
        <div className="col">
          <h1>Your albums:</h1>
          <br />
          {albums !== null && albums.length > 0 ? (
            albums.map((item) => (
              <AlbumItem item={item} playMusic={playMusic} />
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
              <SongItem item={item} playMusic={playMusic} />
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
};

export default User;
