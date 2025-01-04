import React from "react";
import { useNavigate } from "react-router-dom";

function Register() {
  const navigate = useNavigate();

  const handleSubmit = async (event) => {
    event.preventDefault();
    const formData = new FormData(event.target);
    const formDataJson = {};
    formData.forEach((value, key) => {
      formDataJson[key] = value;
    });

    try {
      const response = await fetch("http://localhost:8080/api/v1/users/add", {
        method: "POST",
        headers: {
          "Content-Type": "application/x-www-form-urlencoded",
        },
        body: new URLSearchParams(formDataJson),
        credentials: "include",
      });
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      navigate("/login");
    } catch (error) {
      console.error("Error:", error);
    }
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
                    <h2>Sign up</h2>
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
                          <label className="button-label">Email</label>
                          <br />
                          <span className="icon">
                            <ion-icon name="mail-outline"></ion-icon>
                          </span>
                          <input type="text" id="email" name="email" required />
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
                            className="button login-button"
                            id="submit-btn"
                            type="submit"
                          >
                            Sign up
                          </button>
                        </div>
                      </div>
                    </div>
                    <div className="row">
                      <div className="col login-register">
                        <p>
                          Already have an account?
                          <br />
                          <a href="/login" className="register-link">
                            Log in
                          </a>
                        </p>
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

export default Register;
