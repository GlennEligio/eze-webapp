import React, { FormEventHandler, MouseEventHandler, useEffect } from "react";
import { useSelector } from "react-redux";
import TransactionService from "../../../api/TransactionService";
import useHttp, { RequestConfig } from "../../../hooks/useHttp";
import { IRootState } from "../../../store";
import RequestStatusMessage from "../Other/RequestStatusMessage";
import { transactionAction } from "../../../store/transactionSlice";
import { useDispatch } from "react-redux";

interface CancelTransactionModalProps {
  selectedTxCode: string;
  previousModalId: string;
}

const CancelTransactionModal: React.FC<CancelTransactionModalProps> = (
  props
) => {
  const auth = useSelector((state: IRootState) => state.auth);
  const dispatch = useDispatch();
  const {
    data,
    error,
    status,
    sendRequest: cancelTransaction,
  } = useHttp<boolean>(TransactionService.cancelTransaction, false);

  // remove the cancelled transaction if the request is success
  useEffect(() => {
    if (data && error === null && status === "completed") {
      dispatch(
        transactionAction.removeTransaction({ txCode: props.selectedTxCode })
      );
      dispatch(
        transactionAction.updateSelectedTransaction({
          selectedTransaction: null,
        })
      );
    }
  }, [data, error, status]);

  const cancelTransactionHandler: FormEventHandler = (event) => {
    event.preventDefault();
    const requestConfig: RequestConfig = {
      headers: {
        Authorization: `Bearer ${auth.accessToken}`,
      },
      method: "DELETE",
      relativeUrl: `/api/v1/transactions/student/${props.selectedTxCode}`,
    };

    cancelTransaction(requestConfig);
  };

  return (
    <div
      className="modal fade"
      id="cancelTransactionModal"
      tabIndex={-1}
      aria-labelledby="cancelTransactionModal"
      aria-hidden="true"
    >
      <div className="modal-dialog modal-dialog-centered">
        <div className="modal-content">
          <div className="modal-header">
            <h1 className="modal-title fs-5">Cancel Transaction</h1>
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
                status={status}
                loadingMessage="Cancelling transaction..."
                successMessage="Transaction cancelled"
                startMessage="Do you want to cancel the transaction?"
                key="Cancel Transaction"
              />
            }
            <form onSubmit={cancelTransactionHandler}>
              <div className="form-floating mb-3">
                <input
                  type="text"
                  className="form-control"
                  id="cancelTransactionCode"
                  value={props.selectedTxCode}
                  disabled={true}
                />
                <label htmlFor="cancelTransactionCode">Code</label>
              </div>
              <div className="modal-footer">
                <button
                  type="button"
                  className="btn btn-secondary"
                  data-bs-toggle="modal"
                  data-bs-target={props.previousModalId}
                >
                  Back
                </button>
                <button type="submit" className="btn btn-danger">
                  Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CancelTransactionModal;
