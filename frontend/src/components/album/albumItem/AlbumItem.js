import { useNavigate } from "react-router-dom";

const AlbumItem = (props) => {
  const navigate = useNavigate();
  const { item } = props;

  return (
    <div className="container mt-5 mb-5 d-flex justify-content-between align-items-center rounded">
      <img
        width="50px"
        height="50px"
        src={`${item.image}`}
        alt=""
        className="profile"
      />
      <div className="d-flex align-items-center">
        <a onClick={() => navigate(`/albums/${item.id}`)}>
          <h2 className="text-white">{item.name}</h2>
        </a>
      </div>
    </div>
  );
};

export default AlbumItem;
