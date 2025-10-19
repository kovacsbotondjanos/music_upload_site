import { useNavigate } from "react-router-dom";

const SongItem = (props) => {
  const navigate = useNavigate();
  const { playMusic, item } = props;

  return (
    <div className="container mt-5 mb-5 d-flex justify-content-between align-items-center rounded">
      <button
        className="custom-button"
        onClick={() => playMusic(item.nameHashed)}
      >
        <img
          alt=""
          width="50px"
          height="50px"
          src={`${item.image}`}
          className="profile"
        />
      </button>
      <div className="d-flex align-items-center">
        <a onClick={() => navigate(`/songs/${item.id}`)}>
          <h2 className="text-white">{item.name}</h2>
        </a>
      </div>
    </div>
  );
};

export default SongItem;
