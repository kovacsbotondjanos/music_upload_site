import { React } from "react";
import User from "./userComponent/User";

const userComponent = (props) => {
  const {
    playMusic
  } = props;

  return (
    <div className="black-bg">
      <User
        playMusic={playMusic}
      />
    </div>
  );
}

export default userComponent;
