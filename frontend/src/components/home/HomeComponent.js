import React from "react";
import Home from "./homeComponent/Home";

const HomeComponent = (props) => {
  const { playMusic } = props;

  return (
    <div className="black-bg">
      <Home playMusic={playMusic} />
    </div>
  );
};

export default HomeComponent;
