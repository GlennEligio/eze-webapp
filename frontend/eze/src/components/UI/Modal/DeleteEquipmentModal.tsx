import React, { useState, useEffect } from "react";
import useHttp, { RequestConfig } from "../../../hooks/useHttp";
import * as EquipmentService from "../../../api/EquipmentService";
import { useSelector } from "react-redux";
import { IRootState } from "../../../store";
import { equipmentActions } from "../../../store/equipmentSlice";
import { useDispatch } from "react-redux";

const DeleteEquipmentModal = () => {
  const auth = useSelector((state: IRootState) => state.auth);
  const equipment = useSelector((state: IRootState) => state.equipment);
  const dispatch = useDispatch();
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
  } = useHttp<boolean>(EquipmentService.deleteEquipment, false);

  useEffect(() => {
    if (requestStatus == "completed") {
      if (error == null) {
        dispatch(
          equipmentActions.removeEquipment({
            equipmentCode: equipment.selectedEquipment?.equipmentCode,
          })
        );
      }
    }
  }, [requestStatus]);

  useEffect(() => {
    const selectedEquipment = equipment.selectedEquipment;
    if (selectedEquipment == null) return;
    setName(selectedEquipment?.name as string);
    setBarcode(selectedEquipment?.barcode as string);
    setStatus(selectedEquipment?.status as string);
    setDefectiveSince(selectedEquipment?.defectiveSince as string);
    setIsDuplicable(selectedEquipment?.isDuplicable as boolean);
  }, [equipment.selectedEquipment]);

  const ondeleteEquipment = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    console.log("Deleting Equipment");

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
            <form onSubmit={ondeleteEquipment}>
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
