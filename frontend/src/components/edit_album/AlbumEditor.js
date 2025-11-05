import { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { getAlbum, patchAlbum, searchSongs } from "../../services/controller";
import InfiniteScroll from "react-infinite-scroll-component";

const AlbumEditor = () => {
  const navigate = useNavigate();
  const { albumId } = useParams();
  const [album, setAlbum] = useState(null);
  const [query, setQuery] = useState("");
  const [searchResults, setSearchResults] = useState([]);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);

  useEffect(() => {
    const fetch = async () =>
      getAlbum(albumId, (data) => {
        if (data) {
          setAlbum(data);
        }
      });
    fetch();
  }, [albumId]);

  const removeSong = (song) => {
    setAlbum({
      ...album,
      songs: album.songs.filter((s) => s.id !== song.id),
    });
  };

  const fetchSongs = async () => {
    if (!query.trim()) return;
    await searchSongs(page, query, (data) => {
      if (!data || data.length === 0) {
        setHasMore(false);
      } else {
        setSearchResults((prev) => [...prev, ...data]);
        setPage((prev) => prev + 1);
      }
    });
  };

  const handleSearch = async (e) => {
    const val = e.target.value;
    setQuery(val);
    setSearchResults([]);
    setPage(0);
    setHasMore(true);
    if (val.trim()) {
      await fetchSongs();
    }
  };

  const addSongToAlbum = (song) => {
    if (!album.songs.some((s) => s.id === song.id)) {
      setAlbum({
        ...album,
        songs: [...album.songs, song],
      });
    }
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    const formData = new FormData(event.target);
    // formData.append("songIds", JSON.stringify(album.songs.map((s) => s.id)));
    album.songs.forEach((s) => formData.append("songIds", s.id));
    patchAlbum(album.id, formData, () => navigate("/profile"));
  };

  return (
    <div className="black-bg">
      <div className="container container-fluid">
        <div className="row">
          <div className="wrapper">
            <div className="form-box login">
              <div className="container">
                <div className="row">
                  <div className="col">
                    <h2>Path an Album</h2>
                  </div>
                </div>
              </div>
              {album && (
                <div className="container">
                  <div className="row">
                    <form onSubmit={handleSubmit} encType="multipart/form-data">
                      <div className="row">
                        <div className="col">
                          <div className="input-box">
                            <label className="button-label">Name</label>
                            <br />
                            <span className="icon">
                              <ion-icon name="pricetag-outline"></ion-icon>
                            </span>
                            <input
                              type="text"
                              id="name"
                              name="name"
                              required
                              defaultValue={album.name}
                            />
                          </div>
                        </div>
                      </div>

                      <div className="row">
                        <div className="col">
                          <div className="input-box">
                            <label className="button-label">Album cover</label>
                            <br />
                            <span className="icon">
                              <ion-icon name="image-outline"></ion-icon>
                            </span>
                            <input type="file" id="image" name="image" />
                          </div>
                        </div>
                      </div>

                      <div className="row">
                        <div className="col">
                          <div className="input-box">
                            <span className="icon">
                              <ion-icon name="lock-closed-outline"></ion-icon>
                            </span>
                            <label className="button-label">
                              Protection type
                            </label>
                            <br />
                            <select
                              name="protectionType"
                              id="protectionType"
                              defaultValue={album.protectionType}
                            >
                              <option value="PRIVATE">PRIVATE</option>
                              <option value="PROTECTED">PROTECTED</option>
                              <option value="PUBLIC">PUBLIC</option>
                            </select>
                          </div>
                        </div>
                      </div>

                      {album.songs?.length ? (
                        <div>
                          {album.songs.map((song) => (
                            <div>
                              <div className="row">
                                <div className="col">
                                  <h4>{song.name}</h4>
                                </div>
                                <div className="col">
                                  <button
                                    type="button"
                                    onClick={() => removeSong(song)}
                                  >
                                    Remove
                                  </button>
                                </div>
                              </div>
                            </div>
                          ))}
                        </div>
                      ) : null}
                      <p>Add songs:</p>
                      <div className="row mt-4">
                        <div className="col">
                          <label className="button-label">
                            Search and Add Songs
                          </label>
                          <input
                            type="text"
                            value={query}
                            onChange={handleSearch}
                            placeholder="Type song name..."
                            className="form-control"
                          />
                          {query && (
                            <div id="scrollableResults">
                              <InfiniteScroll
                                dataLength={searchResults.length}
                                next={fetchSongs}
                                hasMore={hasMore}
                                scrollableTarget="scrollableResults"
                                loader={
                                  <p style={{ padding: "8px" }}>
                                    Loading more...
                                  </p>
                                }
                                endMessage={
                                  <p style={{ padding: "8px" }}>
                                    No more results
                                  </p>
                                }
                              >
                                {searchResults.map((song) => (
                                  <div
                                    key={song.id}
                                    className="scrollable-result-item"
                                    onClick={() => addSongToAlbum(song)}
                                  >
                                    {" "}
                                    {song.name}
                                  </div>
                                ))}
                              </InfiniteScroll>
                            </div>
                          )}
                        </div>
                      </div>

                      <div className="row">
                        <div className="col">
                          <div className="login-button">
                            <button
                              type="submit"
                              id="submit-btn"
                              className="button login-button"
                            >
                              Save changes
                            </button>
                          </div>
                        </div>
                      </div>
                    </form>
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AlbumEditor;
