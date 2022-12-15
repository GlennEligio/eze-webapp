import React, { useEffect, useRef } from "react";
import useHttp, { RequestConfig } from "../../../hooks/useHttp";
import ProfessorService, {
  CreateUpdateProfessor,
  Professor,
} from "../../../api/ProfessorService";
import { useSelector } from "react-redux";
import { IRootState } from "../../../store";
import { useDispatch } from "react-redux";
import { professorActions } from "../../../store/professorSlice";
import {
  validateEmail,
  validateNotEmpty,
  validatePhMobileNumber,
  validateUrl,
} from "../../../validation/validations";
import useInput, { InputType } from "../../../hooks/useInput";
import RequestStatusMessage from "../Other/RequestStatusMessage";

interface UpdateProfessorModalProps {
  selectedProfessor: Professor | null;
}

const UpdateProfessorModal: React.FC<UpdateProfessorModalProps> = (props) => {
  const auth = useSelector((state: IRootState) => state.auth);
  const dispatch = useDispatch();
  const modal = useRef<HTMLDivElement | null>(null);
  const {
    sendRequest: updateProfessor,
    data,
    error,
    status,
    resetHttpState,
  } = useHttp<Professor>(ProfessorService.updateProfessor, false);

  // useInput for the controlled input and validation
  const {
    value: name,
    hasError: nameInputHasError,
    isValid: nameIsValid,
    errorMessage: nameErrorMessage,
    set: setName,
  } = useInput<string, HTMLInputElement>(
    validateNotEmpty("Name"),
    "",
    InputType.TEXT
  );
  const {
    value: contactNumber,
    hasError: contactNumberInputHasError,
    isValid: contactNumberIsValid,
    valueChangeHandler: contactNumberChangeHandler,
    inputBlurHandler: contactNumberBlurHandler,
    errorMessage: contactNumberErrorMessage,
    set: setContactNumber,
  } = useInput<string, HTMLInputElement>(
    validatePhMobileNumber("Contact number"),
    "",
    InputType.TEXT
  );
  const {
    value: profile,
    hasError: profileInputHasError,
    isValid: profileIsValid,
    valueChangeHandler: profileChangeHandler,
    inputBlurHandler: profileBlurHandler,
    reset: resetProfileInput,
    errorMessage: profileErrorMessage,
  } = useInput(validateUrl("Profile image url"), "", InputType.TEXT);
  const {
    value: email,
    hasError: emailInputHasError,
    isValid: emailIsValid,
    valueChangeHandler: emailChangeHandler,
    inputBlurHandler: emailBlurHandler,
    reset: resetEmailInput,
    errorMessage: emailErrorMessage,
  } = useInput(validateEmail("Email"), "", InputType.TEXT);

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

  // Add hidden.bs.modal eventHandler to Modal at component mount
  useEffect(() => {
    if (modal.current !== null && modal.current !== undefined) {
      modal.current.addEventListener("hidden.bs.modal", () => {
        resetHttpState();
      });
      if (props.selectedProfessor) {
        setName(props.selectedProfessor.name);
        setContactNumber(props.selectedProfessor.contactNumber);
      }
    }
  }, []);

  const completeProfessorInfo =
    nameIsValid && contactNumberIsValid && emailIsValid && profileIsValid;

  const updateProfessorHandler = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    console.log("Updating Professor");
    const updatedProfessor: CreateUpdateProfessor = {
      name,
      contactNumber,
      email,
      profile,
    };

    if (!completeProfessorInfo) {
      console.log("Invalid professor");
      return;
    }

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
            {
              <RequestStatusMessage
                data={data}
                error={error}
                status={status}
                loadingMessage="Updating professor..."
                successMessage="Professor updated"
                key="Update Professor"
              />
            }
            <form onSubmit={updateProfessorHandler}>
              <div className={nameInputHasError ? "invalid" : ""}>
                {nameInputHasError && <span>{nameErrorMessage}</span>}
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
              </div>
              <div className={contactNumberInputHasError ? "invalid" : ""}>
                {contactNumberInputHasError && (
                  <span>{contactNumberErrorMessage}</span>
                )}
                <div className="form-floating mb-3">
                  <input
                    type="text"
                    className="form-control"
                    id="updateProfessorContactNumber"
                    onChange={contactNumberChangeHandler}
                    onBlur={contactNumberBlurHandler}
                    value={contactNumber}
                  />
                  <label htmlFor="updateProfessorContactNumber">
                    Contact Number
                  </label>
                </div>
              </div>
              <div className={emailInputHasError ? "invalid" : ""}>
                {emailInputHasError && <span>{emailErrorMessage}</span>}
                <div className="form-floating mb-3">
                  <input
                    type="text"
                    className="form-control"
                    id="newProfessorEmail"
                    onChange={emailChangeHandler}
                    onBlur={emailBlurHandler}
                    value={email}
                  />
                  <label htmlFor="newProfessorEmail">Email</label>
                </div>
              </div>
              <div className={profileInputHasError ? "invalid" : ""}>
                {profileInputHasError && <span>{profileErrorMessage}</span>}
                <div className="form-floating mb-3">
                  <input
                    type="text"
                    className="form-control"
                    id="newProfessorProfile"
                    onChange={profileChangeHandler}
                    onBlur={profileBlurHandler}
                    value={profile}
                  />
                  <label htmlFor="newProfessorProfile">Profile image url</label>
                </div>
              </div>
              <div className="modal-footer">
                <button
                  type="button"
                  className="btn btn-secondary"
                  data-bs-dismiss="modal"
                >
                  Close
                </button>
                <button
                  type="submit"
                  className="btn btn-primary"
                  disabled={!completeProfessorInfo}
                >
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
