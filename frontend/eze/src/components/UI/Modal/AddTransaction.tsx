import React, {
  useState,
  useEffect,
  useRef,
  PropsWithChildren,
  FormEventHandler,
} from "react";
import useHttp, { RequestConfig } from "../../../hooks/useHttp";
import TransactionService, {
  CreateUpdateTransaction,
  TransactionFull,
  TxStatus,
} from "../../../api/TransactionService";
import { useSelector } from "react-redux";
import { IRootState } from "../../../store";
import { Equipment } from "../../../api/EquipmentService";
import { Professor } from "../../../api/ProfessorService";
import {
  isValidStudent,
  Student,
  StudentFull,
} from "../../../api/StudentService";
import RequestStatusMessage from "../Other/RequestStatusMessage";

interface AddTransactionProps {
  params: string;
  accessToken: string;
  equipments: Equipment[];
  student: StudentFull;
  professor: Professor;
}

const AddTransaction: React.FC<PropsWithChildren<AddTransactionProps>> = (
  props
) => {
  const {
    sendRequest: createTransaction,
    data,
    error,
    status,
  } = useHttp<TransactionFull>(TransactionService.createTransaction, true);

  // Fetch transaction details when selectedTransaction changes
  const createTransactionSubmitHandler: FormEventHandler = (event) => {
    event.preventDefault();

    const requestBody: CreateUpdateTransaction = {
      borrower: props.student,
      equipments: props.equipments,
      professor: props.professor,
      status: TxStatus.PENDING,
    };

    const params = new URLSearchParams({
      complete: "true",
    }).toString();

    const requestConfig: RequestConfig = {
      headers: {
        Authorization: `Bearer ${props.accessToken}`,
      },
      body: requestBody,
      method: "POST",
      relativeUrl: `/api/v1/student?${params}`,
    };

    createTransaction(requestConfig);
  };

  return (
    <div
      className="modal fade"
      id="addTransactionModal"
      tabIndex={-1}
      aria-labelledby="addTransactionModalLabel"
      aria-hidden="true"
    >
      <div className="modal-dialog modal-dialog-centered modal-dialog-scrollable">
        <div className="modal-content">
          <div className="modal-header">
            <h1 className="modal-title fs-5">Add Transaction</h1>
            <button
              type="button"
              className="btn-close"
              data-bs-dismiss="modal"
              aria-label="Close"
            ></button>
          </div>
          <div className="modal-body">
            <form onSubmit={createTransactionSubmitHandler}>
              {
                <RequestStatusMessage
                  data={data}
                  error={error}
                  key={"Add Transaction"}
                  loadingMessage="Creating Transaction..."
                  status={status}
                  successMessage="Transaction added"
                />
              }
              <div className="form-floating mb-3">
                <textarea
                  className="form-control"
                  id="transactionEquipments"
                  value={props.equipments.map((e) => e.name).join(", ")}
                  readOnly
                ></textarea>
                <label htmlFor="transactionEquipments">Equipments</label>
              </div>
              <div className="form-floating mb-3">
                <input
                  type="text"
                  className="form-control"
                  id="transactionBorrower"
                  value={props.student.fullName}
                  disabled
                ></input>
                <label htmlFor="transactionBorrower">Borrower</label>
              </div>
              <div className="form-floating mb-3">
                <input
                  type="text"
                  className="form-control"
                  id="transactionYearAndSection"
                  value={props.student.yearAndSection.sectionName}
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
                  value={props.professor.name}
                  disabled
                />
                <label htmlFor="transactionProfessor">Professor</label>
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
                  <button className="btn btn-success" type="submit">
                    Create
                  </button>
                </div>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AddTransaction;
