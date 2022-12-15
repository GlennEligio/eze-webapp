import React, { useState, useEffect, useRef } from "react";
import useHttp, { RequestConfig } from "../../../hooks/useHttp";
import AccountService, {
  Account,
  AccountType,
  CreateUpdateAccountDto,
} from "../../../api/AccountService";
import { useSelector } from "react-redux";
import { IRootState } from "../../../store";
import { useDispatch } from "react-redux";
import { accountActions } from "../../../store/accountSlice";
import {
  validateContains,
  validateNotEmpty,
  validateUrl,
} from "../../../validation/validations";
import useInput, { InputType } from "../../../hooks/useInput";
import RequestStatusMessage from "../Other/RequestStatusMessage";

const AddAccountModal = () => {
  const auth = useSelector((state: IRootState) => state.auth);
  const dispatch = useDispatch();
  const [email, setEmail] = useState("");
  const [active, setActive] = useState(true);
  const modal = useRef<HTMLDivElement | null>(null);
  const {
    sendRequest: createAccount,
    data,
    error,
    status: requestStatus,
    resetHttpState,
  } = useHttp<Account>(AccountService.createAccount, false);

  // useInput for the controlled input and validation
  const {
    value: fullName,
    hasError: fullNameInputHasError,
    isValid: fullNameIsValid,
    valueChangeHandler: fullNameChangeHandler,
    inputBlurHandler: fullNameBlurHandler,
    reset: resetFullNameInput,
    errorMessage: fullNameErrorMessage,
  } = useInput(validateNotEmpty("Full name"), "", InputType.TEXT);
  const {
    value: username,
    hasError: usernameInputHasError,
    isValid: usernameIsValid,
    valueChangeHandler: usernameChangeHandler,
    inputBlurHandler: usernameBlurHandler,
    reset: resetUsernameInput,
    errorMessage: usernameErrorMessage,
  } = useInput(validateNotEmpty("Username"), "", InputType.TEXT);
  const {
    value: password,
    hasError: passwordInputHasError,
    isValid: passwordIsValid,
    valueChangeHandler: passwordChangeHandler,
    inputBlurHandler: passwordBlurHandler,
    reset: resetPasswordInput,
    errorMessage: passwordErrorMessage,
  } = useInput(validateNotEmpty("Password"), "", InputType.TEXT);
  const {
    value: type,
    hasError: typeInputHasError,
    isValid: typeIsValid,
    valueChangeHandler: typeChangeHandler,
    inputBlurHandler: typeBlurHandler,
    reset: resetTypeInput,
    errorMessage: typeErrorMessage,
  } = useInput(
    validateContains("Type", [
      AccountType.ADMIN,
      AccountType.STUDENT_ASSISTANT,
      AccountType.PROF,
      AccountType.STUDENT,
    ]),
    AccountType.STUDENT_ASSISTANT,
    InputType.SELECT
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

  // Add Account in Redux after successful request
  useEffect(() => {
    if (requestStatus === "completed" && data && error === null) {
      dispatch(accountActions.addAccount({ newAccount: data }));
    }
  }, [data, requestStatus, error]);

  const addAccountHandler = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    console.log("Adding Account");
    const newAccount: CreateUpdateAccountDto = {
      fullName,
      active,
      email,
      password,
      type,
      username,
      profile,
    };

    if (
      !usernameIsValid ||
      !passwordIsValid ||
      !typeIsValid ||
      !fullNameIsValid ||
      !profileIsValid
    ) {
      console.log("Invalid account");
      return;
    }

    const requestConf: RequestConfig = {
      body: newAccount,
      headers: {
        Authorization: `Bearer ${auth.accessToken}`,
        "Content-type": "application/json",
      },
    };
    createAccount(requestConf);
    resetFullNameInput();
    resetPasswordInput();
    resetProfileInput();
    resetTypeInput();
    resetUsernameInput();
    setActive(true);
    setEmail("");
  };

  // set up modal so that when hidden.bs.modal event is triggered, it will reset the useHttp and useInput states
  useEffect(() => {
    if (modal.current !== null && modal.current !== undefined) {
      modal.current.addEventListener("hidden.bs.modal", () => {
        resetHttpState();
        resetPasswordInput();
        resetTypeInput();
        resetUsernameInput();
        resetFullNameInput();
        setActive(true);
        setEmail("");
        resetProfileInput();
      });
    }
  }, [modal.current]);

  return (
    <div
      className="modal fade"
      id="addAccountModal"
      tabIndex={-1}
      aria-labelledby="addAccountModalLabel"
      aria-hidden="true"
      ref={modal}
    >
      <div className="modal-dialog modal-dialog-centered modal-dialog-scrollable">
        <div className="modal-content">
          <div className="modal-header">
            <h1 className="modal-title fs-5" id="accounttModalLabel">
              Add Account
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
                key={"Add account"}
                loadingMessage="Adding Account..."
                status={requestStatus}
                successMessage="Account added"
              />
            }
            <form onSubmit={addAccountHandler}>
              <div className={usernameInputHasError ? "invalid" : ""}>
                {usernameInputHasError && <span>{usernameErrorMessage}</span>}
                <div className={`form-floating mb-3`}>
                  <input
                    type="text"
                    className="form-control"
                    id="newAccountUsername"
                    onChange={usernameChangeHandler}
                    onBlur={usernameBlurHandler}
                    value={username}
                  />
                  <label htmlFor="newAccountUsername">Username</label>
                </div>
              </div>
              <div className={passwordInputHasError ? "invalid" : ""}>
                {passwordInputHasError && <span>{passwordErrorMessage}</span>}
                <div className="form-floating mb-3">
                  <input
                    type="password"
                    className="form-control"
                    id="newAccountPassword"
                    onChange={passwordChangeHandler}
                    onBlur={passwordBlurHandler}
                    value={password}
                  />
                  <label htmlFor="newAccountPassword">Password</label>
                </div>
              </div>
              <div className={typeInputHasError ? "invalid" : ""}>
                {typeInputHasError && <span>{typeErrorMessage}</span>}
              </div>
              <div className="form-floating mb-3">
                <select
                  className="form-select"
                  id="newAccountType"
                  onChange={typeChangeHandler}
                  onBlur={typeBlurHandler}
                  value={type}
                >
                  <option value={AccountType.ADMIN}>ADMIN</option>
                  <option value={AccountType.STUDENT_ASSISTANT}>
                    STUDENT ASSISTANT
                  </option>
                  <option value={AccountType.PROF}>PROF</option>
                  <option value={AccountType.STUDENT}>STUDENT</option>
                </select>
                <label htmlFor="newAccountType">Type</label>
              </div>
              <div className="form-floating mb-3">
                <select
                  className="form-select"
                  id="newAccountActive"
                  onChange={(e) => setActive(e.currentTarget.value === "YES")}
                  value={active ? "YES" : "NO"}
                >
                  <option value="YES">YES</option>
                  <option value="NO">NO</option>
                </select>
                <label htmlFor="newAccountActive">Active?</label>
              </div>
              <div className={fullNameInputHasError ? "invalid" : ""}>
                {fullNameInputHasError && <span>{fullNameErrorMessage}</span>}
                <div className="form-floating mb-3">
                  <input
                    type="text"
                    className="form-control"
                    id="newAccountFullname"
                    onChange={fullNameChangeHandler}
                    onBlur={fullNameBlurHandler}
                    value={fullName}
                  />
                  <label htmlFor="newAccountFullname">Full name</label>
                </div>
              </div>
              <div className="form-floating mb-3">
                <input
                  type="email"
                  className="form-control"
                  id="newAccountEmail"
                  onChange={(e) => setEmail(e.target.value)}
                  value={email}
                />
                <label htmlFor="newAccountFullname">Email</label>
              </div>
              <div className={profileInputHasError ? "invalid" : ""}>
                {profileInputHasError && <span>{profileErrorMessage}</span>}
                <div className="form-floating mb-3">
                  <input
                    type="text"
                    className="form-control"
                    id="newAccountProfile"
                    onChange={profileChangeHandler}
                    onBlur={profileBlurHandler}
                    value={profile}
                  />
                  <label htmlFor="newAccountProfile">Profile url</label>
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
                  Add Account
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AddAccountModal;
