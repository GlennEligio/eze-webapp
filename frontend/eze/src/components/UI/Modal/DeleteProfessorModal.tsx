import React, { useState, useEffect, useRef } from "react";
import useHttp, { RequestConfig } from "../../../hooks/useHttp";
import ProfessorService, { Professor } from "../../../api/ProfessorService";
import { useSelector } from "react-redux";
import { IRootState } from "../../../store";
import { useDispatch } from "react-redux";
import validator from "validator";
import { professorActions } from "../../../store/professorSlice";
import RequestStatusMessage from "../Other/RequestStatusMessage";

interface DeleteProfessorModalProps {
  selectedProfessor: Professor | null;
}

const DeleteProfessorModal: React.FC<DeleteProfessorModalProps> = (props) => {
  const auth = useSelector((state: IRootState) => state.auth);
  const [name, setName] = useState("");
  const [contactNumber, setContactNumber] = useState("");
  const [email, setEmail] = useState("");
  const [profile, setProfile] = useState("");
  const modal = useRef<HTMLDivElement | null>(null);
  const dispatch = useDispatch();
  const {
    sendRequest: deleteProfessor,
    data,
    error,
    status,
    resetHttpState,
  } = useHttp<boolean>(ProfessorService.deleteProfessor, false);

  // Delete the Professor to the Redux store
  useEffect(() => {
    if (status == "completed" && error === null && data) {
      dispatch(
        professorActions.removeProfessor({
          name: name,
        })
      );
      dispatch(
        professorActions.updateSelectedProfessor({ selectedProfessor: null })
      );
    }
  }, [data, status, error]);

  // Update States based on selectedProfessor
  useEffect(() => {
    if (props.selectedProfessor) {
      setName(props.selectedProfessor.name);
      setContactNumber(props.selectedProfessor.contactNumber);
      setProfile(props.selectedProfessor.profile);
      setEmail(props.selectedProfessor.email);
    }
  }, [props.selectedProfessor]);

  // Add hidden.bs.modal eventHandler to Modal at Component Mount
  useEffect(() => {
    if (modal.current) {
      modal.current.addEventListener("hidden.bs.modal", () => {
        resetHttpState();
        if (props.selectedProfessor) {
          setName(props.selectedProfessor.name);
          setContactNumber(props.selectedProfessor.contactNumber);
        }
      });
    }
  }, []);

  // form submitHandler for deleting Professor
  const deleteProfessorHandler = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    console.log("Deleting Professor");

    if (validator.isEmpty(name)) return;

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
      ref={modal}
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
            {
              <RequestStatusMessage
                data={data}
                error={error}
                status={status}
                loadingMessage="Deleting professor..."
                successMessage="Professor deleted"
                key="Delete Professor"
              />
            }
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
              <div className="form-floating mb-3">
                <input
                  type="text"
                  className="form-control"
                  id="deleteProfessorEmail"
                  value={email}
                  disabled={true}
                />
                <label htmlFor="deleteProfessorEmail">Email</label>
              </div>
              <div className="form-floating mb-3">
                <input
                  type="text"
                  className="form-control"
                  id="deleteProfessorProfile"
                  value={profile}
                  disabled={true}
                />
                <label htmlFor="deleteProfessorProfile">
                  Profile image url
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
