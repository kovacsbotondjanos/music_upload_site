import React from "react";
import Home from "./homeComponent/Home";

function HomeComponent(props) {
  const { songs, playMusic } = props;

  return (
    <div className="black-bg">
      <Home songs={songs} playMusic={playMusic} />
    </div>
  );
}

export default HomeComponent;
