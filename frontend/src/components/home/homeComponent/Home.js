import React from "react";
import SongItem from "../../song/songItem/SongItem";

function Home(props) {
  const { songs, playMusic, getImageURL } = props;
  return (
    <div className="container container-fluid">
      <div className="row">
        <div className="col">
          <h1>Recommended songs:</h1>
          <br />
          {songs != null &&
            songs.map((item) => (
              <SongItem item={item} playMusic={playMusic} getImageURL={getImageURL}/>
            ))}
        </div>
      </div>
    </div>
  );
}

export default Home;
