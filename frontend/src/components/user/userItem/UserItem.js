import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { followUser, getImage } from "../../../services/controller";
import { resolve } from "../../../services/utils";

const UserItem = (props) => {
  const navigate = useNavigate();
  const { item } = props;
  const [imgURL, setimgURL] = useState("");

  useEffect(() => {
    const fetch = async () =>
      getImage(item.profilePicture, resolve(setimgURL));
    fetch();
  }, [item.profilePicture]);

  return (
    <div className="container mt-5 mb-5 d-flex justify-content-between align-items-center rounded">
      <img width="50px" height="50px" src={imgURL} alt="" className="profile" />
      <div className="d-flex align-items-center">
        <a href="" onClick={() => navigate(`/users/${item.id}`)}>
          <h2 className="text-white">{item.username}</h2>
        </a>
      </div>
      <div>
        <a
          href=""
          className="d-flex align-items-center"
          onClick={() => followUser(item.id, () => {})}
        >
          Follow
        </a>
      </div>
    </div>
  );
};

export default UserItem;
