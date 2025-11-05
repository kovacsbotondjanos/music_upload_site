
const TagItem = (props) => {
  const { item, index, removeTag } = props;

  return (
    <div className="col">
      <div className="row">
        {index}. {item}
      </div>
      <div className="row">
        <button className="bg-grey" onClick={() => removeTag(item)}>
          <ion-icon name="trash-outline"></ion-icon>
        </button>
      </div>
    </div>
  );
};

export default TagItem;
