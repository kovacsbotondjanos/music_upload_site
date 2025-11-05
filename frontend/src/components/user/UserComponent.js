import { React } from "react";
import User from "./userComponent/User";

const userComponent = (props) => {
  const { loggedIn, profilePic, username, playMusic } = props;

  return (
    <div className="black-bg">
      <User
        loggedIn={loggedIn}
        profilePic={profilePic}
        username={username}
        playMusic={playMusic}
      />
    </div>
  );
};

export default userComponent;
