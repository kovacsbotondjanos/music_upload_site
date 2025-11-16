import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { addSong, getTags, getToken } from "../../services/controller";
import TagItem from "../tag/TagItem";

const UploadSong = () => {
  const navigate = useNavigate();

  const [searchTerm, setSearchTerm] = useState("");
  const [tags, setTags] = useState([]);
  const [selectedTags, setSelectedTags] = useState([]);
  const [songFile, setSongFile] = useState(null);
  const [imagePreview, setImagePreview] = useState(null);

  useEffect(() => {
    const token = getToken();
    if (!token) {
      navigate("/");
    }
  });

  const handleInputChange = (event) => {
    setSearchTerm(event.target.value);
    const name = event.target.value;
    if (name !== "") {
      getTags(name, (data) => {
        if (data) {
          setTags((prevTags) => {
            const existingNames = prevTags.map((tag) => tag.name);
            if (!existingNames.includes(name)) {
              return [...data, { name }];
            }
            return data;
          });
        }
      });
    }
  };

  const addTag = (tag) => {
    setSelectedTags((prevTags) => {
      if (prevTags.includes(tag)) {
        return prevTags.filter((t) => t !== tag);
      }

      if (prevTags.length < 3) {
        return [...prevTags, tag];
      }

      return prevTags;
    });
  };

  const removeTag = (tag) => {
    setSelectedTags((prevTags) => {
      return prevTags.filter((t) => t !== tag);
    });
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    const formData = new FormData(event.target);

    if (selectedTags.length > 0) {
      formData.append(
        "tags",
        selectedTags.length > 0 ? selectedTags.join(",") : ""
      );
    }
    addSong(formData, () => navigate("/profile"));
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
                        <label className="button-label">MP3 file</label>
                        <div
                          className="upload-box"
                          onClick={() =>
                            document.getElementById("song").click()
                          }
                        >
                          <ion-icon name="musical-notes-outline"></ion-icon>
                          <p>
                            {songFile
                              ? songFile.name
                              : "Click to upload MP3..."}
                          </p>
                          <input
                            type="file"
                            id="song"
                            name="song"
                            accept=".mp3"
                            onChange={(e) => setSongFile(e.target.files[0])}
                            hidden
                            required
                          />
                        </div>
                      </div>

                      <div className="col">
                        <label className="button-label">Song cover</label>
                        <div
                          className="upload-box"
                          onClick={() =>
                            document.getElementById("image").click()
                          }
                        >
                          {imagePreview ? (
                            <img src={imagePreview} className="preview-img" />
                          ) : (
                            <>
                              <ion-icon name="image-outline"></ion-icon>
                              <p>Click to upload image...</p>
                            </>
                          )}
                          <input
                            type="file"
                            id="image"
                            name="image"
                            accept=".jpg,.jpeg,.png"
                            onChange={(e) => {
                              if (e.target.files[0]) {
                                const url = URL.createObjectURL(
                                  e.target.files[0]
                                );
                                setImagePreview(url);
                              }
                            }}
                            hidden
                          />
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
                        <div className="input-box">
                          <label className="button-label">Tag</label>
                          <br />
                          <input
                            type="text"
                            id="name"
                            name="name"
                            value={searchTerm}
                            onChange={handleInputChange}
                            className="tag-input"
                            placeholder="Search tags..."
                          />
                          {searchTerm && (
                            <div id="scrollableResults">
                              {tags.map((item) => (
                                <div
                                  key={item.name}
                                  className="scrollable-result-item"
                                  onClick={() => addTag(item.name)}
                                >
                                  {item.name}
                                </div>
                              ))}
                            </div>
                          )}
                        </div>
                      </div>
                    </div>
                    <div>
                      {selectedTags.map((item, index) => (
                        <TagItem
                          item={item}
                          index={index + 1}
                          removeTag={removeTag}
                        />
                      ))}
                    </div>

                    <div className="row">
                      <div className="col">
                        <div className="login-button">
                          <button
                            type="submit"
                            id="submit-btn"
                            className="button login-button"
                          >
                            Add new song
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

export default UploadSong;
