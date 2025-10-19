import React from "react";
import { useNavigate } from "react-router-dom";
import { login, getCurrentUser } from "../../services/controller";

const Login = (props) => {
  const navigate = useNavigate();

  const { setLoggedIn, setUsername, setProfilePic } = props;

  const handleSubmit = async (event) => {
    event.preventDefault();
    const formData = new FormData(event.target);
    const formDataJson = {};
    formData.forEach((value, key) => {
      formDataJson[key] = value;
    });

    login(formDataJson['username'], formDataJson['password'], () => {
      getCurrentUser(data => {
          if (data) {
            setLoggedIn(true);
            setUsername(data.username);
            setProfilePic(data.profilePicture);
            navigate("/");
          }
        })
    })
  };

  return (
    <div className="black-bg">
      <div className="container container-fluid">
        <div className="row">
          <div className="wrapper">
            <div className="form-box login">
              <div className="container">
                <div className="row">
                  <div className="col">
                    <h2>Login</h2>
                  </div>
                </div>
              </div>
              <div className="container">
                <div className="row">
                  <form onSubmit={handleSubmit}>
                    <div className="row">
                      <div className="col">
                        <div className="input-box">
                          <label className="button-label">Username</label>
                          <br />
                          <span className="icon">
                            <ion-icon name="pricetag-outline"></ion-icon>
                          </span>
                          <input
                            type="text"
                            id="username"
                            name="username"
                            required
                          />
                        </div>
                      </div>
                    </div>

                    <div className="row">
                      <div className="col">
                        <div className="input-box">
                          <label className="button-label">Password</label>
                          <br />
                          <span className="icon">
                            <ion-icon name="lock-closed-outline"></ion-icon>
                          </span>
                          <input
                            type="password"
                            id="password"
                            name="password"
                            required
                          />
                        </div>
                      </div>
                    </div>

                    <div className="row">
                      <div className="col">
                        <div className="login-button">
                          <button
                            type="submit"
                            id="submit-btn"
                            className="button login-button"
                          >
                            Login
                          </button>
                        </div>
                        <div className="login-register">
                          <p>
                            Don't have an account?
                            <br />
                            <a href="/register" className="register-link">
                              Register
                            </a>
                          </p>
                        </div>
                      </div>
                    </div>
                  </form>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Login;
