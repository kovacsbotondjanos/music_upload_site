import React from "react";
import Home from "./homeComponent/Home";

function HomeComponent(props) {
  const { songs, playMusic, getImageURL } = props;

  return (
    <div className="black-bg">
      <Home songs={songs} playMusic={playMusic} getImageURL={getImageURL}/>
    </div>
  );
}

export default HomeComponent;
