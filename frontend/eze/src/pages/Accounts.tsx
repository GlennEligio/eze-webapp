import { FC, MouseEventHandler, useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import AccountItem from "../components/Account/AccountItem";
import useHttp, { RequestConfig } from "../hooks/useHttp";
import { Account } from "../api/AccountService";
import { IRootState } from "../store";
import AccountService from "../api/AccountService";
import { useDispatch } from "react-redux";
import { accountActions } from "../store/accountSlice";
import AddAccountModal from "../components/UI/Modal/AddAccountModal";
import UpdateAccountModal from "../components/UI/Modal/UpdateAccountModal";
import DeleteAccountModal from "../components/UI/Modal/DeleteAccountModal";
import MiniClock from "../components/UI/Other/MiniClock";
import ImportExportModal from "../components/UI/Modal/ImportExportModal";

const Accounts: FC = () => {
  const auth = useSelector((state: IRootState) => state.auth);
  const account = useSelector((state: IRootState) => state.account);
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const {
    sendRequest: getAccounts,
    data,
    error,
    status,
  } = useHttp<Account[]>(AccountService.getAccounts, true);

  const backBtnHandler: MouseEventHandler = () => {
    navigate("/");
  };

  // Fetch all accounts on start
  useEffect(() => {
    fetchAccounts();
  }, []);

  // Add all accounts in the Context
  useEffect(() => {
    if (status === "completed" && error === null) {
      dispatch(accountActions.addAccounts({ accounts: data }));
    }
  }, [data]);

  // onclick handler to update selected account
  const onUpdateSelectedAccount = (selectedAccount: Account) => {
    if (selectedAccount.username === account.selectedAccount?.username) {
      dispatch(accountActions.updateSelectedAccount({ selectedAccount: null }));
      return;
    }
    dispatch(
      accountActions.updateSelectedAccount({ selectedAccount: selectedAccount })
    );
  };

  // fetches all accounts
  const fetchAccounts = () => {
    const requestConf: RequestConfig = {
      headers: {
        Authorization: `Bearer ${auth.accessToken}`,
      },
    };
    getAccounts(requestConf);
  };

  return (
    <div className="container-md d-flex flex-column h-100">
      {/* <!-- Header --> */}
      <div className="row">
        <header>
          <div className="pt-5 pb-2">
            <div className="d-flex justify-content-between">
              <div className="my-auto">
                <span className="me-3">
                  <i
                    className="bi bi-arrow-left-circle fs-1"
                    onClick={backBtnHandler}
                  ></i>
                </span>
                <a
                  href="#importExportAccountModal"
                  data-bs-toggle="modal"
                  className="text-dark"
                >
                  <i className="bi bi-gear fs-1"></i>
                </a>
              </div>
              <div className="d-flex justify-content-end">
                <div className="d-flex align-items-center">
                  <i className="bi bi-people-fill fs-1"></i>
                </div>
                <div className="d-flex flex-column justify-content-center ms-3">
                  <span className="fs-3">Registered User</span>
                </div>
              </div>
            </div>
          </div>
        </header>
      </div>
      {/* <!-- Main --> */}
      <div className="row h-80">
        <main className="col-12 d-flex flex-column h-100 pb-4">
          {/* <!-- User info table --> */}
          <div className="row mt-2 gx-0 overflow-auto">
            <div className="col">
              <div className="table-responsive-xxl">
                <table
                  className="table table-hover"
                  style={{ minWidth: "1300px" }}
                >
                  <thead className="table-dark">
                    <tr>
                      <th>Id</th>
                      <th>Full name</th>
                      <th>Username</th>
                      <th>Email</th>
                      <th>Type</th>
                      <th>Created at</th>
                      <th>Active</th>
                    </tr>
                  </thead>
                  <tbody>
                    {account.accounts &&
                      account.accounts.length > 0 &&
                      account.accounts.map((a) => {
                        return (
                          <AccountItem
                            account={a}
                            focused={
                              account.selectedAccount?.username === a.username
                            }
                            onRowClick={onUpdateSelectedAccount}
                            key={a.username}
                          />
                        );
                      })}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
          {error && (
            <div className="d-flex justify-content-center">
              <span className="text-danger">
                Unable to fetch the account list
              </span>
            </div>
          )}

          {/* <!-- User action options --> */}
          <div className="row py-3 mt-auto">
            <div className="col-2"></div>
            <div className="col d-flex justify-content-center align-items-center">
              <div className="px-4">
                <button
                  className="btn btn-dark"
                  data-bs-target="#addAccountModal"
                  data-bs-toggle="modal"
                >
                  <i className="bi bi-person-plus-fill fs-5"></i>
                  <span>Add</span>
                </button>
              </div>
              <div className="px-4">
                <button
                  className="btn btn-dark"
                  data-bs-target="#updateAccountModal"
                  data-bs-toggle="modal"
                  disabled={account.selectedAccount === null}
                >
                  <i className="bi bi-pencil-fill fs-5"></i>
                  <span>Edit</span>
                </button>
              </div>
              <div className="px-4">
                <button
                  className="btn btn-dark"
                  data-bs-target="#deleteAccountModal"
                  data-bs-toggle="modal"
                  disabled={account.selectedAccount === null}
                >
                  <i className="bi bi-trash-fill fs-5"></i>
                  <span>Delete</span>
                </button>
              </div>
            </div>
            <div className="col-2 d-flex flex-column align-items-end justify-content-center">
              <MiniClock />
            </div>
          </div>
        </main>
      </div>
      <div>
        <AddAccountModal />
        <UpdateAccountModal />
        <DeleteAccountModal />
        <ImportExportModal
          itemName="Account"
          downloadFunction={AccountService.download}
          uploadFunction={AccountService.upload}
          jwt={auth.accessToken}
          key="Account"
        />
      </div>
    </div>
  );
};

export default Accounts;
