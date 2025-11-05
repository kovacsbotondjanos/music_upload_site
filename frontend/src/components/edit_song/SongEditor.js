import { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { getSong, patchSong } from "../../services/controller";

const SongEditor = () => {
  const navigate = useNavigate();
  const { songId } = useParams();
  const [song, setSong] = useState(null);

  useEffect(() => {
    const fetch = async () =>
      getSong(songId, (data) => {
        if (data) {
          setSong(data);
        }
      });
    fetch();
  }, [songId]);

  const handleSubmit = async (event) => {
    event.preventDefault();
    const formData = new FormData(event.target);
    patchSong(songId, formData, () => navigate("/profile"));
  };

  return (
    <div className="black-bg">
      <div className="container container-fluid">
        <div className="row">
          <div className="wrapper">
            <div className="form-box login">
              <div className="container">
                <div className="row">
                  <div className="col">
                    <h2>Path a Song</h2>
                  </div>
                </div>
              </div>
              {song && (
                <div className="container">
                  <div className="row">
                    <form onSubmit={handleSubmit} encType="multipart/form-data">
                      <div className="row">
                        <div className="col">
                          <div className="input-box">
                            <label className="button-label">Name</label>
                            <br />
                            <span className="icon">
                              <ion-icon name="pricetag-outline"></ion-icon>
                            </span>
                            <input
                              type="text"
                              id="name"
                              name="name"
                              required
                              defaultValue={song.name}
                            />
                          </div>
                        </div>
                      </div>

                      <div className="row">
                        <div className="col">
                          <div className="input-box">
                            <label className="button-label">Song cover</label>
                            <br />
                            <span className="icon">
                              <ion-icon name="image-outline"></ion-icon>
                            </span>
                            <input type="file" id="image" name="image" />
                          </div>
                        </div>
                      </div>

                      <div className="row">
                        <div className="col">
                          <div className="input-box">
                            <span className="icon">
                              <ion-icon name="lock-closed-outline"></ion-icon>
                            </span>
                            <label className="button-label">
                              Protection type
                            </label>
                            <br />
                            <select
                              name="protectionType"
                              id="protectionType"
                              defaultValue={song.protectionType}
                            >
                              <option value="PRIVATE">PRIVATE</option>
                              <option value="PROTECTED">PROTECTED</option>
                              <option value="PUBLIC">PUBLIC</option>
                            </select>
                          </div>
                        </div>
                      </div>

                      <div className="row">
                        <div className="col">
                          <div className="login-button">
                            <button
                              type="submit"
                              id="submit-btn"
                              className="button login-button"
                            >
                              Save changes
                            </button>
                          </div>
                        </div>
                      </div>
                    </form>
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default SongEditor;
