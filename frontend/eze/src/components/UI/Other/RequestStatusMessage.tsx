import React from "react";

interface RequestStatusMessageProps {
  data: any;
  error: string | null;
  status: "pending" | "completed" | null;
  loadingMessage: string;
  successMessage: string;
}

const RequestStatusMessage: React.FC<RequestStatusMessageProps> = (props) => {
  let MessageDisplay = <></>;
  let requestClassName = "";
  if (props.status === "pending") {
    requestClassName = "pending";
    MessageDisplay = (
      <>
        <i className="bi bi-arrow-clockwise"></i>
        <span>{props.loadingMessage}</span>
      </>
    );
  } else if (props.status === "completed") {
    if (props.error !== null || props.data === null) {
      requestClassName = "error";
      MessageDisplay = (
        <>
          <i className="bi bi-exclamation-circle-fill"></i>
          <span>{props.error}</span>
        </>
      );
    } else if (props.error === null || props.data !== null) {
      requestClassName = "success";
      MessageDisplay = (
        <>
          <i className="bi bi-check-circle-fill"></i>
          <span>{props.successMessage}</span>
        </>
      );
    }
  }
  return (
    <div className={`request-status-message ${requestClassName}`}>
      {MessageDisplay}
    </div>
  );
};

export default RequestStatusMessage;
