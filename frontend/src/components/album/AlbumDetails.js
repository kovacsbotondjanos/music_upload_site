import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import Album from "./albumComponent/Album";
import { fetchAlbum } from "../../services/controller";

function AlbumDetail(props) {
  const { albumId } = useParams();
  const [album, setAlbum] = useState(null);

  useEffect(() => {
    const fetch = async () =>
      fetchAlbum(albumId, (data) => {
        if (data) {
          setAlbum(data);
        }
      });
    fetch();
  }, []);

  return (
    <div className="black-bg">
      <Album album={album} />
    </div>
  );
}

export default AlbumDetail;
