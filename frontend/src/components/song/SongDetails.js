import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import Song from "./songComponent/Song";
import { fetchSong } from "../../services/controller";

function SongDetail(props) {
  const { songId } = useParams();
  const { playMusic } = props;
  const [song, setSong] = useState(null);

  useEffect(() => {
    const fetch = async () =>
      fetchSong(songId, (data) => {
        if (data) {
          setSong(data);
        }
      });
    fetch();
  }, []);

  return (
    <div className="black-bg">
      <Song song={song} playMusic={playMusic} />
    </div>
  );
}

export default SongDetail;
