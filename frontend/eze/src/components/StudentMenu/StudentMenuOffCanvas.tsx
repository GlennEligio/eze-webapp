import React from "react";

interface AccountMenuOffCanvas {
  accountName: string;
  accountType: string;
  accountProfileUrl: string;
}

const StudentMenuOffCanvas: React.FC<AccountMenuOffCanvas> = (props) => {
  return (
    <div>
      <div
        className="offcanvas offcanvas-start"
        tabIndex={-1}
        id="studentOffCanvas"
        aria-labelledby="studentOffCanvasLabel"
      >
        <div className="offcanvas-header">
          <h5 className="offcanvas-title" id="studentOffCanvasLabel">
            {props.accountType}
          </h5>
          <button
            type="button"
            className="btn-close"
            data-bs-dismiss="offcanvas"
            aria-label="Close"
          ></button>
        </div>
        <div className="offcanvas-body">
          <div className="d-flex justify-content-center">
            <img
              src="./img/example student image.jpg"
              alt="Student image"
              className="rounded-circle"
              width="200px"
              height="200px"
            />
          </div>
          <div>
            <p>{props.accountName}</p>
          </div>
          <hr />
          <div>
            <nav className="nav nav-pills flex-column">
              <a className="nav-link active" aria-current="page" href="#">
                <i className="bi bi-house-fill"></i> Home
              </a>
              <a className="nav-link" aria-current="page" href="#">
                <i className="bi bi-bag-plus-fill"></i> Borrow Equipments
              </a>
              <a className="nav-link" href="#">
                <i className="bi bi-clock-history"></i> Current Transactions
              </a>
              <a className="nav-link" href="#">
                <i className="bi bi-file-earmark-check-fill"></i> Transaction
                History
              </a>
              <a className="nav-link" href="#">
                <i className="bi bi-person-circle"></i> Account Setting
              </a>
            </nav>
          </div>
        </div>
      </div>
    </div>
  );
};

export default StudentMenuOffCanvas;
