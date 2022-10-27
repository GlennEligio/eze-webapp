import React, { useState, useEffect } from "react";
import useHttp, { RequestConfig } from "../../../hooks/useHttp";
import EquipmentService, {
  Equipment,
  CreateUpdateEquipmentDto,
} from "../../../api/EquipmentService";
import { useSelector } from "react-redux";
import { IRootState } from "../../../store";
import { equipmentActions } from "../../../store/equipmentSlice";
import { useDispatch } from "react-redux";

const AddEquipmentModal = () => {
  const auth = useSelector((state: IRootState) => state.auth);
  const dispatch = useDispatch();
  const [name, setName] = useState("");
  const [status, setStatus] = useState("GOOD");
  const [barcode, setBarcode] = useState("");
  const [defectiveSince, setDefectiveSince] = useState("");
  const [isDuplicable, setIsDuplicable] = useState(true);
  const {
    sendRequest: createEquipment,
    data,
    error,
    status: requestStatus,
  } = useHttp<Equipment>(EquipmentService.createEquipment, false);

  useEffect(() => {
    if (requestStatus === "completed") {
      if (error === null) {
        dispatch(equipmentActions.addEquipment({ newEquipment: data }));
      }
    }
  }, [data]);

  const onAddEquipment = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    console.log("Adding Equipment");
    const newEquipment: CreateUpdateEquipmentDto = {
      name: name,
      status: status,
      barcode: barcode,
      defectiveSince: defectiveSince,
      isDuplicable: isDuplicable,
    };

    const requestConf: RequestConfig = {
      body: newEquipment,
      headers: {
        Authorization: `Bearer ${auth.accessToken}`,
        "Content-type": "application/json",
      },
    };
    createEquipment(requestConf);
    setName("");
    setBarcode("");
    setDefectiveSince("");
    setIsDuplicable(true);
    setStatus("GOOD");
  };

  return (
    <div
      className="modal fade"
      id="addEquipmentModal"
      tabIndex={-1}
      aria-labelledby="addEquipmentModalLabel"
      aria-hidden="true"
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
            <form onSubmit={onAddEquipment}>
              <div className="form-floating mb-3">
                <input
                  type="text"
                  className="form-control"
                  id="newEquipmentName"
                  placeholder="Sample equipment name"
                  onChange={(e) => setName(e.target.value)}
                  value={name}
                />
                <label htmlFor="newEquipmentName">Name</label>
              </div>
              <div className="form-floating mb-3">
                <select
                  className="form-select"
                  id="newEquipmentStatus"
                  aria-label="Status"
                  onChange={(e) => setStatus(e.currentTarget.value)}
                  value={status}
                >
                  <option value="GOOD">GOOD</option>
                  <option value="DEFECTIVE">DEFECTIVE</option>
                </select>
                <label htmlFor="newEquipmentStatus">Status</label>
              </div>
              <div className="form-floating mb-3">
                <select
                  className="form-select"
                  id="newEquipmentDuplicable"
                  aria-label="IsDuplicable"
                  onChange={(e) =>
                    setIsDuplicable(e.currentTarget.value === "YES")
                  }
                  value={isDuplicable ? "YES" : "NO"}
                >
                  <option value="YES">YES</option>
                  <option value="NO">NO</option>
                </select>
                <label htmlFor="newEquipmentDuplicable">Duplicable?</label>
              </div>
              <div className="form-floating mb-3">
                <input
                  type="text"
                  className="form-control"
                  id="newEquipmentBarcode"
                  placeholder="SOMEBARCODESTRING"
                  onChange={(e) => setBarcode(e.target.value)}
                  value={barcode}
                />
                <label htmlFor="newEquipmentBarcode">Barcode</label>
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
