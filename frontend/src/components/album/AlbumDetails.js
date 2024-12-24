import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import Album from "./albumComponent/Album";
import Player from "../player/Player";

function AlbumDetail(props) {
  const { albumId } = useParams();
  const [album, setAlbum] = useState(null);
  const { getImageURL } = props
  useEffect(() => {
    fetchAlbum();
  }, []);

  const fetchAlbum = () =>
    fetch(`http://localhost:8080/api/v1/albums/${albumId}`, {
        method: "GET",
        headers: {
        "Content-Type": "application/json",
        },
        credentials: "include",
    })
        .then((response) => {
        if (!response.ok) {
            throw new Error("Network response was not ok " + response.statusText);
        }
        return response.json();
        })
        .then((data) => {
        setAlbum(data);
        })
        .catch((error) => {
        console.error("There was a problem with the fetch operation:", error);
        });


  return (
    <div className="black-bg">
      <Album album={album} getImageURL={getImageURL}/>
    </div>
  );
}

export default AlbumDetail;
