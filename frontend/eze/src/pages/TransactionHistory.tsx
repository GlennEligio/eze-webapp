import React, { MouseEventHandler, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useSelector } from "react-redux";
import { IRootState } from "../store";
import TransactionService, { Transaction } from "../api/TransactionService";
import useHttp, { RequestConfig } from "../hooks/useHttp";
import MiniClock from "../components/UI/Other/MiniClock";
import TransactionItem from "../components/Transaction/TransactionItem";
import TransactionDetailsModal from "../components/UI/Modal/TransactionDetailsModal";
import { transactionAction } from "../store/transactionSlice";
import { useDispatch } from "react-redux";
import fileDownload from "js-file-download";

function TransactionHistory() {
  const transaction = useSelector((state: IRootState) => state.transaction);
  const auth = useSelector((state: IRootState) => state.auth);
  const dispatch = useDispatch();
  const [fromDate, setFromDate] = useState("");
  const [toDate, setToDate] = useState("");
  const {
    sendRequest: getTransactionsHist,
    data: transactionHistListData,
    error: getTransactionsError,
    status: getTransactionsStatus,
  } = useHttp<Transaction[]>(TransactionService.getTransactions, false);
  const navigate = useNavigate();

  // back button click handler
  const backBtnClickHandler: MouseEventHandler = () => {
    navigate("/");
  };

  // reset transaction in the Redux Store
  useEffect(() => {
    dispatch(transactionAction.resetState());
  }, []);

  // For setting the equipmentState based on data from useHttp
  useEffect(() => {
    if (
      transactionHistListData &&
      getTransactionsStatus === "completed" &&
      getTransactionsError === null
    ) {
      let transactionModified = [...transactionHistListData].map((t) => {
        return { ...t, equipmentsCount: t.equipmentsHistCount };
      });
      dispatch(
        transactionAction.addTransactions({
          transactions: [...transactionModified],
        })
      );
    }
  }, [transactionHistListData, getTransactionsStatus, getTransactionsError]);

  // onClickHandler to update selected Equipment
  const transactionItemClickHandler = (clickedTransaction: Transaction) => {
    dispatch(
      transactionAction.updateSelectedTransaction({
        selectedTransaction: clickedTransaction,
      })
    );
  };

  // reset click handler
  const resetClickHandler: MouseEventHandler = (e) => {
    e.preventDefault();
    setFromDate("");
    setToDate("");
  };

  // download click handler
  const downloadBtnHandler = async () => {
    let params = "";
    if (!!toDate && !!fromDate) {
      params = `?${new URLSearchParams({ toDate, fromDate }).toString()}`;
    }

    TransactionService.download(auth.accessToken, params)
      .then((resp) => resp.blob())
      .then((blob) => fileDownload(blob, "Transactions.xlsx"))
      .catch((error) => console.log(error));
  };

  // search for the transactions history list
  // TODO: Add UI cue for user to enter to and from dates
  const searchTransactionHandler = (e: React.FormEvent) => {
    e.preventDefault();
    if (!!toDate && !!fromDate) {
      const params = {
        historical: "true",
        returned: "true",
        complete: "false",
        fromDate,
        toDate,
      };

      const requestConfig: RequestConfig = {
        headers: {
          Authorization: `Bearer ${auth.accessToken}`,
        },
        relativeUrl: `/api/v1/transactions?${new URLSearchParams(
          params
        ).toString()}`,
      };

      getTransactionsHist(requestConfig);
    }
  };

  return (
    <>
      <div className="container-lg d-flex flex-column h-100">
        <div className="row">
          <header>
            <div className="pt-2 pb-1">
              <div className="d-flex justify-content-between">
                <div className="my-auto">
                  <span>
                    <i
                      className={`bi bi-arrow-left-circle fs-1 me-4 back-button`}
                      onClick={backBtnClickHandler}
                    ></i>
                  </span>
                </div>
                <div className="d-flex justify-content-end">
                  <div className="d-flex align-items-center">
                    <i className="bi bi-clock-history fs-1"></i>
                  </div>
                  <div className="d-flex flex-column justify-content-center ms-3 flex-grow-1">
                    <span className="fs-3">Transaction History</span>
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
                <form onSubmit={searchTransactionHandler} className="w-100">
                  <div className="d-flex overflow-auto">
                    <div className="d-flex align-items-center me-4">
                      <div className="me-2 fs-5">From:</div>
                      <input
                        type={"datetime-local"}
                        className="form-control"
                        onChange={(e) => setFromDate(e.target.value)}
                        value={fromDate}
                      />
                    </div>
                    <div className="d-flex align-items-center me-4">
                      <div className="me-2 fs-5">To:</div>
                      <input
                        type={"datetime-local"}
                        className="form-control"
                        onChange={(e) => setToDate(e.target.value)}
                        value={toDate}
                      />
                    </div>
                    <div className="d-flex align-items-center">
                      <button type={"submit"} className="btn btn-dark me-2">
                        <i className="bi bi-search"></i> Search
                      </button>
                      <button
                        className="btn btn-dark me-2"
                        onClick={downloadBtnHandler}
                      >
                        <i className="bi bi-download"></i> Download
                      </button>
                      <button
                        type={"button"}
                        onClick={resetClickHandler}
                        className="btn btn-dark"
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
                    className="table table-hover"
                    style={{ minWidth: "1500px" }}
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
                      {[...transaction.transactions].map((t) => {
                        return (
                          <TransactionItem
                            key={t.txCode}
                            transaction={t}
                            focused={
                              transaction.selectedTransaction?.txCode ===
                              t.txCode
                            }
                            onTransactionItemClick={transactionItemClickHandler}
                            data-bs-toggle="modal"
                            data-bs-target="#transactionDetailsModal"
                          />
                        );
                      })}
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
            <div className="row py-3 mt-auto">
              <div className="col d-flex justify-content-between align-items-center">
                <div>
                  <h5>
                    List of Transactions: {transaction.transactions.length}
                  </h5>
                </div>
                <MiniClock />
              </div>
            </div>
          </main>
        </div>
      </div>
      <div>
        <TransactionDetailsModal
          params={new URLSearchParams({
            complete: "true",
            historical: "true",
          }).toString()}
          type="HISTORY"
        />
      </div>
    </>
  );
}

export default TransactionHistory;
