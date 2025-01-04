import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";

function AlbumItem(props) {
  const navigate = useNavigate();
  const { getImageURL, item } = props;
  const [imgURL, setimgURL] = useState("");

  useEffect(() => {
    fetchImg();
  }, []);

  const fetchImg = () => {
    getImageURL(item.image)
      .then((resp) => {
        setimgURL(resp);
      })
      .catch((err) => {
        console.error(err);
      });
  };

  return (
    <div className="container mt-5 mb-5 d-flex justify-content-between align-items-center rounded">
                  <img
                      width="50px"
                      height="50px"
                      src={imgURL}
                      alt=""
                      className="profile"
                  />
                  <div className="d-flex align-items-center">
                  <a onClick={() => navigate(`/albums/${item.id}`)}>
                      <h2 className="text-white">{item.name}</h2>
                  </a>
                  </div>
              </div>
  );
}

export default AlbumItem;
