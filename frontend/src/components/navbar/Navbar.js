import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { getToken, getUserData, logout } from "../../services/controller";

const Navbar = (props) => {
  const navigate = useNavigate();
  const {
    loggedIn,
    profilePic,
    setLoggedIn,
    setUsername,
    setProfilePic,
    setCurrentUserId,
  } = props;

  useEffect(() => {
    const token = getToken();
    if (!token) {
      setLoggedIn(false);
      setUsername(null);
      setProfilePic(null);
      setCurrentUserId(0);
      return;
    }

    if (!loggedIn || !profilePic) {
      (async () => {
        await getUserData((data) => {
          if (data) {
            setLoggedIn(true);
            setUsername(data.username);
            setProfilePic(data.profilePicture);
            setCurrentUserId(data.id);
          } else {
            localStorage.removeItem("jwtToken");
            setLoggedIn(false);
            setUsername(null);
            setProfilePic(null);
            setCurrentUserId(0);
          }
        });
      })();
    }
  }, [loggedIn, profilePic, setLoggedIn, setUsername, setProfilePic]);

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
                  src={`${profilePic}`}
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
          <button onClick={() => navigate("/search")}>Search</button>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
