import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import SongItem from "../../song/songItem/SongItem";
import { formatStringToDate } from "../../../services/utils";
import {
  removeAlbum,
  getRecommendedSongsForAlbum,
} from "../../../services/controller";
import InfiniteScroll from "react-infinite-scroll-component";

const Album = (props) => {
  const navigate = useNavigate();
  const { album, playMusic, currentUserId } = props;
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [recommendedSongs, setSongs] = useState([]);

  var fetchSongs = async () => {
    await getRecommendedSongsForAlbum(page, album.id, (data) => {
      if (!data || data.length === 0) {
        setHasMore(false);
      } else {
        setSongs((prev) => [...prev, ...data]);
        setPage((prev) => prev + 1);
      }
    });
  };

  useEffect(() => {
    if (album) {
      fetchSongs();
    }
  }, [album]);

  return (
    <div className="container container-fluid">
      <div className="row">
        <div className="col">
          {album != null ? (
            <div>
              <div className="row">
                <div className="col">
                  <h1>{"name: " + album.name}</h1>
                </div>
                <div className="col ms-auto text-end">
                  <img
                    width="50px"
                    height="50px"
                    src={`${album.image}`}
                    alt=""
                    className="profile"
                  />
                </div>
                {album.userId === currentUserId ? (
                  <div>
                    <button
                      className="custom-button"
                      onClick={() =>
                        removeAlbum(album.id, () => {
                          navigate("/profile");
                        })
                      }
                    >
                      <ion-icon name="trash-outline"></ion-icon>
                    </button>
                    <button
                      className="custom-button"
                      onClick={() => navigate("/albums/edit/" + album.id)}
                    >
                      <ion-icon name="create"></ion-icon>
                    </button>
                  </div>
                ) : null}
              </div>

              <div>
                <div className="col">
                  <h1>{"creation date: " + formatStringToDate(album.createdAt)}</h1>
                </div>
              </div>

              <div>
                <div className="col">
                  <h1>{"created by: " + album.username}</h1>
                </div>
              </div>

              <div>
                <div className="col">
                  <ion-icon
                    name={
                      album.protectionType === "PRIVATE"
                        ? "lock-closed-outline"
                        : album.protectionType === "PUBLIC"
                        ? "lock-open-outline"
                        : "link-outline"
                    }
                  ></ion-icon>
                </div>
              </div>

              <div>
                <div className="col">
                  {album.songs !== null && album.songs.length > 0
                    ? album.songs.map((item) => (
                        <SongItem item={item} playMusic={playMusic} />
                      ))
                    : "No songs present"}
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
            <p>Sorry this album is not available</p>
          )}
          <br />
        </div>
      </div>
    </div>
  );
};

export default Album;
