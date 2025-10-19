import React, { memo } from "react";

const Player = memo(() => {
  return (
    <div id="music-player">
      <audio id="audio-player" controls crossOrigin="anonymous">
        <source id="audio-source" src="" type="audio/mp3" />
        Your browser does not support the audio element.
      </audio>
    </div>
  );
})

export default Player;
