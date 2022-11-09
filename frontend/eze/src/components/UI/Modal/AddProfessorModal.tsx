import React, { useState, useEffect } from "react";
import useHttp, { RequestConfig } from "../../../hooks/useHttp";
import ProfessorService, {
  CreateUpdateProfessor,
  isValidProfessor,
  Professor,
} from "../../../api/ProfessorService";
import { useSelector } from "react-redux";
import { IRootState } from "../../../store";
import { useDispatch } from "react-redux";
import { professorActions } from "../../../store/professorSlice";
import {
  validateNotEmpty,
  validatePhMobileNumber,
} from "../../../validation/validations";
import useInput, { InputType } from "../../../hooks/useInput";

const AddProfessorModal = () => {
  const auth = useSelector((state: IRootState) => state.auth);
  const dispatch = useDispatch();
  const {
    sendRequest: createProfessor,
    data,
    error,
    status,
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

  // Add the received Professor to the Redux
  useEffect(() => {
    if (status === "completed" && error === null) {
      dispatch(professorActions.addProfessor({ newProfessor: data }));
    }
  }, [data, status, error]);

  const addAccountHandler = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    console.log("Adding Professor");
    const newProfessor: CreateUpdateProfessor = {
      name,
      contactNumber,
    };

    if (!nameIsValid || !contactNumberIsValid) {
      console.log("Invalid professor");
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
  };

  return (
    <div
      className="modal fade"
      id="addProfessorModal"
      tabIndex={-1}
      aria-labelledby="addProfessorModalLabel"
      aria-hidden="true"
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
            <form onSubmit={addAccountHandler}>
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
              <div className="modal-footer">
                <button
                  type="button"
                  className="btn btn-secondary"
                  data-bs-dismiss="modal"
                >
                  Close
                </button>
                <button type="submit" className="btn btn-primary">
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
