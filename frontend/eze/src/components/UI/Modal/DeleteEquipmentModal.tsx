import React, { useState, useEffect, useRef } from "react";
import useHttp, { RequestConfig } from "../../../hooks/useHttp";
import EquipmentService from "../../../api/EquipmentService";
import { useSelector } from "react-redux";
import { IRootState } from "../../../store";
import { equipmentActions } from "../../../store/equipmentSlice";
import { useDispatch } from "react-redux";
import validator from "validator";
import RequestStatusMessage from "../Other/RequestStatusMessage";

const DeleteEquipmentModal = () => {
  const auth = useSelector((state: IRootState) => state.auth);
  const equipment = useSelector((state: IRootState) => state.equipment);
  const dispatch = useDispatch();
  const modal = useRef<HTMLDivElement | null>(null);
  const [name, setName] = useState("");
  const [status, setStatus] = useState("GOOD");
  const [barcode, setBarcode] = useState("");
  const [defectiveSince, setDefectiveSince] = useState("");
  const [isDuplicable, setIsDuplicable] = useState(true);
  const {
    sendRequest: deleteEquipment,
    data,
    error,
    status: requestStatus,
    resetHttpState,
  } = useHttp<boolean>(EquipmentService.deleteEquipment, false);

  // remove Equipment and set selectedEquipment to null after successful delete request
  useEffect(() => {
    if (requestStatus === "completed" && data && error === null) {
      dispatch(
        equipmentActions.removeEquipment({
          equipmentCode: equipment.selectedEquipment?.equipmentCode,
        })
      );
      dispatch(
        equipmentActions.updateSelectedEquipment({ selectedEquipment: null })
      );
    }
  }, [requestStatus, data, error]);

  // prepopulate inputs based on selectedEquipment
  useEffect(() => {
    const selectedEquipment = equipment.selectedEquipment;
    if (selectedEquipment == null) return;
    setName(selectedEquipment.name || "");
    setBarcode(selectedEquipment.barcode || "");
    setStatus(selectedEquipment.status || "");
    setDefectiveSince(selectedEquipment.defectiveSince || "");
    setIsDuplicable(selectedEquipment.isDuplicable || false);
  }, [equipment.selectedEquipment]);

  // hidden modal event handler for resetting useHttp and useInput state
  useEffect(() => {
    if (modal.current !== null && modal.current !== undefined) {
      modal.current.addEventListener("hidden.bs.modal", () => {
        resetHttpState();
        const selectedEquipment = equipment.selectedEquipment;
        if (selectedEquipment == null) return;
        setName(selectedEquipment.name || "");
        setBarcode(selectedEquipment.barcode || "");
        setStatus(selectedEquipment.status || "");
        setDefectiveSince(selectedEquipment.defectiveSince || "");
        setIsDuplicable(selectedEquipment.isDuplicable || false);
      });
    }
  }, []);

  const deleteEquipmentHandler = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    if (validator.isEmpty(equipment.selectedEquipment?.equipmentCode + ""))
      return;

    const requestConf: RequestConfig = {
      headers: {
        Authorization: `Bearer ${auth.accessToken}`,
        "Content-type": "application/json",
      },
      relativeUrl: `/api/v1/equipments/${equipment.selectedEquipment?.equipmentCode}`,
    };
    deleteEquipment(requestConf);
  };

  return (
    <div
      className="modal fade"
      id="deleteEquipmentModal"
      tabIndex={-1}
      aria-labelledby="deleteEquipmentModalLabel"
      aria-hidden="true"
    >
      <div className="modal-dialog modal-dialog-centered">
        <div className="modal-content">
          <div className="modal-header">
            <h1 className="modal-title fs-5" id="equipmentModalLabel">
              Delete Equipment
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
                loadingMessage="Deleting equipment..."
                status={requestStatus}
                successMessage="Equipment deleted"
                key={"Delete Equipment"}
              />
            }
            <form onSubmit={deleteEquipmentHandler}>
              <div className="form-floating mb-3">
                <input
                  type="text"
                  className="form-control"
                  id="deleteEquipmentName"
                  placeholder="Sample equipment name"
                  readOnly={true}
                  value={name}
                />
                <label htmlFor="deleteEquipmentName">Name</label>
              </div>
              <div className="form-floating mb-3">
                <select
                  className="form-select"
                  id="deleteEquipmentStatus"
                  aria-label="Status"
                  value={status}
                  disabled={true}
                >
                  <option value="GOOD">GOOD</option>
                  <option value="DEFECTIVE">DEFECTIVE</option>
                </select>
                <label htmlFor="deleteEquipmentStatus">Status</label>
              </div>
              <div className="form-floating mb-3">
                <select
                  className="form-select"
                  id="deleteEquipmentDuplicable"
                  aria-label="IsDuplicable"
                  disabled={true}
                  value={isDuplicable ? "YES" : "NO"}
                >
                  <option value="YES">YES</option>
                  <option value="NO">NO</option>
                </select>
                <label htmlFor="deleteEquipmentDuplicable">Duplicable?</label>
              </div>
              <div className="form-floating mb-3">
                <input
                  type="text"
                  className="form-control"
                  id="deleteEquipmentBarcode"
                  placeholder="SOMEBARCODESTRING"
                  disabled={true}
                  value={barcode}
                />
                <label htmlFor="deleteEquipmentBarcode">Barcode</label>
              </div>
              <div className="form-floating mb-3">
                <input
                  type="datetime-local"
                  className="form-control"
                  id="deleteEquipmentDefectivesince"
                  placeholder="April 24, 2020"
                  value={defectiveSince}
                  disabled={true}
                />
                <label htmlFor="deleteEquipmentDefectivesince">
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
                <button type="submit" className="btn btn-danger">
                  Delete Equipment
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default DeleteEquipmentModal;
