import { useState, useEffect } from "react";
import SongItem from "../../song/songItem/SongItem";
import { getRecommendedSongs } from "../../../services/controller";
import InfiniteScroll from "react-infinite-scroll-component";

const Home = (props) => {
  const [songs, setSongs] = useState([]);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const { playMusic } = props;

  var fetchSongs = async () => {
    await getRecommendedSongs(page, (data) => {
      if (!data || data.length === 0) {
        setHasMore(false);
      } else {
        setSongs((prev) => [...prev, ...data]);
        setPage((prev) => prev + 1);
      }
    });
  };

  useEffect(() => fetchSongs(), []);

  return (
    <div className="container container-fluid">
      <div className="row">
        <div className="col">
          <h1>Recommended songs:</h1>
          <br />
          <InfiniteScroll
            dataLength={songs.length}
            next={fetchSongs}
            hasMore={hasMore}
            loader={<h4>Loading songs...</h4>}
            endMessage={<p>No more songs</p>}
          >
            {songs !== null &&
              songs.map((item) => (
                <SongItem item={item} playMusic={playMusic} />
              ))}
          </InfiniteScroll>
        </div>
      </div>
    </div>
  );
};

export default Home;
