import React, { useState, useEffect, useRef } from "react";
import useHttp, { RequestConfig } from "../../../hooks/useHttp";
import EquipmentService, {
  Equipment,
  CreateUpdateEquipmentDto,
  isValidEquipment,
} from "../../../api/EquipmentService";
import { useSelector } from "react-redux";
import { IRootState } from "../../../store";
import { equipmentActions } from "../../../store/equipmentSlice";
import { useDispatch } from "react-redux";
import useInput from "../../../hooks/useInput";
import {
  validateContains,
  validateNotEmpty,
  validatePattern,
} from "../../../validation/validations";
import { InputType } from "../../../hooks/useInput";
import RequestStatusMessage from "../Other/RequestStatusMessage";

const AddEquipmentModal = () => {
  const auth = useSelector((state: IRootState) => state.auth);
  const dispatch = useDispatch();
  const [defectiveSince, setDefectiveSince] = useState("");
  const modal = useRef<HTMLDivElement | null>(null);
  const {
    sendRequest: createEquipment,
    data,
    error,
    status: requestStatus,
    resetHttpState,
  } = useHttp<Equipment>(EquipmentService.createEquipment, false);

  // useInput for the controlled input and validation
  const {
    value: name,
    hasError: nameInputHasError,
    isValid: nameIsValid,
    valueChangeHandler: nameChangeHandler,
    inputBlurHandler: nameBlurHandler,
    reset: resetNameInput,
    errorMessage: nameErrorMessage,
  } = useInput(validateNotEmpty("Equipment name"), "", InputType.TEXT);

  const {
    value: barcode,
    hasError: barcodeInputHasError,
    isValid: barcodeIsValid,
    valueChangeHandler: barcodeChangeHandler,
    inputBlurHandler: barcodeBlurHandler,
    reset: resetBarcodeInput,
    errorMessage: barcodeErrorMessage,
  } = useInput(validateNotEmpty("Barcode"), "", InputType.TEXT);

  const {
    value: status,
    hasError: statusInputHasError,
    isValid: statusIsValid,
    valueChangeHandler: statusChangeHandler,
    inputBlurHandler: statusBlurHandler,
    reset: resetStatusInput,
    errorMessage: statusErrorMessage,
  } = useInput(
    validateContains("Status", ["GOOD", "DEFECTIVE"]),
    "GOOD",
    InputType.SELECT
  );

  const {
    value: isDuplicable,
    hasError: isDuplicableInputHasError,
    isValid: isDuplicableIsValid,
    valueChangeHandler: isDuplicableChangeHandler,
    inputBlurHandler: isDuplicableBlurHander,
    reset: resetIsDuplicable,
    errorMessage: isDuplicableErrorMessage,
  } = useInput(
    validateContains("Is Duplicable", ["YES", "NO"]),
    "YES",
    InputType.SELECT
  );

  // add the Equipment in Redux after successful
  useEffect(() => {
    if (requestStatus === "completed" && data && error === null) {
      dispatch(equipmentActions.addEquipment({ newEquipment: data }));
    }
  }, [data, requestStatus, error]);

  // hidden modal event handler for resetting useHttp and useInput state
  useEffect(() => {
    if (modal.current !== null && modal.current !== undefined) {
      modal.current.addEventListener("hidden.bs.modal", () => {
        resetHttpState();
        resetNameInput();
        resetBarcodeInput();
        resetStatusInput();
        setDefectiveSince("");
        resetIsDuplicable();
      });
    }
  }, []);

  const addEquipmentHandler = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const newEquipment: CreateUpdateEquipmentDto = {
      name: name,
      status: status,
      barcode: barcode,
      defectiveSince: defectiveSince,
      isDuplicable: isDuplicable === "YES" ? true : false,
    };

    if (
      !nameIsValid ||
      !barcodeIsValid ||
      !isDuplicableIsValid ||
      !statusIsValid
    ) {
      return;
    }

    const requestConf: RequestConfig = {
      body: newEquipment,
      headers: {
        Authorization: `Bearer ${auth.accessToken}`,
        "Content-type": "application/json",
      },
    };
    createEquipment(requestConf);
    resetNameInput();
    resetBarcodeInput();
    resetStatusInput();
    setDefectiveSince("");
    resetIsDuplicable();
  };

  return (
    <div
      className="modal fade"
      id="addEquipmentModal"
      tabIndex={-1}
      aria-labelledby="addEquipmentModalLabel"
      aria-hidden="true"
      ref={modal}
    >
      <div className="modal-dialog modal-dialog-centered">
        <div className="modal-content">
          <div className="modal-header">
            <h1 className="modal-title fs-5" id="equipmentModalLabel">
              Add Equipment
            </h1>
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
                loadingMessage="Adding equipment..."
                status={requestStatus}
                successMessage="Equipment added"
                key={"Add Equipment"}
              />
            }
            <form onSubmit={addEquipmentHandler}>
              <div className={nameInputHasError ? "invalid" : ""}>
                {nameInputHasError && <span>{nameErrorMessage}</span>}
                <div className="form-floating mb-3">
                  <input
                    type="text"
                    className="form-control"
                    id="newEquipmentName"
                    placeholder="Sample equipment name"
                    onChange={nameChangeHandler}
                    onBlur={nameBlurHandler}
                    value={name}
                  />
                  <label htmlFor="newEquipmentName">Name</label>
                </div>
              </div>
              <div className={statusInputHasError ? "invalid" : ""}>
                {statusInputHasError && <span>{statusErrorMessage}</span>}
                <div className="form-floating mb-3">
                  <select
                    className="form-select"
                    id="newEquipmentStatus"
                    aria-label="Status"
                    onChange={statusChangeHandler}
                    onBlur={statusBlurHandler}
                    value={status}
                  >
                    <option value="GOOD">GOOD</option>
                    <option value="DEFECTIVE">DEFECTIVE</option>
                  </select>
                  <label htmlFor="newEquipmentStatus">Status</label>
                </div>
              </div>
              <div className={isDuplicableInputHasError ? "invalid" : ""}>
                {isDuplicableInputHasError && (
                  <span>{isDuplicableErrorMessage}</span>
                )}
                <div className="form-floating mb-3">
                  <select
                    className="form-select"
                    id="newEquipmentDuplicable"
                    aria-label="IsDuplicable"
                    onChange={isDuplicableChangeHandler}
                    onBlur={isDuplicableBlurHander}
                    value={isDuplicable}
                  >
                    <option value="YES">YES</option>
                    <option value="NO">NO</option>
                  </select>
                  <label htmlFor="newEquipmentDuplicable">Duplicable?</label>
                </div>
              </div>
              <div className={barcodeInputHasError ? "invalid" : ""}>
                {barcodeInputHasError && <span>{barcodeErrorMessage}</span>}
                <div className="form-floating mb-3">
                  <input
                    type="text"
                    className="form-control"
                    id="newEquipmentBarcode"
                    placeholder="SOMEBARCODESTRING"
                    onChange={barcodeChangeHandler}
                    onBlur={barcodeBlurHandler}
                    value={barcode}
                  />
                  <label htmlFor="newEquipmentBarcode">Barcode</label>
                </div>
              </div>
              <div className="form-floating mb-3">
                <input
                  type="datetime-local"
                  className="form-control"
                  id="newEquipmentDefectivesince"
                  placeholder="April 24, 2020"
                  onChange={(e) => setDefectiveSince(e.target.value)}
                  value={defectiveSince}
                />
                <label htmlFor="newEquipmentDefectivesince">
                  Defective since?
                </label>
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
                  Add Equipment
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AddEquipmentModal;
