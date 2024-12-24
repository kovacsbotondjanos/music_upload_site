import React from "react";
import { useNavigate } from "react-router-dom";

function Album(props) {
  const navigate = useNavigate();
  const { album, getImageURL } = props;

  function removeAlbum(id) {
    fetch(`http://localhost:8080/api/v1/albums/${id}`, {
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
          {album != null ? (
            <div>
              <div className="row">
                <div className="col">
                  <h1>{album.name}</h1>
                </div>
                <div class="col ms-auto text-end">
                  <img
                    width="50px"
                    height="50px"
                    src={getImageURL(album.image)}
                    alt=""
                    className="profile"
                  />
                </div>
                <div>
                  <button
                    className="custom-button"
                    onClick={() => removeAlbum(album.id)}
                  >
                    <ion-icon name="trash-outline"></ion-icon>
                  </button>
                </div>
              </div>

              <div>
                <div className="col">
                  <h1>{album.createdAt}</h1>
                </div>
              </div>

              <div>
                <div className="col">
                  <h1>{album.songs.length}</h1>
                </div>
              </div>

              <div>
                <div className="col">
                  <ion-icon name={album.protectionType === "PRIVATE" ? "lock-closed-outline" : album.protectionType === "PUBLIC" ? "lock-open-outline" : "link-outline"}></ion-icon>
                </div>
              </div>

              <div>
                <div className="col">
                  <h1>{album.userId}</h1>
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

export default Album;
