import { MouseEventHandler, useEffect } from "react";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import AccountItem from "../components/Account/AccountItem";
import useHttp from "../hooks/useHttp";
import { Account } from "../models/Account";
import { IRootState } from "../store";

const Accounts = () => {
  const auth = useSelector((state: IRootState) => state.auth);
  const navigate = useNavigate();

  const fetchAccounts = async () => {
    const responseObj = fetch("http://localhost:3200/api/accounts", {
      headers: {
        Authorization: `Bearer ${auth.accessToken}`,
        "Content-Type": "application/json",
      },
    }).then((res) => {
      if (res.ok) {
        return res.json();
      }
      throw new Error("Cant fetch the account list");
    });
    return responseObj;
  };

  const { data: accounts, error, sendRequest } = useHttp(fetchAccounts, true);

  const backBtnHandler: MouseEventHandler = () => {
    navigate("/");
  };

  useEffect(() => {
    sendRequest();
  }, [accounts]);

  return (
    <div className="container-md d-flex flex-column h-100">
      {/* <!-- Header --> */}
      <div className="row">
        <header>
          <div className="pt-5 pb-2">
            <div className="d-flex justify-content-between">
              <div className="my-auto">
                <i
                  className="bi bi-arrow-left-circle fs-1"
                  onClick={backBtnHandler}
                ></i>
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
          {accounts instanceof Array && accounts.length > 0 && (
            <div className="row gx-0 overflow-auto">
              <div className="col-12 table-responsive-xxl">
                <table
                  className="table table-hover"
                  style={{ minWidth: "1200px" }}
                >
                  <thead className="table-dark">
                    <tr>
                      <th>Full name</th>
                      <th>Username</th>
                      <th>User type</th>
                      <th>Date and Time Registered</th>
                      <th>Email</th>
                    </tr>
                  </thead>
                  <tbody>
                    {accounts.map((account: Account) => (
                      <AccountItem account={account} key={account._id} />
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}
          {accounts instanceof Array && accounts.length === 0 && (
            <div className="d-flex justify-content-center">
              <span>Empty account list</span>
            </div>
          )}
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
                <button className="btn btn-outline-dark">
                  <i className="bi bi-person-plus-fill fs-5"></i>
                  <span>Add</span>
                </button>
              </div>
              <div className="px-4">
                <button className="btn btn-outline-dark">
                  <i className="bi bi-pencil-fill fs-5"></i>
                  <span>Edit</span>
                </button>
              </div>
              <div className="px-4">
                <button className="btn btn-outline-dark">
                  <i className="bi bi-trash-fill fs-5"></i>
                  <span>Delete</span>
                </button>
              </div>
            </div>
            <div className="col-2 d-flex flex-column align-items-end justify-content-center">
              <span>1:49 AM</span>
              <span>12 Oct 2019</span>
            </div>
          </div>
        </main>
      </div>
    </div>
  );
};

export default Accounts;
