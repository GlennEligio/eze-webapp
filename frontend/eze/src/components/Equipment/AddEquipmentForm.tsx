import React, { useRef, useState, useEffect } from "react";
import useHttp, { RequestConfig } from "../../hooks/useHttp";
import { Equipment, CreateEquipmentDto } from "../../api/EquipmentService";
import * as EquipmentService from "../../api/EquipmentService";
import { useSelector } from "react-redux";
import { IRootState } from "../../store";
import { equipmentActions } from "../../store/equipmentSlice";
import { useDispatch } from "react-redux";

const AddEquipmentForm = () => {
  const addEquipmentForm = useRef<HTMLFormElement>(null);
  const submitButton = useRef<HTMLButtonElement>(null);
  const auth = useSelector((state: IRootState) => state.auth);
  const equipment = useSelector((state: IRootState) => state.equipment);
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
    if (requestStatus == "completed") {
      if (error == null) {
        dispatch(equipmentActions.addEquipment({ newEquipment: data }));
      }
    }
  }, [data]);

  const onAddEquipment = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const newEquipment: CreateEquipmentDto = {
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
  };

  return (
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
          <form ref={addEquipmentForm} onSubmit={onAddEquipment}>
            <div className="form-floating mb-3">
              <input
                type="text"
                className="form-control"
                id="newEquipmentName"
                placeholder="Sample equipment name"
                onChange={(e) => setName(e.target.value)}
              />
              <label htmlFor="newEquipmentName">Name</label>
            </div>
            <div className="form-floating mb-3">
              <select
                className="form-select"
                id="newEquipmentStatus"
                aria-label="Status"
                onChange={(e) => setStatus(e.currentTarget.value)}
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
              />
              <label htmlFor="newEquipmentDefectivesince">
                Defective since?
              </label>
            </div>
            <button
              type="submit"
              ref={submitButton}
              style={{ display: "none" }}
            />
          </form>
        </div>
        <div className="modal-footer">
          <button
            type="button"
            className="btn btn-secondary"
            data-bs-dismiss="modal"
          >
            Close
          </button>
          <button
            type="button"
            className="btn btn-primary"
            onClick={() => submitButton.current?.click()}
          >
            Add Equipment
          </button>
        </div>
      </div>
    </div>
  );
};

export default AddEquipmentForm;
