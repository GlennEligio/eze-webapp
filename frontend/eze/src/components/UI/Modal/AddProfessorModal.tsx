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

const AddProfessorModal = () => {
  const auth = useSelector((state: IRootState) => state.auth);
  const dispatch = useDispatch();
  const modal = useRef<HTMLDivElement | null>(null);
  const {
    sendRequest: createProfessor,
    data,
    error,
    status,
    resetHttpState,
  } = useHttp<Professor>(ProfessorService.createProfessor, false);

  // useInput for the controlled input and validation
  const {
    value: name,
    hasError: nameInputHasError,
    isValid: nameIsValid,
    valueChangeHandler: nameChangeHandler,
    inputBlurHandler: nameBlurHandler,
    reset: resetNameInput,
    errorMessage: nameErrorMessage,
  } = useInput(validateNotEmpty("Name"), "", InputType.TEXT);
  const {
    value: contactNumber,
    hasError: contactNumberInputHasError,
    isValid: contactNumberIsValid,
    valueChangeHandler: contactNumberChangeHandler,
    inputBlurHandler: contactNumberBlurHandler,
    reset: resetContactNumber,
    errorMessage: contactNumberErrorMessage,
  } = useInput(validatePhMobileNumber("Contact number"), "", InputType.TEXT);
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
    if (status === "completed" && error === null) {
      dispatch(professorActions.addProfessor({ newProfessor: data }));
    }
  }, [data, status, error]);

  // useEffect ONMOUNT for adding hidden.bs.modal eventHandler for modal to reset useHttp and useInput states
  useEffect(() => {
    if (modal.current !== null && modal.current !== undefined) {
      modal.current.addEventListener("hidden.bs.modal", () => {
        resetHttpState();
        resetContactNumber();
        resetNameInput();
        resetEmailInput();
        resetProfileInput();
      });
    }
  }, []);

  const completeProfessorInfo =
    nameIsValid && contactNumberIsValid && emailIsValid && profileIsValid;

  // form submitHandler for adding Professor
  const addProfessorHandler = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const newProfessor: CreateUpdateProfessor = {
      name,
      contactNumber,
      email,
      profile,
    };

    if (!completeProfessorInfo) {
      return;
    }

    const requestConf: RequestConfig = {
      body: newProfessor,
      headers: {
        Authorization: `Bearer ${auth.accessToken}`,
        "Content-type": "application/json",
      },
      relativeUrl: "/api/v1/professors",
    };
    createProfessor(requestConf);
    resetContactNumber();
    resetNameInput();
    resetEmailInput();
    resetProfileInput();
  };

  return (
    <div
      className="modal fade"
      id="addProfessorModal"
      tabIndex={-1}
      aria-labelledby="addProfessorModalLabel"
      aria-hidden="true"
      ref={modal}
    >
      <div className="modal-dialog modal-dialog-centered">
        <div className="modal-content">
          <div className="modal-header">
            <h1 className="modal-title fs-5" id="professorlModalLabel">
              Add Professor
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
                loadingMessage="Adding professor..."
                successMessage="Professor added"
                key="Add Professor"
              />
            }
            <form onSubmit={addProfessorHandler}>
              <div className={nameInputHasError ? "invalid" : ""}>
                {nameInputHasError && <span>{nameErrorMessage}</span>}
                <div className="form-floating mb-3">
                  <input
                    type="text"
                    className="form-control"
                    id="newProfessorName"
                    onChange={nameChangeHandler}
                    onBlur={nameBlurHandler}
                    value={name}
                  />
                  <label htmlFor="newProfessorName">Name</label>
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
                    id="newProfessorContactNumber"
                    onChange={contactNumberChangeHandler}
                    onBlur={contactNumberBlurHandler}
                    value={contactNumber}
                  />
                  <label htmlFor="newProfessorContactNumber">
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
                  Add
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AddProfessorModal;
