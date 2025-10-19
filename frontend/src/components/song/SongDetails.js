import { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import Song from "./songComponent/Song";
import { getSong } from "../../services/controller";
import { resolve } from "../../services/utils";

const SongDetail = (props) => {
  const { songId } = useParams();
  const { playMusic } = props;
  const [song, setSong] = useState(null);

  useEffect(() => {
    const fetch = async () =>
      await getSong(songId, resolve(setSong));
    fetch();
  }, [songId]);

  return (
    <div className="black-bg">
      { song == null ? null : <Song song={song} playMusic={playMusic} /> }
    </div>
  );
}

export default SongDetail;
