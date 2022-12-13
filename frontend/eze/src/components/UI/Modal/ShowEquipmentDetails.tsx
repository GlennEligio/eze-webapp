import React, { useState, useEffect } from "react";
import { Equipment } from "../../../api/EquipmentService";

interface ShowEquipmentDetailsProps {
  equipmentToShow: Equipment | undefined;
}

const ShowEquipmentDetails: React.FC<ShowEquipmentDetailsProps> = (props) => {
  const [name, setName] = useState("");
  const [status, setStatus] = useState("GOOD");
  const [barcode, setBarcode] = useState("");
  const [defectiveSince, setDefectiveSince] = useState("");
  const [isDuplicable, setIsDuplicable] = useState(true);
  const [equipmentCode, setEquipmentCode] = useState("");

  // update modal state based on the props passed
  useEffect(() => {
    if (props.equipmentToShow) {
      setName(props.equipmentToShow.name);
      setStatus(props.equipmentToShow.status);
      setBarcode(props.equipmentToShow.barcode);
      setDefectiveSince(
        props.equipmentToShow.defectiveSince
          ? props.equipmentToShow.defectiveSince
          : ""
      );
      setIsDuplicable(props.equipmentToShow.isDuplicable);
      setEquipmentCode(props.equipmentToShow.equipmentCode);
    }
  }, [props.equipmentToShow]);

  return (
    <div
      className="modal fade"
      id="showEquipmentDetails"
      tabIndex={-1}
      aria-labelledby="showEquipmentDetailsLabel"
      aria-hidden="true"
    >
      <div className="modal-dialog modal-dialog-centered">
        <div className="modal-content">
          <div className="modal-header">
            <h1 className="modal-title fs-5" id="showEquipmentDetailsLabel">
              Equipment Details
            </h1>
            <button
              type="button"
              className="btn-close"
              data-bs-dismiss="modal"
              aria-label="Close"
            ></button>
          </div>
          <div className="modal-body">
            <div className="form-floating mb-3">
              <input
                type="text"
                className="form-control"
                id="showEquipmentEqCode"
                placeholder="Sample equipment code"
                disabled
                value={equipmentCode}
              />
              <label htmlFor="showEquipmentName">Equipment Code</label>
            </div>
            <div className="form-floating mb-3">
              <input
                type="text"
                className="form-control"
                id="showEquipmentName"
                placeholder="Sample equipment name"
                disabled
                value={name}
              />
              <label htmlFor="showEquipmentName">Name</label>
            </div>
            <div className="form-floating mb-3">
              <select
                className="form-select"
                id="showEquipmentStatus"
                aria-label="Status"
                value={status}
                disabled={true}
              >
                <option value="GOOD">GOOD</option>
                <option value="DEFECTIVE">DEFECTIVE</option>
              </select>
              <label htmlFor="showEquipmentStatus">Status</label>
            </div>
            <div className="form-floating mb-3">
              <select
                className="form-select"
                id="showEquipmentDuplicable"
                aria-label="IsDuplicable"
                disabled={true}
                value={isDuplicable ? "YES" : "NO"}
              >
                <option value="YES">YES</option>
                <option value="NO">NO</option>
              </select>
              <label htmlFor="showEquipmentDuplicable">Duplicable?</label>
            </div>
            <div className="form-floating mb-3">
              <input
                type="text"
                className="form-control"
                id="showEquipmentBarcode"
                placeholder="SOMEBARCODESTRING"
                disabled={true}
                value={barcode}
              />
              <label htmlFor="showEquipmentBarcode">Barcode</label>
            </div>
            <div className="form-floating mb-3">
              <input
                type="datetime-local"
                className="form-control"
                id="showEquipmentDefectivesince"
                placeholder="April 24, 2020"
                value={defectiveSince}
                disabled={true}
              />
              <label htmlFor="showEquipmentDefectivesince">
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
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ShowEquipmentDetails;
