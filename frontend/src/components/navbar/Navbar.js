import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";

function Navbar(props) {
  const navigate = useNavigate();
  const [imgURL, setimgURL] = useState("");
  const {
    loggedIn,
    profilePic,
    username,
    setLoggedIn,
    setUsername,
    setProfilePic,
    getImageURL
  } = props;

  useEffect(() => {
    if (profilePic) {
      fetchImg();
    }
  }, [profilePic]);

  const fetchImg = () => {
    getImageURL(profilePic)
    .then((resp) => {
      setimgURL(resp);
    })
    .catch((err) => {
      console.error(err);
    });
  };

  function logout() {
    fetch("http://localhost:8080/logout", {
      method: "POST",
      credentials: "include",
    })
      .then((response) => {
        if (response.ok) {
          setLoggedIn(false);
          setProfilePic(null);
          setUsername(null);
          navigate("/login");
        } else {
          console.error("Logout failed");
        }
      })
      .catch((error) => {
        console.error("Error during logout:", error);
      });
  }

  return (
    <nav className="navbar navbar-expand">
      <div className="container-fluid">
        <div className="row display-flex">
          {loggedIn && (
            <div className="collapse navbar-collapse">
              <a className="justify-content profile_div" href="/profile">
                <img
                  width="50px"
                  height="50px"
                  src={imgURL}
                  alt="User's Profile Picture"
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
                  onClick={() => logout()}
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
