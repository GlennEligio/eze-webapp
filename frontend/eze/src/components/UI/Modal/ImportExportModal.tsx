import React from "react";

interface ImportExportModalProps {
  itemName: string;
  jwt: string;
  downloadFunction: Function;
  uploadFunction: Function;
}

const ImportExportModal: React.FC<ImportExportModalProps> = (props) => {
  return (
    <div
      className="modal fade"
      id="importExportModal"
      tabIndex={-1}
      aria-labelledby="importExportModalLabel"
      aria-hidden="true"
    >
      <div className="modal-dialog modal-dialog-centered">
        <div className="modal-content">
          <div className="modal-header">
            <h1 className="modal-title fs-5" id="importExportModalLabel">
              Download/Upload {props.itemName}
            </h1>
            <button
              type="button"
              className="btn-close"
              data-bs-dismiss="modal"
              aria-label="Close"
            ></button>
          </div>
          <div className="modal-body">
            <div className="row">
              <div className="col-4">Download</div>
              <div className="col-8">
                <button className="btn btn-sm">Download</button>
              </div>
            </div>
            <div className="row">
              <div className="col-4">Upload</div>
              <div className="col-8">
                <form>
                  <input type="file" name="file" />
                </form>
              </div>
            </div>
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
  );
};

export default ImportExportModal;
