import { MouseEventHandler } from "react";
import { useNavigate } from "react-router-dom";

const Students = () => {
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
                  <span className="fs-3">Student Database</span>
                </div>
              </div>
            </div>
          </div>
        </header>
      </div>
      {/* <!-- Main --> */}
      <div className="row h-80">
        <main className="col-12 d-flex flex-column h-100">
          {/* <!-- Year level filter --> */}
          <div className="row">
            <div className="col d-flex align-items-center">
              <div className="flex-grow-1 border-bottom border-info border-3">
                <span className="px-3">All Year Level</span>
              </div>
              <div className="border-bottom border-secondary border-3">
                <span className="px-3">First Year</span>
              </div>
              <div className="border-bottom border-secondary border-3">
                <span className="px-3">Second Year</span>
              </div>
              <div className="border-bottom border-secondary border-3">
                <span className="px-3">Third Year</span>
              </div>
              <div className="border-bottom border-secondary border-3">
                <span className="px-3">Fourth Year</span>
              </div>
              <div className="border-bottom border-secondary border-3">
                <span className="px-3">Fifth Year</span>
              </div>
            </div>
          </div>
          {/* <!-- Student Number search bar --> */}
          <div className="row mt-2 gx-1">
            <div className="col d-flex align-items-center justify-content-end">
              <i className="bi bi-arrow-repeat fs-3"></i>
            </div>
            <div className="col-3 d-flex align-items-center justify-content-end">
              <form className="w-100">
                <div className="input-group">
                  <input
                    className="form-control"
                    type="text"
                    placeholder="Search Student Number"
                  />
                  <button className="btn btn-outline-secondary" type="submit">
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
                style={{ minWidth: "1300px" }}
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
                <tbody>
                  <tr>
                    <td>2015-00129-MN-0</td>
                    <td>John Glenn Eligio</td>
                    <td>BSECE 5-3</td>
                    <td>09062560574</td>
                    <td>January 1, 1996</td>
                    <td>Malabon City</td>
                    <td>johnglenneligio@yahoo.com</td>
                    <td>Jaydee Eligio</td>
                    <td>09560574842</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
          {/* <!-- User registered count --> */}
          <div className="row py-3 mt-auto">
            <div className="col d-flex justify-content-between align-items-center">
              <div>
                <h5>Overall registered student: 6</h5>
              </div>
              <div className="d-flex flex-column justify-content-center align-items-end">
                <span>1:46 AM</span>
                <span>12 Oct 2019</span>
              </div>
            </div>
          </div>
        </main>
      </div>
    </div>
  );
};

export default Students;
