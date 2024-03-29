import React, { useState, useEffect, useRef } from "react";
import useHttp, { RequestConfig } from "../../../hooks/useHttp";
import { useSelector } from "react-redux";
import { IRootState } from "../../../store";
import { useDispatch } from "react-redux";
import { accountActions } from "../../../store/accountSlice";
import AccountService, { AccountType } from "../../../api/AccountService";
import validator from "validator";
import RequestStatusMessage from "../Other/RequestStatusMessage";

const DeleteAccountModal = () => {
  const auth = useSelector((state: IRootState) => state.auth);
  const account = useSelector((state: IRootState) => state.account);
  const modal = useRef<HTMLDivElement | null>(null);
  const dispatch = useDispatch();
  const [username, setUsername] = useState("");
  const [fullName, setFullName] = useState("");
  const [email, setEmail] = useState("");
  const [profile, setProfile] = useState("");
  const [type, setType] = useState(AccountType.STUDENT_ASSISTANT);
  const [active, setActive] = useState(true);
  const {
    sendRequest: deleteAccount,
    error,
    data,
    status: requestStatus,
    resetHttpState,
  } = useHttp<boolean>(AccountService.deleteAccount, false);

  // remove Account in context after successful remova
  useEffect(() => {
    if (requestStatus == "completed" && error == null && data) {
      dispatch(
        accountActions.removeAccount({
          username: account.selectedAccount?.username,
        })
      );
      dispatch(accountActions.updateSelectedAccount({ selectedAccount: null }));
    }
  }, [requestStatus, error, data]);

  // Populate inputs with the selected Account in Context
  useEffect(() => {
    const selectedAcn = account.selectedAccount;
    if (selectedAcn === null) return;
    setUsername(!!selectedAcn.username ? selectedAcn.username : "");
    setFullName(!!selectedAcn.fullName ? selectedAcn.fullName : "");
    setEmail(!!selectedAcn.email ? selectedAcn.email : "");
    setType(selectedAcn.type);
    setActive(!!selectedAcn.active ? selectedAcn.active : true);
    setProfile(!!selectedAcn.profile ? selectedAcn.profile : "");
  }, [account.selectedAccount]);

  // Add eventHandler to hidden.bs.modal event of Modal to reset useHttp state
  // set up modal so that when hidden.bs.modal event is triggered, it will reset the useHttp and useInput states
  useEffect(() => {
    if (modal.current !== null && modal.current !== undefined) {
      modal.current.addEventListener("hidden.bs.modal", () => {
        resetHttpState();
      });
    }
  }, [modal.current]);

  const deleteAccountHandler = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    if (validator.isEmpty(account.selectedAccount?.username + "")) return;

    const requestConf: RequestConfig = {
      headers: {
        Authorization: `Bearer ${auth.accessToken}`,
        "Content-type": "application/json",
      },
      relativeUrl: `/api/v1/accounts/${account.selectedAccount?.username}`,
    };
    deleteAccount(requestConf);
  };

  return (
    <div
      className="modal fade"
      id="deleteAccountModal"
      tabIndex={-1}
      aria-hidden="true"
      ref={modal}
    >
      <div className="modal-dialog modal-dialog-centered">
        <div className="modal-content">
          <div className="modal-header">
            <h1 className="modal-title fs-5">Delete Account</h1>
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
                key={"Delete account"}
                loadingMessage="Deleting Account..."
                status={requestStatus}
                successMessage="Account deleting"
              />
            }
            <form onSubmit={deleteAccountHandler}>
              <div className="form-floating mb-3">
                <input
                  type="text"
                  className="form-control"
                  id="deleteAccountUsername"
                  readOnly={true}
                  value={username}
                />
                <label htmlFor="deleteAccountUsername">Username</label>
              </div>
              <div className="form-floating mb-3">
                <select
                  className="form-select"
                  id="deleteAccountType"
                  disabled={true}
                  value={type}
                >
                  <option value={AccountType.ADMIN}>ADMIN</option>
                  <option value={AccountType.STUDENT_ASSISTANT}>
                    STUDENT ASSISTANT
                  </option>
                </select>
                <label htmlFor="deleteAccountType">Type</label>
              </div>
              <div className="form-floating mb-3">
                <select
                  className="form-select"
                  id="deleteAccountActive"
                  disabled={true}
                  value={active ? "YES" : "NO"}
                >
                  <option value="YES">YES</option>
                  <option value="NO">NO</option>
                </select>
                <label htmlFor="deleteAccountActive">Active?</label>
              </div>
              <div className="form-floating mb-3">
                <input
                  type="text"
                  className="form-control"
                  id="deleteAccountFullname"
                  disabled={true}
                  value={fullName}
                />
                <label htmlFor="deleteAccountFullname">Full name</label>
              </div>
              <div className="form-floating mb-3">
                <input
                  type="email"
                  className="form-control"
                  id="deleteAccountEmail"
                  disabled={true}
                  value={email}
                />
                <label htmlFor="deleteAccountFullname">Email</label>
              </div>
              <div className="form-floating mb-3">
                <input
                  type="text"
                  className="form-control"
                  id="deleteAccountProfile"
                  disabled={true}
                  value={profile}
                />
                <label htmlFor="deleteAccountPassword">Profile image url</label>
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
                  Delete Account
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default DeleteAccountModal;
