import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import Album from "./albumComponent/Album";
import { getAlbum } from "../../services/controller";

const AlbumDetail = (props) => {
  const { playMusic, currentUserId } = props;
  const { albumId } = useParams();
  const [album, setAlbum] = useState(null);

  useEffect(() => {
    const fetch = async () =>
      await getAlbum(albumId, (data) => {
        if (data) {
          setAlbum(data);
        }
      });
    fetch();
  }, [albumId]);

  return (
    <div className="black-bg">
      {album == null ? null : (
        <Album
          album={album}
          playMusic={playMusic}
          currentUserId={currentUserId}
        />
      )}
    </div>
  );
};

export default AlbumDetail;
