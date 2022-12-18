import React, { useRef, useState, useEffect } from "react";
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
import { validateContains } from "../../../validation/validations";
import { validateNotEmpty } from "../../../validation/validations";
import { InputType } from "../../../hooks/useInput";
import RequestStatusMessage from "../Other/RequestStatusMessage";

const UpdateEquipmentModal = () => {
  const auth = useSelector((state: IRootState) => state.auth);
  const equipment = useSelector((state: IRootState) => state.equipment);
  const dispatch = useDispatch();
  const modal = useRef<HTMLDivElement | null>(null);
  const [defectiveSince, setDefectiveSince] = useState("");
  const {
    sendRequest: updateEquipment,
    data,
    error,
    status: requestStatus,
    resetHttpState,
  } = useHttp<Equipment>(EquipmentService.updateEquipment, false);

  // useInput for the controlled input and validation
  const {
    value: name,
    hasError: nameInputHasError,
    isValid: nameIsValid,
    valueChangeHandler: nameChangeHandler,
    inputBlurHandler: nameBlurHandler,
    reset: resetNameInput,
    errorMessage: nameErrorMessage,
    set: setName,
  } = useInput(validateNotEmpty("Equipment name"), "", InputType.TEXT);

  const {
    value: barcode,
    hasError: barcodeInputHasError,
    isValid: barcodeIsValid,
    valueChangeHandler: barcodeChangeHandler,
    inputBlurHandler: barcodeBlurHandler,
    reset: resetBarcodeInput,
    errorMessage: barcodeErrorMessage,
    set: setBarcode,
  } = useInput(validateNotEmpty("Barcode"), "", InputType.TEXT);

  const {
    value: status,
    hasError: statusInputHasError,
    isValid: statusIsValid,
    valueChangeHandler: statusChangeHandler,
    inputBlurHandler: statusBlurHandler,
    reset: resetStatusInput,
    errorMessage: statusErrorMessage,
    set: setStatus,
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
    set: setIsDuplicable,
  } = useInput(
    validateContains("Is Duplicable", ["YES", "NO"]),
    "YES",
    InputType.SELECT
  );

  // prepopulate inputs based on selectedEquipment
  useEffect(() => {
    const selectedEquipment = equipment.selectedEquipment;
    if (selectedEquipment === null) return;
    setName(selectedEquipment.name ? selectedEquipment.name : "");
    setBarcode(selectedEquipment.barcode ? selectedEquipment.barcode : "");
    setStatus(selectedEquipment.status ? selectedEquipment.status : "");
    setDefectiveSince(
      selectedEquipment.defectiveSince ? selectedEquipment.defectiveSince : ""
    );
    setIsDuplicable(
      selectedEquipment.isDuplicable
        ? selectedEquipment.isDuplicable
          ? "YES"
          : "NO"
        : "YES"
    );
  }, [equipment.selectedEquipment]);

  // Update Equipment in the Redux
  useEffect(() => {
    if (requestStatus == "completed" && data && error === null) {
      dispatch(equipmentActions.updateEquipment({ equipment: data }));
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

  const updateEquipmentHandler = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const updatedEquipment: CreateUpdateEquipmentDto = {
      name: name,
      status: status,
      barcode: barcode,
      defectiveSince: defectiveSince,
      isDuplicable: isDuplicable === "YES" ? true : false,
    };

    if (
      !nameIsValid ||
      !statusIsValid ||
      !barcodeIsValid ||
      !isDuplicableIsValid
    ) {
      return;
    }

    const requestConf: RequestConfig = {
      body: updatedEquipment,
      headers: {
        Authorization: `Bearer ${auth.accessToken}`,
        "Content-type": "application/json",
      },
      relativeUrl: `/api/v1/equipments/${equipment.selectedEquipment?.equipmentCode}`,
    };

    updateEquipment(requestConf);
  };

  return (
    <div
      className="modal fade"
      id="updateEquipmentModal"
      tabIndex={-1}
      aria-labelledby="updateEquipmentModalLabel"
      aria-hidden="true"
      ref={modal}
    >
      <div className="modal-dialog modal-dialog-centered">
        <div className="modal-content">
          <div className="modal-header">
            <h1 className="modal-title fs-5" id="equipmentModalLabel">
              Update Equipment
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
                loadingMessage="Updating equipment..."
                status={requestStatus}
                successMessage="Equipment updated"
                key={"Update Equipment"}
              />
            }
            <form onSubmit={updateEquipmentHandler}>
              <div className={nameInputHasError ? "invalid" : ""}>
                {nameInputHasError && <span>{nameErrorMessage}</span>}
                <div className="form-floating mb-3">
                  <input
                    type="text"
                    className="form-control"
                    id="updateEquipmentName"
                    placeholder="Sample equipment name"
                    onChange={nameChangeHandler}
                    onBlur={nameBlurHandler}
                    value={name}
                  />
                  <label htmlFor="updateEquipmentName">Name</label>
                </div>
              </div>
              <div className={statusInputHasError ? "invalid" : ""}>
                {statusInputHasError && <span>{statusErrorMessage}</span>}
                <div className="form-floating mb-3">
                  <select
                    className="form-select"
                    id="updateEquipmentStatus"
                    aria-label="Status"
                    onChange={statusChangeHandler}
                    onBlur={statusBlurHandler}
                    value={status}
                  >
                    <option value="GOOD">GOOD</option>
                    <option value="DEFECTIVE">DEFECTIVE</option>
                  </select>
                  <label htmlFor="updateEquipmentStatus">Status</label>
                </div>
              </div>
              <div className={isDuplicableInputHasError ? "invalid" : ""}>
                {isDuplicableInputHasError && (
                  <span>{isDuplicableErrorMessage}</span>
                )}
                <div className="form-floating mb-3">
                  <select
                    className="form-select"
                    id="updateEquipmentDuplicable"
                    aria-label="IsDuplicable"
                    onChange={isDuplicableChangeHandler}
                    onBlur={isDuplicableBlurHander}
                    value={isDuplicable}
                  >
                    <option value="YES">YES</option>
                    <option value="NO">NO</option>
                  </select>
                  <label htmlFor="updateEquipmentDuplicable">Duplicable?</label>
                </div>
              </div>
              <div className={barcodeInputHasError ? "invalid" : ""}>
                {barcodeInputHasError && <span>{barcodeErrorMessage}</span>}
                <div className="form-floating mb-3">
                  <input
                    type="text"
                    className="form-control"
                    id="updateEquipmentBarcode"
                    placeholder="SOMEBARCODESTRING"
                    onChange={barcodeChangeHandler}
                    onBlur={barcodeBlurHandler}
                    value={barcode}
                  />
                  <label htmlFor="updateEquipmentBarcode">Barcode</label>
                </div>
              </div>
              <div className="form-floating mb-3">
                <input
                  type="datetime-local"
                  className="form-control"
                  id="updateEquipmentDefectivesince"
                  placeholder="April 24, 2020"
                  onChange={(e) => setDefectiveSince(e.target.value)}
                  value={defectiveSince}
                />
                <label htmlFor="updateEquipmentDefectivesince">
                  Defective since?
                </label>
                <div className="modal-footer">
                  <button
                    type="button"
                    className="btn btn-secondary"
                    data-bs-dismiss="modal"
                  >
                    Close
                  </button>
                  <button type="submit" className="btn btn-primary">
                    Update Equipment
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

export default UpdateEquipmentModal;
