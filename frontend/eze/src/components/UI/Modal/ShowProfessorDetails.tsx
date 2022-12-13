import React, { useState, useEffect, useRef } from "react";
import { Professor } from "../../../api/ProfessorService";

interface ShowProfessorDetailsProps {
  selectedProfessor?: Professor | null;
}

const ShowProfessorDetails: React.FC<ShowProfessorDetailsProps> = (props) => {
  const [name, setName] = useState("");
  const [contactNumber, setContactNumber] = useState("");

  // Add update the state based on the props
  useEffect(() => {
    if (props.selectedProfessor) {
      setName(props.selectedProfessor.name);
      setContactNumber(props.selectedProfessor.contactNumber);
    }
  }, [props.selectedProfessor]);

  return (
    <div
      className="modal fade"
      id="showProfessorDetails"
      tabIndex={-1}
      aria-labelledby="showProfessorDetailsLabel"
      aria-hidden="true"
    >
      <div className="modal-dialog modal-dialog-centered">
        <div className="modal-content">
          <div className="modal-header">
            <h1 className="modal-title fs-5" id="showProfessorDetailsLabel">
              Professor Details
            </h1>
            <button
              type="button"
              className="btn-close"
              data-bs-dismiss="modal"
              aria-label="Close"
            ></button>
          </div>
          <div className="modal-body">
            <div className="form-floating mb-3">
              <input
                type="text"
                className="form-control"
                id="showProfessorName"
                value={name}
                disabled={true}
              />
              <label htmlFor="showProfessorName">Name</label>
            </div>
            <div className="form-floating mb-3">
              <input
                type="text"
                className="form-control"
                id="showProfessorContactNumber"
                value={contactNumber}
                disabled={true}
              />
              <label htmlFor="showProfessorContactNumber">Contact Number</label>
            </div>
            <div className="modal-footer">
              <button
                type="button"
                data-bs-dismiss="modal"
                className="btn btn-secondary"
              >
                Close
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ShowProfessorDetails;
