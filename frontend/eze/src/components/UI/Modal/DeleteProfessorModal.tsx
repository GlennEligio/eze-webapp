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

interface DeleteProfessorModalProps {
  selectedProfessor: Professor | null;
}

const DeleteProfessorModal: React.FC<DeleteProfessorModalProps> = (props) => {
  const auth = useSelector((state: IRootState) => state.auth);
  const [name, setName] = useState("");
  const [contactNumber, setContactNumber] = useState("");
  const dispatch = useDispatch();
  const {
    sendRequest: deleteProfessor,
    data,
    error,
    status,
  } = useHttp<boolean>(ProfessorService.deleteProfessor, false);

  // Delete the Professor to the Redux store
  useEffect(() => {
    if (status == "completed" && error === null) {
      dispatch(
        professorActions.removeProfessor({
          name: name,
        })
      );
    }
  }, [data, status, error]);

  // Update States based on selectedProfessor
  useEffect(() => {
    if (props.selectedProfessor) {
      setName(props.selectedProfessor.name);
      setContactNumber(props.selectedProfessor.contactNumber);
    }
  }, [props.selectedProfessor]);

  const deleteProfessorHandler = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    console.log("Deleting Professor");
    const requestConf: RequestConfig = {
      headers: {
        Authorization: `Bearer ${auth.accessToken}`,
      },
      relativeUrl: `/api/v1/professors/${name}`,
    };
    deleteProfessor(requestConf);
  };

  return (
    <div
      className="modal fade"
      id="deleteProfessorModal"
      tabIndex={-1}
      aria-labelledby="deleteProfessorModalLabel"
      aria-hidden="true"
    >
      <div className="modal-dialog modal-dialog-centered">
        <div className="modal-content">
          <div className="modal-header">
            <h1 className="modal-title fs-5" id="professorlModalLabel">
              Delete Professor
            </h1>
            <button
              type="button"
              className="btn-close"
              data-bs-dismiss="modal"
              aria-label="Close"
            ></button>
          </div>
          <div className="modal-body">
            <form onSubmit={deleteProfessorHandler}>
              <div className="form-floating mb-3">
                <input
                  type="text"
                  className="form-control"
                  id="deleteProfessorName"
                  value={name}
                  disabled={true}
                />
                <label htmlFor="deleteProfessorName">Name</label>
              </div>
              <div className="form-floating mb-3">
                <input
                  type="text"
                  className="form-control"
                  id="deleteProfessorContactNumber"
                  value={contactNumber}
                  disabled={true}
                />
                <label htmlFor="deleteProfessorContactNumber">
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
                  Delete
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default DeleteProfessorModal;
