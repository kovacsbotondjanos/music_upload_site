import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { getTags } from "../../services/controller";
import { resolve } from "../../services/utils";
import TagItem from "../tag/TagItem";

const UploadSong = () => {
  const navigate = useNavigate();

  const [searchTerm, setSearchTerm] = useState("");
  const [tags, setTags] = useState([]);
  const [selectedTags, setSelectedTags] = useState([]);

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
  }

  const handleSubmit = async (event) => {
    event.preventDefault();

    const formData = new FormData(event.target);

    if (selectedTags.length > 0) {
      formData.append(
        "tags",
        selectedTags.length > 0 ? selectedTags.join(",") : ""
      );
    }

    try {
      const response = await fetch("http://localhost:30002/api/v1/songs", {
        method: "POST",
        body: formData,
        credentials: "include",
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      navigate("/profile");
    } catch (error) {
      console.error("Error:", error);
    }
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
                        <div className="input-box">
                          <label className="button-label">MP3 file</label>
                          <br />
                          <span className="icon">
                            <ion-icon name="musical-notes-outline"></ion-icon>
                          </span>
                          <input type="file" id="song" name="song" required />
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
                          <select name="protectionType" id="protection_type">
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
                          />
                        </div>
                      </div>
                    </div>
                    <div>
                      <div className="bg-secondary p-2 rounded">
                        {tags.map((item) => (
                          <div className="row">
                            <div className="col">
                              <a className="text-white" onClick={() => addTag(item.name)}>
                                {item.name}
                              </a>
                            </div>
                          </div>
                        ))}
                      </div>
                      {selectedTags.map((item, index) => (
                        <TagItem item={item} index={index + 1} removeTag={removeTag} />
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
