import { React, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getImage, removeSong } from "../../../services/controller";
import { resolve } from "../../../services/utils";

const Song = (props) => {
  const navigate = useNavigate();
  const { song, playMusic } = props;
  const [imgURL, setimgURL] = useState(null);

  useEffect(() => {
    const fetch = async () =>
      getImage(song.image, resolve(setimgURL));

    if (song) {
      fetch();
    }
  }, [song]);

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
                      src={imgURL}
                      alt=""
                      className="profile"
                    />
                  </button>
                </div>
                <div>
                  <button
                    className="custom-button"
                    onClick={() =>
                      removeSong(song.id, () => {
                        navigate("/profile");
                      })
                    }
                  >
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
                  <ion-icon
                    name={
                      song.protectionType === "PRIVATE"
                        ? "lock-closed-outline"
                        : song.protectionType === "PUBLIC"
                        ? "lock-open-outline"
                        : "link-outline"
                    }
                  ></ion-icon>
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
};

export default Song;
