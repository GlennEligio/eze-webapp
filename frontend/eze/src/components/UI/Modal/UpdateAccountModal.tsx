import React, { useState, useEffect } from "react";
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

const UpdateAccountModal = () => {
  const auth = useSelector((state: IRootState) => state.auth);
  const account = useSelector((state: IRootState) => state.account);
  const dispatch = useDispatch();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [fullName, setFullName] = useState("");
  const [email, setEmail] = useState("");
  const [type, setType] = useState(AccountType.STUDENT_ASSISTANT);
  const [active, setActive] = useState(true);
  const {
    sendRequest: updateAccount,
    data,
    error,
    status: requestStatus,
  } = useHttp<Account>(AccountService.updateAccount, false);

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

  const onUpdateAccount = (event: React.FormEvent<HTMLFormElement>) => {
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
            <form onSubmit={onUpdateAccount}>
              <div className="form-floating mb-3">
                <input
                  type="text"
                  className="form-control"
                  id="updateAccountUsername"
                  onChange={(e) => setUsername(e.target.value)}
                  value={username}
                />
                <label htmlFor="updateAccountUsername">Username</label>
              </div>
              <div className="form-floating mb-3">
                <input
                  type="text"
                  className="form-control"
                  id="updateAccountPassword"
                  onChange={(e) => setPassword(e.target.value)}
                  value={password}
                />
                <label htmlFor="updateAccountPassword">Password</label>
              </div>
              <div className="form-floating mb-3">
                <select
                  className="form-select"
                  id="updateAccountType"
                  onChange={(e) =>
                    setType(
                      e.currentTarget.value === "ADMIN"
                        ? AccountType.ADMIN
                        : AccountType.STUDENT_ASSISTANT
                    )
                  }
                  value={type}
                >
                  <option value={AccountType.ADMIN}>ADMIN</option>
                  <option value={AccountType.STUDENT_ASSISTANT}>
                    STUDENT ASSISTANT
                  </option>
                </select>
                <label htmlFor="updateAccountType">Type</label>
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
              <div className="form-floating mb-3">
                <input
                  type="text"
                  className="form-control"
                  id="updateAccountFullname"
                  onChange={(e) => setFullName(e.target.value)}
                  value={fullName}
                />
                <label htmlFor="updateAccountFullname">Full name</label>
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
