import React from "react";

interface SearchItemResultProps {
  itemName: string;
  action: "ADD" | "REMOVE";
  modalIdTarget: string;
  retrieveItemDetails: Function;
  addItem: Function;
  removeItem: Function;
}

const SearchItemResult: React.FC<SearchItemResultProps> = (props) => {
  return (
    <li className="list-group-item">
      <div className="d-flex justify-content-between">
        <a
          onClick={() => props.retrieveItemDetails(props.itemName)}
          data-bs-target={props.modalIdTarget}
          data-bs-toggle="modal"
        >
          {props.itemName}
        </a>
        {props.action === "ADD" && (
          <i className="bi bi-plus-lg" onClick={() => props.addItem()}></i>
        )}
        {props.action === "REMOVE" && (
          <i className="bi bi-dash-lg" onClick={() => props.removeItem()}></i>
        )}
      </div>
    </li>
  );
};

export default SearchItemResult;
