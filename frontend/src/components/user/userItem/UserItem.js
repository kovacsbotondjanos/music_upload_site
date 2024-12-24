import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";

function UserItem(props) {
  const navigate = useNavigate();
  const { getImageURL, item, follow } = props;
  const [imgURL, setimgURL] = useState("");

  useEffect(() => {
    fetchImg();
  }, []);

  const fetchImg = () => {
    getImageURL(item.profilePicture)
      .then((resp) => {
        setimgURL(resp);
      })
      .catch((err) => {
        console.error(err);
      });
  };

  return (
    <div className="container mt-5 mb-5 d-flex justify-content-between align-items-center rounded">
      <img width="50px" height="50px" src={imgURL} alt="" className="profile" />
      <div className="d-flex align-items-center">
        <a onClick={() => navigate(`/users/${item.id}`)}>
          <h2 className="text-white">{item.username}</h2>
        </a>
      </div>
      <div>
        <a
          className="d-flex align-items-center"
          onClick={() => follow(item.id)}
        >
          Follow
        </a>
      </div>
    </div>
  );
}

export default UserItem;
