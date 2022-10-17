import React, { useRef, useState, useEffect } from "react";
import useHttp, { RequestConfig } from "../../../hooks/useHttp";
import {
  Equipment,
  CreateUpdateEquipmentDto,
} from "../../../api/EquipmentService";
import * as EquipmentService from "../../../api/EquipmentService";
import { useSelector } from "react-redux";
import { IRootState } from "../../../store";
import { equipmentActions } from "../../../store/equipmentSlice";
import { useDispatch } from "react-redux";

const UpdateEquipmentModal = () => {
  const auth = useSelector((state: IRootState) => state.auth);
  const equipment = useSelector((state: IRootState) => state.equipment);
  const dispatch = useDispatch();
  const [name, setName] = useState("");
  const [status, setStatus] = useState("GOOD");
  const [barcode, setBarcode] = useState("");
  const [defectiveSince, setDefectiveSince] = useState("");
  const [isDuplicable, setIsDuplicable] = useState(true);
  const {
    sendRequest: updateEquipment,
    data,
    error,
    status: requestStatus,
  } = useHttp<Equipment>(EquipmentService.updateEquipment, false);

  useEffect(() => {
    const selectedEquipment = equipment.selectedEquipment;
    if (selectedEquipment == null) return;
    setName(selectedEquipment?.name as string);
    setBarcode(selectedEquipment?.barcode as string);
    setStatus(selectedEquipment?.status as string);
    setDefectiveSince(selectedEquipment?.defectiveSince as string);
    setIsDuplicable(selectedEquipment?.isDuplicable as boolean);
  }, [equipment.selectedEquipment]);

  useEffect(() => {
    if (requestStatus == "completed") {
      if (error === null) {
        dispatch(equipmentActions.updateEquipment({ equipment: data }));
      }
    }
  }, [data]);

  const onUpdateEquipment = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const updatedEquipment: CreateUpdateEquipmentDto = {
      name: name,
      status: status,
      barcode: barcode,
      defectiveSince: defectiveSince,
      isDuplicable: isDuplicable,
    };

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
            <form onSubmit={onUpdateEquipment}>
              <div className="form-floating mb-3">
                <input
                  type="text"
                  className="form-control"
                  id="updateEquipmentName"
                  placeholder="Sample equipment name"
                  onChange={(e) => setName(e.target.value)}
                  value={name}
                />
                <label htmlFor="updateEquipmentName">Name</label>
              </div>
              <div className="form-floating mb-3">
                <select
                  className="form-select"
                  id="updateEquipmentStatus"
                  aria-label="Status"
                  onChange={(e) => setStatus(e.currentTarget.value)}
                  value={status}
                >
                  <option value="GOOD">GOOD</option>
                  <option value="DEFECTIVE">DEFECTIVE</option>
                </select>
                <label htmlFor="updateEquipmentStatus">Status</label>
              </div>
              <div className="form-floating mb-3">
                <select
                  className="form-select"
                  id="updateEquipmentDuplicable"
                  aria-label="IsDuplicable"
                  onChange={(e) =>
                    setIsDuplicable(e.currentTarget.value === "YES")
                  }
                  value={isDuplicable ? "YES" : "NO"}
                >
                  <option value="YES">YES</option>
                  <option value="NO">NO</option>
                </select>
                <label htmlFor="updateEquipmentDuplicable">Duplicable?</label>
              </div>
              <div className="form-floating mb-3">
                <input
                  type="text"
                  className="form-control"
                  id="updateEquipmentBarcode"
                  placeholder="SOMEBARCODESTRING"
                  onChange={(e) => setBarcode(e.target.value)}
                  value={barcode}
                />
                <label htmlFor="updateEquipmentBarcode">Barcode</label>
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
