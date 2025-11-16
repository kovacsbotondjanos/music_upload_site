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
        <div className="d-flex w-100 align-items-center justify-content-between">
          <div>
            {loggedIn && (
              <a className="profile_div" href="/profile">
                <img
                  alt=""
                  width="50px"
                  height="50px"
                  src={profilePic}
                  className="profile"
                />
              </a>
            )}
          </div>

          <div className="d-flex align-items-center gap-4 ms-auto">
            <a className="nav-link text-white" href="/">
              Home
            </a>

            {loggedIn ? (
              <a
                className="nav-link text-white custom-button"
                onClick={() =>
                  logout(() => {
                    setLoggedIn(false);
                    setProfilePic(null);
                    setUsername(null);
                    navigate("/login");
                  })
                }
              >
                Log out
              </a>
            ) : (
              <>
                <a className="nav-link text-white" href="/register">
                  Register
                </a>
                <a className="nav-link text-white" href="/login">
                  Log in
                </a>
              </>
            )}

            <a
              className="nav-link text-white"
              onClick={() => navigate("/search")}
            >
              Search
            </a>
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
