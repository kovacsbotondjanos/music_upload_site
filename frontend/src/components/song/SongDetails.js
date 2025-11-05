import { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import Song from "./songComponent/Song";
import { getSong } from "../../services/controller";

const SongDetail = (props) => {
  const { songId } = useParams();
  const { playMusic, currentUserId } = props;
  const [song, setSong] = useState(null);

  useEffect(() => {
    const fetch = async () =>
      await getSong(songId, (data) => {
        if (data) {
          setSong(data);
        }
      });
    fetch();
  }, [songId]);

  return (
    <div className="black-bg">
      {song == null ? null : (
        <Song song={song} playMusic={playMusic} currentUserId={currentUserId} />
      )}
    </div>
  );
};

export default SongDetail;
