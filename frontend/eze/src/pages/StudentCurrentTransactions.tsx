import React, { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import TransactionService, {
  Transaction,
  TxStatus,
} from "../api/TransactionService";
import MiniClock from "../components/UI/Other/MiniClock";
import useHttp, { RequestConfig } from "../hooks/useHttp";
import { IRootState } from "../store";

const StudentCurrentTransactions = () => {
  const auth = useSelector((state: IRootState) => state.auth);
  const [currentTransaction, setCurrentTransaction] = useState<Transaction[]>(
    []
  );
  const [status, setStatus] = useState<TxStatus>(TxStatus.PENDING);

  const {
    sendRequest: getStudentCurrentTransactions,
    data: studentCurrentTransactions,
    error: getStudentCurrentTransactionsError,
    status: getStudentCurrentTransactionsStatus,
    resetHttpState: getStudentCurrentTransactionsResetHttpState,
  } = useHttp<Transaction[]>(TransactionService.getStudentTransaction, true);

  useEffect(() => {
    const params = new URLSearchParams({
      historical: "false",
      returned: "false",
    });
    const requestConfig: RequestConfig = {
      headers: {
        Authorization: `Bearer ${auth.accessToken}`,
      },
      method: "GET",
      relativeUrl: "/api/v1/transactions/student?" + params.toString(),
    };

    getStudentCurrentTransactions(requestConfig);
  }, []);

  return (
    <div className="container-lg d-flex flex-column h-100">
      <div className="row">
        <header>
          <div className="pt-1 pb-2">
            <div className="d-flex justify-content-between">
              <div className="my-auto">
                <span>
                  <a
                    data-bs-toggle="offcanvas"
                    href="#studentOffCanvas"
                    role="button"
                  >
                    <i className="bi bi-list fs-1 me-4 back-button"></i>
                  </a>
                </span>
              </div>
              <div className="d-flex justify-content-end">
                <div className="d-flex align-items-center">
                  <i className="bi bi-clock-history fs-1"></i>
                </div>
                <div className="d-flex flex-column justify-content-center ms-3 flex-grow-1">
                  <span className="fs-3">Current Transactions</span>
                </div>
              </div>
            </div>
          </div>
        </header>
      </div>
      <div className="row h-85">
        <main className="col-12 d-flex flex-column h-100">
          <div className="row mt-2 gx-1">
            <div className="col d-flex align-items-center justify-content-end">
              <form className="w-100">
                <div className="d-flex justify-content-end">
                  <div className="d-flex align-items-center me-4">
                    <div className="me-2 fs-5">From:</div>
                    <input type="datetime-local" className="form-control" />
                  </div>
                  <div className="d-flex align-items-center me-4">
                    <div className="me-2 fs-5">To:</div>
                    <input type="datetime-local" className="form-control" />
                  </div>
                  <div className="d-flex">
                    <button className="btn btn-dark me-2">
                      <i className="bi bi-search"></i> Search
                    </button>
                    <div className="dropdown">
                      <button
                        className="btn btn-dark me-2 dropdown-toggle"
                        type="button"
                        data-bs-toggle="dropdown"
                        aria-expanded="false"
                      >
                        Status
                      </button>
                      <ul className="dropdown-menu" style={{ zIndex: "1000" }}>
                        <li>
                          <a className="dropdown-item" href="#">
                            <i className="bi bi-hourglass-split"></i> Pending
                          </a>
                        </li>
                        <li>
                          <a className="dropdown-item" href="#">
                            <i className="bi bi-check"></i> Accepted
                          </a>
                        </li>
                        <li>
                          <a className="dropdown-item" href="#">
                            <i className="bi bi-x"></i>Rejected
                          </a>
                        </li>
                      </ul>
                    </div>
                    <button className="btn btn-dark">
                      <i className="bi bi-arrow-repeat"></i> Reset
                    </button>
                  </div>
                </div>
              </form>
            </div>
          </div>
          <div className="row mt-2 gx-0 overflow-auto">
            <div className="col">
              <div className="table-responsive-xxl">
                <table
                  className="table table-sm table-hover"
                  style={{
                    minWidth: "1500px",
                  }}
                >
                  <thead className="table-dark">
                    <tr>
                      <th>Transaction Code</th>
                      <th>Borrower</th>
                      <th>Year and Section</th>
                      <th>Equipment Count</th>
                      <th>Professor</th>
                      <th>Borrowed At</th>
                      <th>Returned At</th>
                      <th>Status</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr>
                      <td>123123123</td>
                      <td>John Doe</td>
                      <td>BSECE 1-3</td>
                      <td>3</td>
                      <td>Prof John Doe</td>
                      <td>April 24 123123</td>
                      <td>APril 24 2132312</td>
                      <td>ACCEPTED</td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
          <div className="row py-1 mt-auto">
            <div className="col d-flex justify-content-between align-items-center">
              <div>
                <h5>List of Transactions: 1</h5>
              </div>
              <MiniClock />
            </div>
          </div>
        </main>
      </div>
    </div>
  );
};

export default StudentCurrentTransactions;
