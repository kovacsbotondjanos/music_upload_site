import React from "react";
import { useNavigate } from "react-router-dom";

function Song(props) {
  const navigate = useNavigate();
  const { song, playMusic, getImageURL } = props;

  function removeSong(id) {
    fetch(`http://localhost:8080/api/v1/songs/${id}`, {
    method: "DELETE",
    headers: {
      "Content-Type": "application/json",
    },
    credentials: "include",
    })
    .then((response) => {
      if (!response.ok) {
        throw new Error("Network response was not ok " + response.statusText);
      }
    })
    .then(() => {
        navigate("/profile");
    })
    .catch((error) => {
      console.error("There was a problem with the fetch operation:", error);
    });
  }

  return (
    <div className="container container-fluid">
      <div className="row">
        <div className="col">
          {song != null ? (
            <div>
              <div className="row">
                <div className="col">
                  <h1>{song.name}</h1>
                </div>
                <div className="col ms-auto text-end">
                  <button
                    className="custom-button"
                    onClick={() => playMusic(song.nameHashed)}
                  >
                    <img
                      width="50px"
                      height="50px"
                      src={getImageURL(song.image)}
                      alt=""
                      className="profile"
                    />
                  </button>
                </div>
                <div>
                    <button className="custom-button" onClick={() => removeSong(song.id)}>
                        <ion-icon name="trash-outline"></ion-icon>
                    </button>
                </div>
              </div>

              <div>
                <div className="col">
                  <h1>{song.createdAt}</h1>
                </div>
              </div>
              
              <div>
                <div className="col">
                  <ion-icon name={song.protectionType === "PRIVATE" ? "lock-closed-outline" : song.protectionType === "PUBLIC" ? "lock-open-outline" : "link-outline"}></ion-icon>
                </div>
              </div>

              <div>
                <div className="col">
                  <h1>{song.userId}</h1>
                </div>
              </div>
            </div>
          ) : (
            <p>Sorry this song is not available</p>
          )}
          <br />
        </div>
      </div>
    </div>
  );
}

export default Song;
