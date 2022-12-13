import React, { useState, useEffect, useRef, PropsWithChildren } from "react";
import useHttp, { RequestConfig } from "../../../hooks/useHttp";
import TransactionService, {
  TransactionFull,
} from "../../../api/TransactionService";
import { useSelector } from "react-redux";
import { IRootState } from "../../../store";

interface ShowTransactionDetailsProps {
  params: string;
  type: "HISTORY" | "BORROW/RETURN";
  cancelModalTarget?: string;
}

const ShowTransactionDetails: React.FC<
  PropsWithChildren<ShowTransactionDetailsProps>
> = (props) => {
  const auth = useSelector((state: IRootState) => state.auth);
  const transaction = useSelector((state: IRootState) => state.transaction);
  const [txCode, setTxCode] = useState("");
  const [equipments, setEquipments] = useState("");
  const [borrower, setBorrower] = useState("");
  const [yearAndSection, setYearAndSection] = useState("");
  const [professor, setProfessor] = useState("");
  const [borrowedAt, setBorrowedAt] = useState("");
  const [returnedAt, setReturnedAt] = useState("");
  const [status, setStatus] = useState("");
  const {
    sendRequest: getTransactionByCode,
    data: transactionData,
    error: getTransactionByCodeError,
    status: getTransactionByCodeStatus,
  } = useHttp<TransactionFull>(TransactionService.getTransactionByCode, true);

  // Fetch transaction details when selectedTransaction changes
  useEffect(() => {
    if (transaction.selectedTransaction) {
      const requestConfig: RequestConfig = {
        headers: {
          Authorization: `Bearer ${auth.accessToken}`,
        },
        relativeUrl: `/api/v1/transactions/${transaction.selectedTransaction.txCode}?${props.params}`,
      };
      getTransactionByCode(requestConfig);
    }
  }, [transaction.selectedTransaction, auth.accessToken]);

  // Update input values when data in useHttp changes
  useEffect(() => {
    if (
      transactionData &&
      getTransactionByCodeError === null &&
      getTransactionByCodeStatus === "completed"
    ) {
      setTxCode(transactionData.txCode || "");
      if (props.type === "HISTORY") {
        setEquipments(
          transactionData.equipmentsHistory &&
            transactionData.equipmentsHistory.length > 0
            ? transactionData.equipmentsHistory.map((t) => t.name).join(", ")
            : ""
        );
      } else {
        setEquipments(
          transactionData.equipments && transactionData.equipments.length > 0
            ? transactionData.equipments.map((t) => t.name).join(", ")
            : ""
        );
      }
      setBorrower(transactionData.borrower || "");
      setBorrowedAt(transactionData.borrowedAt || "");
      setProfessor(transactionData.professor || "");
      setReturnedAt(transactionData.returnedAt || "");
      setStatus(transactionData.status || "");
      setYearAndSection(transactionData.yearAndSection || "");
    }
  }, [transactionData, getTransactionByCodeError, getTransactionByCodeStatus]);

  return (
    <div
      className="modal fade"
      id="transactionDetailsModal"
      tabIndex={-1}
      aria-labelledby="transactionDetailsModal"
      aria-hidden="true"
    >
      <div className="modal-dialog modal-dialog-centered modal-dialog-scrollable">
        <div className="modal-content">
          <div className="modal-header">
            <h1 className="modal-title fs-5">Transaction Details</h1>
            <button
              type="button"
              className="btn-close"
              data-bs-dismiss="modal"
              aria-label="Close"
            ></button>
          </div>
          <div className="modal-body">
            <div className="form-floating mb-3">
              <input
                type="text"
                className="form-control"
                id="transactionTxCode"
                placeholder="Sample transaction Code"
                value={txCode}
                disabled
              />
              <label htmlFor="transactionTxCode">Transaction Code</label>
            </div>
            <div className="form-floating mb-3">
              <textarea
                className="form-control"
                id="transactionEquipments"
                value={equipments}
                readOnly
              ></textarea>
              <label htmlFor="transactionEquipments">Equipments</label>
            </div>
            <div className="form-floating mb-3">
              <input
                type="text"
                className="form-control"
                id="transactionBorrower"
                value={borrower}
                disabled
              ></input>
              <label htmlFor="transactionBorrower">Borrower</label>
            </div>
            <div className="form-floating mb-3">
              <input
                type="text"
                className="form-control"
                id="transactionYearAndSection"
                value={yearAndSection}
                disabled
              />
              <label htmlFor="transactionYearAndSection">
                Year and Section
              </label>
            </div>
            <div className="form-floating mb-3">
              <input
                type="text"
                className="form-control"
                id="transactionProfessor"
                value={professor}
                disabled
              />
              <label htmlFor="transactionProfessor">Professor</label>
            </div>
            <div className="form-floating mb-3">
              <input
                type="text"
                className="form-control"
                id="transactionStatus"
                disabled
                value={status}
              />
              <label htmlFor="transactionStatus">Status</label>
            </div>
            <div className="form-floating mb-3">
              <input
                type="datetime-local"
                className="form-control"
                id="transactionBorrowedAt"
                value={borrowedAt}
                disabled
              />
              <label htmlFor="transactionBorrowedAt">Borrowed At</label>
            </div>
            <div className="form-floating mb-3">
              <input
                type="datetime-local"
                className="form-control"
                id="transactionReturnedAt"
                value={returnedAt}
                disabled
              />
              <label htmlFor="transactionReturnedAt">Returned At</label>
            </div>
            <div className="modal-footer">
              <div className="d-flex justify-content-end">
                <button
                  type="button"
                  className="btn btn-secondary me-2"
                  data-bs-dismiss="modal"
                >
                  Close
                </button>
                {props.children}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ShowTransactionDetails;
