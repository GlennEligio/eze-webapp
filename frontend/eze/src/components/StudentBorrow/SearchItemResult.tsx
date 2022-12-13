import React from "react";

interface SearchItemResultProps {
  itemName: string;
  action: "ADD" | "REMOVE";
  modalIdTarget: string;
  onClick: Function;
  addItem: Function;
  removeItem: Function;
}

const SearchItemResult: React.FC<SearchItemResultProps> = (props) => {
  return (
    <li className="list-group-item">
      <div className="d-flex justify-content-between">
        <a
          onClick={() => props.onClick()}
          href={props.modalIdTarget}
          data-bs-toggle="modal"
        >
          {props.itemName}
        </a>
        {props.action === "ADD" && props.addItem && (
          <i className="bi bi-plus-lg" onClick={() => props.addItem()}></i>
        )}
        {props.action === "REMOVE" && props.removeItem && (
          <i className="bi bi-dash-lg" onClick={() => props.removeItem()}></i>
        )}
      </div>
    </li>
  );
};

export default SearchItemResult;
