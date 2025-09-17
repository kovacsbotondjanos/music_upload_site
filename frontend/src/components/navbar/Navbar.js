import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { getImage, getUserData, logout } from "../../services/controller";
import { resolve } from "../../services/utils";

const Navbar = (props) => {
  const navigate = useNavigate();
  const [imgURL, setimgURL] = useState("");
  const {
    loggedIn,
    profilePic,
    setLoggedIn,
    setUsername,
    setProfilePic,
  } = props;

  useEffect(() => {
      const fetch = async () =>
        getUserData((data) => {
          if (data) {
            setLoggedIn(true);
            setUsername(data.username);
            setProfilePic(data.profilePicture);
          }
        });
      fetch();
    }, [setLoggedIn, setUsername, setProfilePic]);

  useEffect(() => {
      const fetch = async () =>
        getImage(profilePic, resolve(setimgURL));
      fetch();
  }, [profilePic]);

  return (
    <nav className="navbar navbar-expand">
      <div className="container-fluid">
        <div className="row display-flex">
          {loggedIn && (
            <div className="collapse navbar-collapse">
              <a className="justify-content profile_div" href="/profile">
                <img
                  alt=""
                  width="50px"
                  height="50px"
                  src={`${imgURL}`}
                  className="profile"
                />
              </a>
            </div>
          )}
          <div className="collapse navbar-collapse">
            <div className="navbar-nav">
              <a className="nav-link active text-white" href="/">
                Home
              </a>
            </div>
          </div>
          {loggedIn && (
            <div className="collapse navbar-collapse justify-content-end">
              <div className="navbar-nav">
                <a
                  href=""
                  onClick={() =>
                    logout(() => {
                      setLoggedIn(false);
                      setProfilePic(null);
                      setUsername(null);
                      navigate("/login");
                    })
                  }
                  className="custom-button nav-link active text-white"
                >
                  Log out
                </a>
              </div>
            </div>
          )}
          {!loggedIn && (
            <div
              className="collapse navbar-collapse justify-content-end"
              id="navbarNavAltMarkup"
            >
              <div className="navbar-nav">
                <a className="nav-link active text-white" href="/register">
                  Register
                </a>
              </div>
            </div>
          )}
          {!loggedIn && (
            <div className="collapse navbar-collapse justify-content-end">
              <div className="navbar-nav">
                <a className="nav-link active text-white" href="/login">
                  Log in
                </a>
              </div>
            </div>
          )}
          <button onClick={() => navigate("/search")}>
            <ion-icon name="search-outline"></ion-icon>
          </button>
        </div>
      </div>
    </nav>
  );
}

export default Navbar;
