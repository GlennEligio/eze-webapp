import React, { useEffect, useRef, useState } from "react";
import fileDownload from "js-file-download";

interface ImportExportModalProps {
  itemName: string;
  jwt: string;
  downloadFunction: (jwt: string) => Promise<Response>;
  uploadFunction: (jwt: string, formData: FormData) => Promise<Response>;
}

const ImportExportModal: React.FC<ImportExportModalProps> = (props) => {
  const [uploadMessage, setUploadMessage] = useState<string | null>("");
  const [uploadStatus, setUploadStatus] = useState<
    "COMPLETED" | "PENDING" | null
  >(null);
  const [uploadError, setUploadError] = useState(false);
  const [overwrite, setOverwrite] = useState(true);
  const fileInput = useRef<HTMLInputElement | null>(null);
  const modal = useRef<HTMLDivElement | null>(null);
  const form = useRef<HTMLFormElement | null>(null);

  useEffect(() => {
    setUploadError(false);
    setUploadStatus(null);
    setUploadMessage("");

    if (modal.current) {
      modal.current.addEventListener("hidden.bs.modal", function (e) {
        setUploadError(false);
        setUploadStatus(null);
        setUploadMessage("");
        if (form.current) {
          form.current.reset();
        }
      });
    }
  }, [modal.current, form.current]);

  const downloadBtnHandler = async () => {
    props
      .downloadFunction(props.jwt)
      .then((resp) => resp.blob())
      .then((blob) => fileDownload(blob, `${props.itemName}.xlsx`))
      .catch((error) => console.log(error));
  };

  const uploadSubmitHandler: React.FormEventHandler = async (event) => {
    event.preventDefault();

    let formData = new FormData();
    if (fileInput && fileInput.current) {
      if (fileInput.current.files && fileInput.current.files[0]) {
        // reset the error, status, message
        setUploadStatus("PENDING");
        setUploadError(false);
        setUploadMessage("");

        formData.append("file", fileInput.current.files[0]);
        formData.append("overwrite", overwrite.toString());
        props
          .uploadFunction(props.jwt, formData)
          .then((res) => {
            switch (res.status) {
              case 200:
                setUploadStatus("COMPLETED");
                return res.json();
              default:
                setUploadStatus("COMPLETED");
                setUploadError(true);
                throw new Error("Upload error");
            }
          })
          .then((data) => {
            const itemsAffected = data[`${props.itemName}s Affected`];
            setUploadMessage(`${props.itemName}s affected: ${itemsAffected}`);
          })
          .catch((error) => {
            setUploadStatus("COMPLETED");
            setUploadError(error.message);
          });
      }
    }
  };

  return (
    <div
      ref={modal}
      className="modal fade"
      id={`importExport${props.itemName}Modal`}
      tabIndex={-1}
      aria-labelledby={`importExport${props.itemName}ModalLabel`}
      aria-hidden="true"
    >
      <div className="modal-dialog modal-dialog-centered">
        <div className="modal-content">
          <div className="modal-header">
            <h1 className="modal-title fs-5">
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
            <div className="mb-3">
              <div className="d-flex justify-content-start align-items-center">
                <div className="mb-2 me-2">Downloads:</div>
                <button
                  className="btn btn-sm btn-success"
                  onClick={downloadBtnHandler}
                >
                  {props.itemName}s.xlsx
                </button>
              </div>
            </div>
            <div>
              <div className="mb-2">Upload</div>
              <div>
                <form onSubmit={uploadSubmitHandler} ref={form}>
                  <div className="mb-2 text-center">
                    {uploadStatus === "PENDING" ? "Uploading..." : ""}
                    {uploadStatus === "COMPLETED" && uploadError
                      ? "Upload error"
                      : ""}
                    {uploadStatus === "COMPLETED" && !uploadError
                      ? uploadMessage
                      : ""}
                  </div>
                  <div className="d-flex justify-content-around align-items-start">
                    <div className="mb-3">
                      <div className="d-flex flex-column align-items-start">
                        <div className="mb-2">
                          <input
                            className="form-control"
                            type="file"
                            name="file"
                            ref={fileInput}
                          />
                        </div>
                        <div className="d-flex align-items-center">
                          <input
                            type={"checkbox"}
                            name="overwrite"
                            onChange={(e) => setOverwrite(e.target.checked)}
                            checked={overwrite}
                          />
                          <span className="ms-1">Overwrite?</span>
                        </div>
                      </div>
                    </div>
                    <div className="mb-3">
                      <button
                        className="btn btn-sm btn-success"
                        type={"submit"}
                      >
                        Submit
                      </button>
                    </div>
                  </div>
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
