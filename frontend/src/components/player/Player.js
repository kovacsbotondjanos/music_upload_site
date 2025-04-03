import AudioPlayer from "react-h5-audio-player";
import React, { memo } from "react";

const Player = memo((props) => {
  const { audio }  = props;

  return (  
    <div id="music-player">
      <AudioPlayer
          src={audio}
          autoPlayAfterSrcChange={true}
          autoPlay={true}
          showSkipControls={true}
          showJumpControls={true}
          showDownloadProgress={true}
          showFilledVolume={true}
          
          onPlaying={() => console.log("asd")}

          onClickNext={() => console.log("next")}
          onClickPrevious={() => console.log("prev")}
          onPause={(e) => console.log("Paused")}
          onPlay={(e) => console.log("Playing")}
          onLoadedData={() => console.log("loaded")}
        />
    </div>
  );
})

export default Player;
