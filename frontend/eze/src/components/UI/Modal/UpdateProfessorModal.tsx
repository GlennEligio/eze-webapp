import React, { useState, useEffect } from "react";
import useHttp, { RequestConfig } from "../../../hooks/useHttp";
import ProfessorService, {
  CreateUpdateProfessor,
  Professor,
} from "../../../api/ProfessorService";
import { useSelector } from "react-redux";
import { IRootState } from "../../../store";
import { useDispatch } from "react-redux";
import { professorActions } from "../../../store/professorSlice";

interface UpdateProfessorModalProps {
  selectedProfessor: Professor | null;
}

const UpdateProfessorModal: React.FC<UpdateProfessorModalProps> = (props) => {
  const auth = useSelector((state: IRootState) => state.auth);
  const dispatch = useDispatch();
  const [name, setName] = useState("");
  const [contactNumber, setContactNumber] = useState("");
  const {
    sendRequest: updateProfessor,
    data,
    error,
    status,
  } = useHttp<Professor>(ProfessorService.updateProfessor, false);

  // Add the received Professor to the Redux
  useEffect(() => {
    if (status == "completed" && error === null) {
      dispatch(professorActions.updateProfessor({ professor: data }));
    }
  }, [data, status, error]);

  // Update Form input based on selectedProfessor value
  useEffect(() => {
    if (props.selectedProfessor) {
      setName(props.selectedProfessor.name);
      setContactNumber(props.selectedProfessor.contactNumber);
    }
  }, [props.selectedProfessor]);

  const updateProfessorHandler = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    console.log("Updating Professor");
    const updatedProfessor: CreateUpdateProfessor = {
      contactNumber,
    };

    const requestConf: RequestConfig = {
      body: updatedProfessor,
      headers: {
        Authorization: `Bearer ${auth.accessToken}`,
        "Content-type": "application/json",
      },
      relativeUrl: `/api/v1/professors/${name}`,
    };
    updateProfessor(requestConf);
  };

  return (
    <div
      className="modal fade"
      id="updateProfessorModal"
      tabIndex={-1}
      aria-labelledby="updateProfessorModalLabel"
      aria-hidden="true"
    >
      <div className="modal-dialog modal-dialog-centered">
        <div className="modal-content">
          <div className="modal-header">
            <h1 className="modal-title fs-5" id="professorlModalLabel">
              Update Professor
            </h1>
            <button
              type="button"
              className="btn-close"
              data-bs-dismiss="modal"
              aria-label="Close"
            ></button>
          </div>
          <div className="modal-body">
            <form onSubmit={updateProfessorHandler}>
              <div className="form-floating mb-3">
                <input
                  type="text"
                  className="form-control"
                  id="updateProfessorName"
                  disabled={true}
                  value={name}
                />
                <label htmlFor="updateProfessorName">Name</label>
              </div>
              <div className="form-floating mb-3">
                <input
                  type="text"
                  className="form-control"
                  id="updateProfessorContactNumber"
                  onChange={(e) => setContactNumber(e.target.value)}
                  value={contactNumber}
                />
                <label htmlFor="updateProfessorContactNumber">
                  Contact Number
                </label>
              </div>
              <div className="modal-footer">
                <button
                  type="button"
                  className="btn btn-secondary"
                  data-bs-dismiss="modal"
                >
                  Close
                </button>
                <button type="submit" className="btn btn-primary">
                  Update
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default UpdateProfessorModal;