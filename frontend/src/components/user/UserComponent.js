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
    playMusic,
    getImageURL
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
        getImageURL={getImageURL}
      />
    </div>
  );
}

export default userComponent;
