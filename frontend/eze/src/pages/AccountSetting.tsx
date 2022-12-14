import React, { FormEventHandler, useEffect } from "react";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import AccountService, {
  Account,
  CreateUpdateAccountDto,
} from "../api/AccountService";
import RequestStatusMessage from "../components/UI/Other/RequestStatusMessage";
import useHttp, { RequestConfig } from "../hooks/useHttp";
import useInput, { InputType } from "../hooks/useInput";
import { IRootState } from "../store";
import {
  validateEmail,
  validateNotEmpty,
  validateUrl,
} from "../validation/validations";

const AccountSetting = () => {
  const auth = useSelector((state: IRootState) => state.auth);
  const navigate = useNavigate();
  const {
    sendRequest: updateAccount,
    data: updateAccountData,
    error: updateAccountError,
    status: updateAccountStatus,
  } = useHttp<Account>(AccountService.updateAccount, false);

  const {
    sendRequest: getOwnAccount,
    data: getOwnAccountData,
    error: getOwnAccountError,
    status: getOwnAccountStatus,
  } = useHttp<Account>(AccountService.getAccountByUsername, false);

  // useInput for the controlled input and validation
  const {
    value: fullName,
    hasError: fullNameInputHasError,
    isValid: fullNameIsValid,
    valueChangeHandler: fullNameChangeHandler,
    inputBlurHandler: fullNameBlurHandler,
    errorMessage: fullNameErrorMessage,
    set: setFullName,
  } = useInput(validateNotEmpty("Full name"), "", InputType.TEXT);
  const {
    value: password,
    hasError: passwordInputHasError,
    isValid: passwordIsValid,
    valueChangeHandler: passwordChangeHandler,
    inputBlurHandler: passwordBlurHandler,
    errorMessage: passwordErrorMessage,
  } = useInput(validateNotEmpty("Password"), "", InputType.TEXT);
  const {
    value: email,
    hasError: emailInputHasError,
    isValid: emailIsValid,
    valueChangeHandler: emailChangeHandler,
    inputBlurHandler: emailBlurHandler,
    errorMessage: emailErrorMessage,
    set: setEmail,
  } = useInput(validateEmail("Email"), "", InputType.TEXT);
  const {
    value: profile,
    hasError: profileInputHasError,
    isValid: profileIsValid,
    valueChangeHandler: profileChangeHandler,
    inputBlurHandler: profileBlurHandler,
    errorMessage: profileErrorMessage,
    set: setProfile,
  } = useInput(validateUrl("Profile image url"), "", InputType.TEXT);

  // Prepopulate the inputs based on the account info
  useEffect(() => {
    const requestConfig: RequestConfig = {
      method: "GET",
      headers: {
        Authorization: `Bearer ${auth.accessToken}`,
      },
      relativeUrl: `/api/v1/accounts/${auth.username}`,
    };

    getOwnAccount(requestConfig);
  }, []);

  // Update input data based on getOwnAccount useHttp states
  useEffect(() => {
    if (
      getOwnAccountData &&
      getOwnAccountError === null &&
      getOwnAccountStatus === "completed"
    ) {
      setFullName(getOwnAccountData.fullName || "");
      setEmail(getOwnAccountData.email || "");
      setProfile(getOwnAccountData.profile || "");
    }
  }, [getOwnAccountData, getOwnAccountError, getOwnAccountStatus]);

  const completeUpdateAccountInfo =
    auth.username &&
    fullNameIsValid &&
    passwordIsValid &&
    emailIsValid &&
    profileIsValid;

  const updateAccountHandler: FormEventHandler = (event) => {
    event.preventDefault();

    if (!completeUpdateAccountInfo) return;

    const requestBody: CreateUpdateAccountDto = {
      username: auth.username,
      password,
      email,
      fullName,
      profile,
    };

    const requestConfig: RequestConfig = {
      body: requestBody,
      headers: {
        Authorization: `Bearer ${auth.accessToken}`,
        "Content-type": "application/json",
      },
      method: "PUT",
      relativeUrl: `/api/v1/accounts/${auth.username}`,
    };

    updateAccount(requestConfig);
  };

  const backBtnHandler = () => {
    navigate("/");
  };

  return (
    <div className="container d-flex flex-column h-100">
      <div className="row d-flex justify-content-center">
        <div className="col-12 col-sm-8 col-md-6 pt-3">
          <div className="fs-5 mb-3">Update Account Info</div>
          {
            <RequestStatusMessage
              data={updateAccountData}
              error={updateAccountError}
              key={"Update account"}
              loadingMessage="Updating Account..."
              status={updateAccountStatus}
              successMessage="Account updated"
            />
          }
          <form onSubmit={updateAccountHandler}>
            <div>
              <div className="form-floating mb-3">
                <input
                  type="text"
                  className="form-control"
                  id="updateAccountUsername"
                  readOnly
                  value={auth.username}
                  disabled
                />
                <label htmlFor="updateAccountUsername">Username</label>
              </div>
            </div>
            <div className={passwordInputHasError ? "invalid" : ""}>
              {passwordInputHasError && <span>{passwordErrorMessage}</span>}
              <div className="form-floating mb-3">
                <input
                  type="password"
                  className="form-control"
                  id="updateAccountPassword"
                  onChange={passwordChangeHandler}
                  onBlur={passwordBlurHandler}
                  value={password}
                />
                <label htmlFor="updateAccountPassword">Password</label>
              </div>
            </div>
            <div className={fullNameInputHasError ? "invalid" : ""}>
              {fullNameInputHasError && <span>{fullNameErrorMessage}</span>}
              <div className="form-floating mb-3">
                <input
                  type="text"
                  className="form-control"
                  id="updateAccountFullname"
                  onChange={fullNameChangeHandler}
                  onBlur={fullNameBlurHandler}
                  value={fullName}
                />
                <label htmlFor="updateAccountFullname">Full name</label>
              </div>
            </div>
            <div className={emailInputHasError ? "invalid" : ""}>
              {emailInputHasError && <span>{emailErrorMessage}</span>}
              <div className="form-floating mb-3">
                <input
                  type="email"
                  className="form-control"
                  id="updateAccountEmail"
                  onChange={emailChangeHandler}
                  onBlur={emailBlurHandler}
                  value={email}
                />
                <label htmlFor="updateAccountEmail">Email</label>
              </div>
            </div>
            <div className={profileInputHasError ? "invalid" : ""}>
              {profileInputHasError && <span>{profileErrorMessage}</span>}
              <div className="form-floating mb-3">
                <input
                  type="text"
                  className="form-control"
                  id="updateAccountProfile"
                  onChange={profileChangeHandler}
                  onBlur={profileBlurHandler}
                  value={profile}
                />
                <label htmlFor="updateAccountProfile">Profile url</label>
              </div>
            </div>
            <div className="modal-footer">
              <button
                type="button"
                className="btn btn-secondary me-3"
                onClick={backBtnHandler}
              >
                Back
              </button>
              <button
                type="submit"
                className="btn btn-primary"
                disabled={!completeUpdateAccountInfo}
              >
                Update Account
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default AccountSetting;
