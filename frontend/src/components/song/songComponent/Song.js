import { useState, useEffect } from "react";
import SongItem from "../songItem/SongItem";
import { useNavigate } from "react-router-dom";
import {
  removeSong,
  getRecommendedSongsForSong,
} from "../../../services/controller";
import InfiniteScroll from "react-infinite-scroll-component";
import { formatStringToDate } from "../../../services/utils";

const Song = (props) => {
  const navigate = useNavigate();
  const { song, playMusic, currentUserId } = props;
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [recommendedSongs, setSongs] = useState([]);

  var fetchSongs = async () => {
    await getRecommendedSongsForSong(page, song.id, (data) => {
      if (!data || data.length === 0) {
        setHasMore(false);
      } else {
        setSongs((prev) => [...prev, ...data]);
        setPage((prev) => prev + 1);
      }
    });
  };

  useEffect(() => {
    setPage(0);
    setHasMore(true);
    setSongs([]);
    if (song) {
      fetchSongs();
    }
  }, [song]);

  return (
    <div className="container container-fluid">
      <div className="row">
        <div className="col">
          {song != null ? (
            <div>
              <div className="row">
                <div className="col">
                  <h1>{"name: " + song.name}</h1>
                </div>
                <div className="col ms-auto text-end">
                  <button
                    className="custom-button"
                    onClick={() => playMusic(song.nameHashed)}
                  >
                    <img
                      width="50px"
                      height="50px"
                      src={`${song.image}`}
                      alt=""
                      className="profile"
                    />
                  </button>
                </div>
                {song.userId === currentUserId ? (
                  <div>
                    <button
                      className="custom-button"
                      onClick={() =>
                        removeSong(song.id, () => {
                          navigate("/profile");
                        })
                      }
                    >
                      <ion-icon name="trash-outline"></ion-icon>
                    </button>
                    <button
                      className="custom-button"
                      onClick={() => navigate("/songs/edit/" + song.id)}
                    >
                      <ion-icon name="create"></ion-icon>
                    </button>
                  </div>
                ) : null}
              </div>

              <div>
                <div className="col">
                  <h1>{"creation date: " + formatStringToDate(song.createdAt)}</h1>
                </div>
              </div>

              <div>
                <div className="col">
                  <h1>{"artist: " + song.username}</h1>
                </div>
              </div>

              <div>
                <div className="col">
                  <ion-icon
                    name={
                      song.protectionType === "PRIVATE"
                        ? "lock-closed-outline"
                        : song.protectionType === "PUBLIC"
                        ? "lock-open-outline"
                        : "link-outline"
                    }
                  ></ion-icon>
                </div>
              </div>

              <div className="container container-fluid">
                <div className="row">
                  <div className="col">
                    <h1>Recommended songs:</h1>
                    <br />
                    <InfiniteScroll
                      dataLength={recommendedSongs.length}
                      next={fetchSongs}
                      hasMore={hasMore}
                      loader={<h4>Loading songs...</h4>}
                      endMessage={<p>No more songs</p>}
                    >
                      {recommendedSongs !== null &&
                        recommendedSongs.map((item) => (
                          <SongItem item={item} playMusic={playMusic} />
                        ))}
                    </InfiniteScroll>
                  </div>
                </div>
              </div>
            </div>
          ) : (
            <p>Sorry this song is not available</p>
          )}
          <br />
        </div>
      </div>
    </div>
  );
};

export default Song;
