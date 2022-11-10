import React, { useState, useEffect, useRef } from "react";
import useHttp, { RequestConfig } from "../../../hooks/useHttp";
import AccountService, {
  Account,
  AccountType,
  CreateUpdateAccountDto,
  isValidAccount,
} from "../../../api/AccountService";
import { useSelector } from "react-redux";
import { IRootState } from "../../../store";
import { useDispatch } from "react-redux";
import { accountActions } from "../../../store/accountSlice";
// import { isValidStudent } from "../../../api/StudentService";
import useInput from "../../../hooks/useInput";
import { InputType } from "../../../hooks/useInput";
import { validateContains } from "../../../validation/validations";
import { validateNotEmpty } from "../../../validation/validations";
import RequestStatusMessage from "../Other/RequestStatusMessage";

const UpdateAccountModal = () => {
  const auth = useSelector((state: IRootState) => state.auth);
  const account = useSelector((state: IRootState) => state.account);
  const dispatch = useDispatch();
  const [email, setEmail] = useState("");
  const [active, setActive] = useState(true);
  const modal = useRef<HTMLDivElement | null>(null);
  const {
    sendRequest: updateAccount,
    data,
    error,
    status: requestStatus,
    resetHttpState,
  } = useHttp<Account>(AccountService.updateAccount, false);

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
    value: username,
    hasError: usernameInputHasError,
    isValid: usernameIsValid,
    errorMessage: usernameErrorMessage,
    set: setUsername,
  } = useInput(validateNotEmpty("Username"), "", InputType.TEXT);
  const {
    value: password,
    hasError: passwordInputHasError,
    isValid: passwordIsValid,
    valueChangeHandler: passwordChangeHandler,
    inputBlurHandler: passwordBlurHandler,
    errorMessage: passwordErrorMessage,
    reset: resetPasswordInput,
  } = useInput(validateNotEmpty("Password"), "", InputType.TEXT);
  const {
    value: type,
    hasError: typeInputHasError,
    isValid: typeIsValid,
    valueChangeHandler: typeChangeHandler,
    inputBlurHandler: typeBlurHandler,
    set: setType,
    errorMessage: typeErrorMessage,
  } = useInput(
    validateContains("Type", [
      AccountType.ADMIN,
      AccountType.STUDENT_ASSISTANT,
    ]),
    AccountType.STUDENT_ASSISTANT,
    InputType.SELECT
  );

  // Add account in the Context
  useEffect(() => {
    if (requestStatus == "completed") {
      if (error == null) {
        dispatch(accountActions.updateAccount({ account: data }));
      }
    }
  }, [data]);

  // Populate inputs with the selected Account in Context
  useEffect(() => {
    const selectedAcn = account.selectedAccount;
    if (selectedAcn === null) return;
    setUsername(selectedAcn.username);
    setFullName(selectedAcn.fullName);
    setEmail(selectedAcn.email);
    setType(selectedAcn.type);
    setActive(selectedAcn.active);
  }, [account.selectedAccount]);

  // set up modal so that when hidden.bs.modal event is triggered, it will
  // 1. reset the useHttp and 2. reset the inputs
  useEffect(() => {
    if (modal.current !== null && modal.current !== undefined) {
      modal.current.addEventListener("hidden.bs.modal", () => {
        resetHttpState();
        resetPasswordInput();
        if (account.selectedAccount !== null) {
          setType(account.selectedAccount.type as AccountType);
          setUsername(account.selectedAccount.username);
          setFullName(account.selectedAccount.fullName);
        }
      });
    }
  }, [modal.current]);

  const updateAccountHandler = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    console.log("Updating Account");
    const updatedAccount: CreateUpdateAccountDto = {
      fullName,
      active,
      email,
      password,
      type,
      username,
    };

    if (
      !fullNameIsValid ||
      !passwordIsValid ||
      !typeIsValid ||
      !usernameIsValid
    ) {
      console.log("Invalid Account");
      return;
    }

    const requestConf: RequestConfig = {
      body: updatedAccount,
      headers: {
        Authorization: `Bearer ${auth.accessToken}`,
        "Content-type": "application/json",
      },
      relativeUrl: `/api/v1/accounts/${account.selectedAccount?.username}`,
    };
    updateAccount(requestConf);
  };

  return (
    <div
      className="modal fade"
      id="updateAccountModal"
      tabIndex={-1}
      aria-labelledby="updateAccountModalLabel"
      aria-hidden="true"
      ref={modal}
    >
      <div className="modal-dialog modal-dialog-centered">
        <div className="modal-content">
          <div className="modal-header">
            <h1 className="modal-title fs-5">Update Account</h1>
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
                key={"Update account"}
                loadingMessage="Updating Account..."
                status={requestStatus}
                successMessage="Account updated"
              />
            }
            <form onSubmit={updateAccountHandler}>
              <div className={usernameInputHasError ? "invalid" : ""}>
                {usernameInputHasError && <span>{usernameErrorMessage}</span>}
                <div className="form-floating mb-3">
                  <input
                    type="text"
                    className="form-control"
                    id="updateAccountUsername"
                    readOnly
                    value={username}
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
              <div className={typeInputHasError ? "invalid" : ""}>
                {typeInputHasError && <span>{typeErrorMessage}</span>}
                <div className="form-floating mb-3">
                  <select
                    className="form-select"
                    id="updateAccountType"
                    onChange={typeChangeHandler}
                    onBlur={typeBlurHandler}
                    value={type}
                  >
                    <option value={AccountType.ADMIN}>ADMIN</option>
                    <option value={AccountType.STUDENT_ASSISTANT}>
                      STUDENT ASSISTANT
                    </option>
                  </select>
                  <label htmlFor="updateAccountType">Type</label>
                </div>
              </div>
              <div className="form-floating mb-3">
                <select
                  className="form-select"
                  id="updateAccountActive"
                  onChange={(e) => setActive(e.currentTarget.value === "YES")}
                  value={active ? "YES" : "NO"}
                >
                  <option value="YES">YES</option>
                  <option value="NO">NO</option>
                </select>
                <label htmlFor="updateAccountActive">Active?</label>
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
              <div className="form-floating mb-3">
                <input
                  type="text"
                  className="form-control"
                  id="updateAccountEmail"
                  onChange={(e) => setEmail(e.target.value)}
                  value={email}
                />
                <label htmlFor="updateAccountFullname">Email</label>
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
                  Update Account
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default UpdateAccountModal;
