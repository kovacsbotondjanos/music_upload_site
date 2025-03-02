import React from "react";
import User from "./userComponent/User";

function userComponent(props) {
  const {
    loggedIn,
    profilePic,
    username,
    setLoggedIn,
    setUsername,
    setProfilePic,
    albums,
    userSongs,
    playMusic
  } = props;
  return (
    <div className="black-bg">
      <User
        loggedIn={loggedIn}
        profilePic={profilePic}
        username={username}
        albums={albums}
        userSongs={userSongs}
        playMusic={playMusic}
      />
    </div>
  );
}

export default userComponent;
