import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import SongItem from "../../song/songItem/SongItem";
import { getImage, removeAlbum, getUser } from "../../../services/controller";
import { formatStringToDate, resolve } from "../../../services/utils";

const Album = (props) => {
  const navigate = useNavigate();
  const { album, playMusic } = props;
  const [user, setUser] = useState(null);
  const [imgURL, setimgURL] = useState(null);

  useEffect(() => {
    const fetch = async () =>
      await getImage(album.image, resolve(setimgURL));
    if (album) {
      fetch();
    }
  }, [album]);

  useEffect(() => {
    const fetch = async () =>
      await getUser(album.userId, resolve(setUser));
    if (album) {
      fetch();
    }
  }, [album]);

  return (
    <div className="container container-fluid">
      <div className="row">
        <div className="col">
          {album != null && user != null ? (
            <div>
              <div className="row">
                <div className="col">
                  <h1>{album.name}</h1>
                </div>
                <div className="col ms-auto text-end">
                  <img
                    width="50px"
                    height="50px"
                    src={imgURL}
                    alt=""
                    className="profile"
                  />
                </div>
                <div>
                  <button
                    className="custom-button"
                    onClick={() =>
                      removeAlbum(album.id, () => {
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
                  <h1>{formatStringToDate(album.createdAt)}</h1>
                </div>
              </div>

              <div>
                <div className="col">
                  <ion-icon
                    name={
                      album.protectionType === "PRIVATE"
                        ? "lock-closed-outline"
                        : album.protectionType === "PUBLIC"
                        ? "lock-open-outline"
                        : "link-outline"
                    }
                  ></ion-icon>
                </div>
              </div>

              <div>
                <div className="col">
                  <h1>{user.username}</h1>
                </div>
              </div>

              
              <div>
                <div className="col">
                {album.songs != null && album.songs.length > 0 
                  ? album.songs.map((item) => <SongItem item={item} playMusic={playMusic} />) 
                  : ("No songs present")}
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

export default Album;
