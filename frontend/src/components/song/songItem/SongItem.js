import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { getImage } from "../../../services/controller";
import { resolve } from "../../../services/utils";

const SongItem = (props) => {
  const navigate = useNavigate();
  const { playMusic, item } = props;
  const [imgURL, setimgURL] = useState("");

  useEffect(() => {
    const fetch = async () =>
      getImage(item.image, resolve(setimgURL));
    if (item) {
      fetch();
    }
  }, [item]);

  return (
    <div className="container mt-5 mb-5 d-flex justify-content-between align-items-center rounded">
      <button
        className="custom-button"
        onClick={() => playMusic(item.nameHashed)}
      >
        <img
          alt=""
          width="50px"
          height="50px"
          src={imgURL}
          className="profile"
        />
      </button>
      <div className="d-flex align-items-center">
        <a onClick={() => navigate(`/songs/${item.id}`)}>
          <h2 className="text-white">{item.name}</h2>
        </a>
      </div>
    </div>
  );
};

export default SongItem;
