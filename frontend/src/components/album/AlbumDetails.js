import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import Album from "./albumComponent/Album";
import { getAlbum } from "../../services/controller";
import { resolve } from "../../services/utils";

const AlbumDetail = () => {
  const { albumId } = useParams();
  const [album, setAlbum] = useState(null);

  useEffect(() => {
    const fetch = async () =>
      await getAlbum(albumId, resolve(setAlbum));
    fetch();
  }, [albumId]);

  return (
    <div className="black-bg">
      <Album album={album} />
    </div>
  );
}

export default AlbumDetail;
