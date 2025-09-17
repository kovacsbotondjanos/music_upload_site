import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { getImage } from "../../../services/controller";
import { resolve } from "../../../services/utils";

const AlbumItem = (props) => {
  const navigate = useNavigate();
  const { item } = props;
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
      <img width="50px" height="50px" src={`${imgURL}`} alt="" className="profile" />
      <div className="d-flex align-items-center">
        <a onClick={() => navigate(`/albums/${item.id}`)}>
          <h2 className="text-white">{item.name}</h2>
        </a>
      </div>
    </div>
  );
}

export default AlbumItem;
