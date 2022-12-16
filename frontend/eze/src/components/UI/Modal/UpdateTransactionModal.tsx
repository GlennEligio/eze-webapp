import React, {
  FormEventHandler,
  MouseEventHandler,
  useEffect,
  useState,
} from "react";
import { useSelector } from "react-redux";
import TransactionService, {
  Transaction,
  TxStatus,
} from "../../../api/TransactionService";
import useHttp, { RequestConfig } from "../../../hooks/useHttp";
import { IRootState } from "../../../store";
import RequestStatusMessage from "../Other/RequestStatusMessage";
import { transactionAction } from "../../../store/transactionSlice";
import { useDispatch } from "react-redux";
import useInput, { InputType } from "../../../hooks/useInput";
import { validateContains } from "../../../validation/validations";

interface UpdateTransactionModalProp {
  selectedTxCode: string;
  previousModalId: string;
}

const UpdateTransactionModal: React.FC<UpdateTransactionModalProp> = (
  props
) => {
  const auth = useSelector((state: IRootState) => state.auth);
  const dispatch = useDispatch();
  const {
    data: updateTransactionData,
    error: updateTransactionError,
    status: updateTransactionStatus,
    sendRequest: updateTransactionStatusRequest,
  } = useHttp<boolean>(TransactionService.updateTransactionStatus, false);
  const {
    value: status,
    hasError: statusHasError,
    isValid: statusIsValid,
    valueChangeHandler: statusChangeHandler,
    inputBlurHandler: statusBlurHandler,
    reset: resetStatusInput,
    errorMessage: statusErrorMessage,
  } = useInput(
    validateContains("Status", [TxStatus.ACCEPTED, TxStatus.DENIED]),
    TxStatus.PENDING,
    InputType.SELECT
  );

  // remove the cancelled transaction if the request is success
  useEffect(() => {
    if (
      updateTransactionData &&
      updateTransactionError === null &&
      updateTransactionStatus === "completed"
    ) {
      dispatch(
        transactionAction.updateTransaction({
          trnascation: props.selectedTxCode,
        })
      );
      dispatch(
        transactionAction.updateSelectedTransaction({
          selectedTransaction: { transaction: updateTransactionData },
        })
      );
    }
  }, [updateTransactionData, updateTransactionError, updateTransactionStatus]);

  const updateTransactionStatusHandler: FormEventHandler = (event) => {
    event.preventDefault();
    const param = new URLSearchParams({
      status,
    }).toString();
    const requestConfig: RequestConfig = {
      headers: {
        Authorization: `Bearer ${auth.accessToken}`,
      },
      method: "PUT",
      relativeUrl: `/api/v1/transactions/student/${props.selectedTxCode}?${param}`,
    };

    updateTransactionStatusRequest(requestConfig);
  };

  return (
    <div
      className="modal fade"
      id="updateTransactionStatusModal"
      tabIndex={-1}
      aria-labelledby="updateTransactionStatusModalLabel"
      aria-hidden="true"
    >
      <div className="modal-dialog modal-dialog-centered">
        <div className="modal-content">
          <div className="modal-header">
            <h1 className="modal-title fs-5">Update Transaction Status</h1>
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
                data={updateTransactionData}
                error={updateTransactionError}
                status={updateTransactionStatus}
                loadingMessage="Updating transaction status..."
                successMessage="Transaction status updated"
                startMessage="Do you want to update Transaction status?"
                key="Update Transaction Status"
              />
            }
            <form onSubmit={updateTransactionStatusHandler}>
              <div className="form-floating mb-3">
                <input
                  type="text"
                  className="form-control"
                  id="updateTransactionCode"
                  value={props.selectedTxCode}
                  disabled={true}
                />
                <label htmlFor="update8TransactionCode">Code</label>
              </div>
              <div className={statusHasError ? "invalid" : ""}>
                {statusHasError && <span>{statusErrorMessage}</span>}
                <div className="form-floating mb-3">
                  <select
                    className="form-select"
                    id="updateTransactionStatus"
                    onChange={statusChangeHandler}
                    onBlur={statusBlurHandler}
                    value={status}
                  >
                    <option value={TxStatus.ACCEPTED}>ACCEPTED</option>
                    <option value={TxStatus.PENDING}>PENDING</option>
                    <option value={TxStatus.DENIED}>DENIED</option>
                  </select>
                  <label htmlFor="updateTransactionStatus">Status</label>
                </div>
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
                <button type="submit" className="btn btn-success">
                  Update
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default UpdateTransactionModal;
