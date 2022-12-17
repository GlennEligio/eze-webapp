import React, {
  FormEventHandler,
  MouseEventHandler,
  useEffect,
  useState,
} from "react";
import { useDispatch } from "react-redux";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { AccountType } from "../api/AccountService";
import TransactionService, {
  Transaction,
  TxStatus,
} from "../api/TransactionService";
import TransactionItem from "../components/Transaction/TransactionItem";
import CancelTransactionModal from "../components/UI/Modal/CancelTransactionModal";
import ShowTransactionDetails from "../components/UI/Modal/ShowTransactionDetails";
import UpdateTransactionModal from "../components/UI/Modal/UpdateTransactionModal";
import MiniClock from "../components/UI/Other/MiniClock";
import useHttp, { RequestConfig } from "../hooks/useHttp";
import { IRootState } from "../store";
import { transactionAction } from "../store/transactionSlice";

interface StudentTransactionsProps {
  historical: "true" | "false";
  returned: "true" | "false";
  cancellable?: boolean;
  type: "HISTORY" | "BORROW/RETURN";
  pageTitle: string;
  updateable?: boolean;
  transactionOf: "student" | "professor";
}

const StudentTransactions: React.FC<StudentTransactionsProps> = (props) => {
  const auth = useSelector((state: IRootState) => state.auth);
  const transaction = useSelector((state: IRootState) => state.transaction);
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const [status, setStatus] = useState<TxStatus>();
  const [toDate, setToDate] = useState("");
  const [fromDate, setFromDate] = useState("");

  const {
    sendRequest: getStudentTransactions,
    data: studentTransactions,
    error: getStudentTransactionsError,
    status: getStudentTransactionsStatus,
  } = useHttp<Transaction[]>(TransactionService.getStudentTransaction, true);

  // Prepopulate the table of current transactions
  useEffect(() => {
    const params = new URLSearchParams({
      historical: props.historical,
      returned: props.returned,
    });
    const requestConfig: RequestConfig = {
      headers: {
        Authorization: `Bearer ${auth.accessToken}`,
      },
      method: "GET",
      relativeUrl:
        `/api/v1/transactions/${props.transactionOf}/${auth.username}?` +
        params.toString(),
    };

    getStudentTransactions(requestConfig);
  }, []);

  // Populate Transaction State when a successful getCurrentTransaction request happens
  useEffect(() => {
    if (
      getStudentTransactionsStatus === "completed" &&
      getStudentTransactionsError === null &&
      studentTransactions
    ) {
      if (studentTransactions instanceof Array) {
        let transactionDisplay: Transaction[];
        if (props.historical) {
          transactionDisplay = [...studentTransactions].map((t) => {
            return { ...t, equipmentsCount: t.equipmentsHistCount };
          });
        } else {
          transactionDisplay = [...studentTransactions];
        }
        dispatch(
          transactionAction.addTransactions({
            transactions: transactionDisplay,
          })
        );
      }
    }
  }, [
    studentTransactions,
    getStudentTransactionsError,
    getStudentTransactionsStatus,
  ]);

  const searchTxSubmitHandler: FormEventHandler = (event) => {
    event.preventDefault();

    const params = new URLSearchParams({
      historical: props.historical,
      returned: props.returned,
      status: status ? status : "",
      fromDate,
      toDate,
    });
    const requestConfig: RequestConfig = {
      headers: {
        Authorization: `Bearer ${auth.accessToken}`,
      },
      method: "GET",
      relativeUrl:
        `/api/v1/transactions/${props.transactionOf}/${auth.username}?` +
        params.toString(),
    };

    getStudentTransactions(requestConfig);
  };

  const backBtnClickHandler = () => {
    navigate("/");
  };

  // Transaction Item click Handler to update selectedTransaction in Redux Store
  const transactionItemClickHandler = (selectedTransaction: Transaction) => {
    dispatch(
      transactionAction.updateSelectedTransaction({
        selectedTransaction,
      })
    );
  };

  const resetSearchBtnHandler: MouseEventHandler = (event) => {
    event.preventDefault();

    setStatus(undefined);
    setToDate("");
    setFromDate("");
  };

  return (
    <>
      <div className="container-lg d-flex flex-column h-100">
        <div className="row">
          <header>
            <div className="pt-1 pb-2">
              <div className="d-flex justify-content-between">
                <div className="my-auto">
                  <span>
                    <a
                      role="button"
                      className="back-button"
                      onClick={() => backBtnClickHandler()}
                    >
                      <i className="bi-arrow-left-circle fs-1"></i>
                    </a>
                  </span>
                </div>
                <div className="d-flex justify-content-end">
                  <div className="d-flex align-items-center">
                    <i className="bi bi-clock-history fs-1"></i>
                  </div>
                  <div className="d-flex flex-column justify-content-center ms-3 flex-grow-1">
                    <span className="fs-3">{props.pageTitle}</span>
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
                <form className="w-100" onSubmit={searchTxSubmitHandler}>
                  <div className="d-flex justify-content-end">
                    <div className="d-flex align-items-center me-4">
                      <div className="me-2 fs-5">From:</div>
                      <input
                        type="datetime-local"
                        className="form-control"
                        onChange={(e) => setFromDate(e.target.value)}
                        value={fromDate}
                      />
                    </div>
                    <div className="d-flex align-items-center me-4">
                      <div className="me-2 fs-5">To:</div>
                      <input
                        type="datetime-local"
                        className="form-control"
                        onChange={(e) => setToDate(e.target.value)}
                        value={toDate}
                      />
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
                          {status ? status : "Status"}
                        </button>
                        <ul
                          className="dropdown-menu"
                          style={{ zIndex: "1000" }}
                        >
                          <li onClick={() => setStatus(undefined)}>
                            <a className="dropdown-item">All</a>
                          </li>
                          <li onClick={() => setStatus(TxStatus.PENDING)}>
                            <a className="dropdown-item">
                              <i className="bi bi-hourglass-split"></i> Pending
                            </a>
                          </li>
                          <li onClick={() => setStatus(TxStatus.ACCEPTED)}>
                            <a className="dropdown-item">
                              <i className="bi bi-check"></i> Accepted
                            </a>
                          </li>
                          <li onClick={() => setStatus(TxStatus.DENIED)}>
                            <a className="dropdown-item">
                              <i className="bi bi-x"></i>Denied
                            </a>
                          </li>
                        </ul>
                      </div>
                      <button
                        className="btn btn-dark"
                        type={"button"}
                        onClick={resetSearchBtnHandler}
                      >
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
                      {transaction.transactions.map((t) => {
                        return (
                          <TransactionItem
                            data-bs-toggle="modal"
                            data-bs-target="#transactionDetailsModal"
                            focused={false}
                            key={t.txCode}
                            onTransactionItemClick={transactionItemClickHandler}
                            transaction={t}
                          />
                        );
                      })}
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
      <div>
        <ShowTransactionDetails
          params={new URLSearchParams({
            complete: "true",
            historical: props.historical,
          }).toString()}
          type={props.type}
        >
          {props.cancellable && (
            <button
              className="btn btn-danger"
              data-bs-toggle="modal"
              data-bs-target="#cancelTransactionModal"
            >
              Cancel
            </button>
          )}
          {props.updateable && (
            <button
              className="btn btn-success"
              data-bs-toggle="modal"
              data-bs-target="#updateTransactionStatusModal"
            >
              Update
            </button>
          )}
        </ShowTransactionDetails>
        {props.cancellable && (
          <CancelTransactionModal
            previousModalId="#transactionDetailsModal"
            selectedTxCode={transaction.selectedTransaction?.txCode || ""}
          />
        )}
        {props.updateable && (
          <UpdateTransactionModal
            previousModalId="#transactionDetailsModal"
            selectedTxCode={transaction.selectedTransaction?.txCode || ""}
          />
        )}
      </div>
    </>
  );
};

export default StudentTransactions;
