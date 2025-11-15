import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { addAlbum, getToken } from "../../services/controller";

const UploadAlbum = () => {
  const navigate = useNavigate();

  const handleSubmit = async (event) => {
    event.preventDefault();
    const formData = new FormData(event.target);
    addAlbum(formData, () => navigate("/profile"));
  };

  useEffect(() => {
    const token = getToken();
    if (!token) {
      navigate("/");
    }
  });

  return (
    <div className="black-bg">
      <div className="container container-fluid">
        <div className="row">
          <div className="wrapper">
            <div className="form-box login">
              <div className="container">
                <div className="row">
                  <div className="col">
                    <h2>Upload a new Song</h2>
                  </div>
                </div>
              </div>
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
                          <input type="text" id="name" name="name" required />
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
                          <select name="protectionType" id="protectionType">
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
                            Create a new album
                          </button>
                        </div>
                      </div>
                    </div>
                  </form>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default UploadAlbum;
