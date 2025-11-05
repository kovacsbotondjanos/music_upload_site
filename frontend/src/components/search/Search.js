import { useState } from "react";
import SongItem from "../song/songItem/SongItem";
import AlbumItem from "../album/albumItem/AlbumItem";
import UserItem from "../user/userItem/UserItem";
import {
  searchSongs,
  searchAlbums,
  searchUser,
} from "../../services/controller";
import InfiniteScroll from "react-infinite-scroll-component";

const Search = (props) => {
  const [searchTerm, setSearchTerm] = useState("");
  const [searchType, setSearchType] = useState("SONG");
  const [songs, setSongs] = useState([]);
  const [albums, setAlbums] = useState([]);
  const [users, setUsers] = useState([]);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const { playMusic } = props;

  const fetchSongs = async (name) =>
    searchSongs(page, name, (data) => {
      if (!data || data.length === 0) {
        setHasMore(false);
      } else {
        setSongs((prev) => [...prev, ...data]);
        setPage((prev) => prev + 1);
      }
    });
  const fetchAlbums = async (name) =>
    searchAlbums(page, name, (data) => {
      if (!data || data.length === 0) {
        setHasMore(false);
      } else {
        setAlbums((prev) => [...prev, ...data]);
        setPage((prev) => prev + 1);
      }
    });
  const fetchUsers = async (name) =>
    searchUser(page, name, (data) => {
      if (!data || data.length === 0) {
        setHasMore(false);
      } else {
        setUsers((prev) => [...prev, ...data]);
        setPage((prev) => prev + 1);
      }
    });

  const handleDropdownChange = (e) => {
    setSongs([]);
    setAlbums([]);
    setUsers([]);
    setPage(0);
    setHasMore(true);
    setSearchType(e.target.value);
  };

  const handleScrollDown = async () => {
    if (searchType === "SONG") {
      await fetchSongs(page, searchTerm);
    } else if (searchType === "ALBUM") {
      await fetchAlbums(page, searchTerm);
    } else if (searchType === "USER") {
      await fetchUsers(page, searchTerm);
    }
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
      searchUser(searchTerm);
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
                <InfiniteScroll
                  dataLength={songs.length}
                  next={handleScrollDown}
                  hasMore={hasMore}
                  loader={<h4>Loading songs...</h4>}
                  endMessage={<p>No more songs</p>}
                >
                  <SongItem item={item} playMusic={playMusic} />
                </InfiniteScroll>
              ))}
          </div>
          <div>
            {albums.map((item) => (
              <InfiniteScroll
                dataLength={songs.length}
                next={handleScrollDown}
                hasMore={hasMore}
                loader={<h4>Loading songs...</h4>}
                endMessage={<p>No more songs</p>}
              >
                <AlbumItem item={item} playMusic={playMusic} />
              </InfiniteScroll>
            ))}
          </div>
          <div>
            {users.map((item) => (
              <InfiniteScroll
                dataLength={songs.length}
                next={handleScrollDown}
                hasMore={hasMore}
                loader={<h4>Loading songs...</h4>}
                endMessage={<p>No more songs</p>}
              >
                <UserItem item={item} playMusic={playMusic} />
              </InfiniteScroll>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export default Search;
