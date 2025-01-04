import React, { useState, useEffect }from "react";
import { useParams } from "react-router-dom";
import Song from "./songComponent/Song";

function SongDetail(props) {
  const { songId } = useParams();
  const { playMusic, getImageURL } = props;
  const [song, setSong] = useState(null);

  useEffect(() => {
    fetchSong();
  }, []);

  const fetchSong = () => fetch(`http://localhost:8080/api/v1/songs/${songId}`, {
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
      setSong(data);
    })
    .catch((error) => {
      console.error("There was a problem with the fetch operation:", error);
    });

  return (
    <div className="black-bg">
      <Song song={song} playMusic={playMusic} getImageURL={getImageURL} />
    </div>
  );
}

export default SongDetail;
