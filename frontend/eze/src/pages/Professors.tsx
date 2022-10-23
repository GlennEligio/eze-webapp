import { useNavigate } from "react-router-dom";
import { MouseEventHandler } from "react";

const Professors = () => {
  const navigate = useNavigate();

  const backBtnHandler: MouseEventHandler = () => {
    navigate("/");
  };

  return (
    <div className="container-md d-flex flex-column h-100">
      {/* <!-- Header --> */}
      <div className="row">
        <header>
          <div className="pt-5 pb-2">
            <div className="d-flex justify-content-between">
              <div className="my-auto">
                <i
                  className="bi bi-arrow-left-circle fs-1"
                  onClick={backBtnHandler}
                ></i>
              </div>
              <div className="d-flex justify-content-end">
                <div className="d-flex align-items-center">
                  <i className="bi bi-person fs-1"></i>
                </div>
                <div className="d-flex flex-column justify-content-center ms-3">
                  <span className="fs-3">Professors Database</span>
                </div>
              </div>
            </div>
          </div>
        </header>
      </div>
      {/* <!-- Main --> */}
      <div className="row h-80">
        <main className="col-12 d-flex flex-column h-100">
          {/* <!-- Student Number search bar --> */}
          <div className="row mt-2 gx-1">
            <div className="col d-flex align-items-center justify-content-end">
              <i className="bi bi-arrow-repeat fs-3"></i>
            </div>
            <div className="col-4 d-flex align-items-center justify-content-end">
              <form className="w-100">
                <div className="input-group">
                  <input
                    className="form-control"
                    type="text"
                    placeholder="Search Professor Name"
                  />
                  <button
                    className="btn btn-secondary"
                    type="button"
                    disabled={true}
                  >
                    <i className="bi bi-search"></i>
                  </button>
                </div>
              </form>
            </div>
          </div>
          {/* <!-- User info table --> */}
          <div className="row mt-2 gx-0 overflow-auto">
            <div className="table-responsive-xxl">
              <table
                className="table table-hover"
                style={{ minWidth: "1500px" }}
              >
                <thead className="table-dark">
                  <tr>
                    <th>Student Number</th>
                    <th>Full Name</th>
                    <th>Year and Section</th>
                    <th>Contact Number</th>
                    <th>Birthday</th>
                    <th>Address</th>
                    <th>Email</th>
                    <th>Guardian</th>
                    <th>Guardian Number</th>
                  </tr>
                </thead>
                <tbody></tbody>
              </table>
            </div>
          </div>
          {/* <!-- User registered count --> */}
          <div className="row py-3 mt-auto">
            <div className="col d-flex justify-content-between align-items-center">
              <div>
                <h5>Overall registered professors: 3</h5>
              </div>
            </div>
          </div>
        </main>
      </div>
    </div>
  );
};

export default Professors;
