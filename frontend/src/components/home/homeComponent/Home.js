import { React, useState, useEffect } from "react";
import SongItem from "../../song/songItem/SongItem";
import { getRecommendedSongs } from "../../../services/controller";
import { resolve } from "../../../services/utils";

const Home = (props) => {
  const [songs, setSongs] = useState([]);
  const { playMusic } = props

   useEffect(() => {
      const fetch = async () =>
        await getRecommendedSongs(resolve(setSongs));
      fetch();
    }, []);
  
  return (
    <div className="container container-fluid">
      <div className="row">
        <div className="col">
          <h1>Recommended songs:</h1>
          <br />
          {songs != null &&
            songs.map((item) => <SongItem item={item} playMusic={playMusic} />)}
        </div>
      </div>
    </div>
  );
}

export default Home;
