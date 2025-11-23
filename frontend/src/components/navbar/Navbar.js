import { useEffect } from "react";
import { useNavigate, Link } from "react-router-dom";
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
              <Link
                className="profile_div" 
                to="/profile"
                onClick={(e) => {
                  e.preventDefault();
                  window.location.href = "/profile";
                }}
              >
                <img
                  alt=""
                  width="50px"
                  height="50px"
                  src={profilePic}
                  className="profile"
                />
              </Link>
            )}
          </div>

          <div className="d-flex align-items-center gap-4 ms-auto">
            <Link
              className="nav-link text-white"
              to="/"
              onClick={(e) => {
                e.preventDefault();
                window.location.href = "/";
              }}
            >
              Home
            </Link>

            {loggedIn ? (
              <Link
                className="nav-link text-white custom-button"
                onClick={(e) => 
                  logout(() => {
                    setLoggedIn(false);
                    setProfilePic(null);
                    setUsername(null);
                    e.preventDefault();
                    window.location.href = "/login";
                  })
                }
                to="/login"
              >
                Log out
              </Link>
            ) : (
              <>
                <Link
                  className="nav-link text-white"
                  to="/register"
                  onClick={(e) => {
                    e.preventDefault();
                    window.location.href = "/register";
                  }}
                >
                  Register
                </Link>
                <Link 
                  className="nav-link text-white"
                  to="/login"
                  onClick={(e) => {
                    e.preventDefault();
                    window.location.href = "/login";
                  }}
                >
                  Log in
                </Link>
              </>
            )}

            <Link
              className="nav-link text-white"
              onClick={(e) => {
                e.preventDefault();
                window.location.href = "/search";
              }}
              to="/search"
            >
              Search
            </Link>
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
