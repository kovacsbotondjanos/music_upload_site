import React from "react";

const TagItem = (props) => {
  const { item, index, removeTag } = props;

  return (
        <div>
          {index}. {item} 
          <button className="bg-grey" onClick={() => removeTag(item)}>
            <ion-icon name="trash-outline">
            </ion-icon>
          </button>
        </div>
  );
};

export default TagItem;
