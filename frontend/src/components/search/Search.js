import { useState } from "react";
import SongItem from "../song/songItem/SongItem";
import AlbumItem from "../album/albumItem/AlbumItem";
import UserItem from "../user/userItem/UserItem";
import { searchSongs, searchAlbums, getUsers } from "../../services/controller";
import { resolve } from "../../services/utils";

const Search = (props) => {
  const [searchTerm, setSearchTerm] = useState("");
  const [searchType, setSearchType] = useState("SONG");
  const [songs, setSongs] = useState([]);
  const [albums, setAlbums] = useState([]);
  const [users, setUsers] = useState([]);
  const { playMusic } = props;

  const fetchSongs = async (name) => searchSongs(name, resolve(setSongs));
  const fetchAlbums = async (name) => searchAlbums(name, resolve(setAlbums));
  const fetchUsers = async (name) => getUsers(name, resolve(setUsers));

  const handleDropdownChange = (e) => {
    setSongs([]);
    setAlbums([]);
    setUsers([]);
    setSearchType(e.target.value);
  };

  const handleInputChange = (event) => {
    setSearchTerm(event.target.value);
    const name = event.target.value;
    if (name !== "") {
      if (searchType === "SONG") {
        fetchSongs(name);
      } else if (searchType === "ALBUM") {
        fetchAlbums(name);
      } else if (searchType === "USER") {
        fetchUsers(name);
      }
    }
  };

  const handleSubmit = (event) => {
    setSearchTerm(event.target.value);
    event.preventDefault();
    if (searchTerm !== "") {
      searchSongs(searchTerm);
      searchAlbums(searchTerm);
      getUsers(searchTerm);
    }
  };

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
                      <label className="button-label">Search type</label>
                      <br />
                      <select
                        value={searchType}
                        onChange={handleDropdownChange}
                        className="form-select"
                      >
                        <option value="SONG">Song</option>
                        <option value="ALBUM">Album</option>
                        <option value="USER">User</option>
                      </select>
                    </div>
                  </div>
                  <div className="col">
                    <div className="input-box">
                      <label className="button-label">Name</label>
                      <br />
                      <input
                        type="text"
                        id="search"
                        name="search"
                        value={searchTerm}
                        onChange={handleInputChange}
                      />
                    </div>
                  </div>
                </div>
              </form>
            </div>
          </div>
          {searchTerm === "SONG" ? <h2>Songs:</h2> : null}
          {searchTerm === "ALBUM" ? <h2>Albums:</h2> : null}
          {searchTerm === "USER" ? <h2>Users:</h2> : null}
          <div>
            {songs !== null &&
              songs.map((item) => (
                <SongItem item={item} playMusic={playMusic} />
              ))}
          </div>
          <div>
            {albums.map((item) => (
              <AlbumItem item={item} playMusic={playMusic} />
            ))}
          </div>
          <div>
            {users.map((item) => (
              <UserItem item={item} playMusic={playMusic} />
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export default Search;
