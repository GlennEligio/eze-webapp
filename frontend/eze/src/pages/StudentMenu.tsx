import React from "react";
import { useSelector } from "react-redux";
import StudentMenuOffCanvas from "../components/StudentMenu/StudentMenuOffCanvas";
import { IRootState } from "../store";

const StudentMenu = () => {
  const auth = useSelector((state: IRootState) => state.auth);

  return (
    <>
      <div className="container-lg d-flex flex-column h-100">
        <div className="row">
          <header>
            <div className="pt-1 pb-2">
              <div className="d-flex justify-content-between">
                <div className="my-auto">
                  <span>
                    <a
                      data-bs-toggle="offcanvas"
                      href="#studentOffCanvas"
                      role="button"
                    >
                      <i className="bi bi-list fs-1 me-4 back-button"></i>
                    </a>
                  </span>
                </div>
                <div className="d-flex justify-content-end">
                  <div className="d-flex align-items-center">
                    <i className="bi bi-house-fill fs-1"></i>
                  </div>
                  <div className="d-flex flex-column justify-content-center ms-3 flex-grow-1">
                    <span className="fs-3">Home</span>
                  </div>
                </div>
              </div>
            </div>
          </header>
        </div>
        {/* <!-- Latest current transactions --> */}
        <div className="row">
          <div className="col d-flex justify-content-start ps-3">
            <h5>Latest Current Transactions</h5>
          </div>
        </div>
        <div className="row h-30">
          <main className="col-12 d-flex flex-column h-100">
            <div className="row mt-2 gx-0 overflow-auto">
              <div className="col">
                <div className="table-responsive-xxl">
                  <table
                    className="table table-sm table-hover"
                    style={{ minWidth: "1500px" }}
                  >
                    <thead className="table-dark">
                      <tr>
                        <th>Transaction Code</th>
                        <th>Borrower</th>
                        <th>Year and Section</th>
                        <th>Equipment Count</th>
                        <th>Professor</th>
                        <th>Borrowed At</th>
                        <th>Returned At</th>
                        <th>Status</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr>
                        <td>123123123</td>
                        <td>John Doe</td>
                        <td>BSECE 1-3</td>
                        <td>3</td>
                        <td>Prof John Doe</td>
                        <td>April 24 123123</td>
                        <td>APril 24 2132312</td>
                        <td>ACCEPTED</td>
                      </tr>
                      <tr>
                        <td>123123123</td>
                        <td>John Doe</td>
                        <td>BSECE 1-3</td>
                        <td>3</td>
                        <td>Prof John Doe</td>
                        <td>April 24 123123</td>
                        <td>APril 24 2132312</td>
                        <td>ACCEPTED</td>
                      </tr>
                      <tr>
                        <td>123123123</td>
                        <td>John Doe</td>
                        <td>BSECE 1-3</td>
                        <td>3</td>
                        <td>Prof John Doe</td>
                        <td>April 24 123123</td>
                        <td>APril 24 2132312</td>
                        <td>ACCEPTED</td>
                      </tr>
                      <tr>
                        <td>123123123</td>
                        <td>John Doe</td>
                        <td>BSECE 1-3</td>
                        <td>3</td>
                        <td>Prof John Doe</td>
                        <td>April 24 123123</td>
                        <td>APril 24 2132312</td>
                        <td>ACCEPTED</td>
                      </tr>
                      <tr>
                        <td>123123123</td>
                        <td>John Doe</td>
                        <td>BSECE 1-3</td>
                        <td>3</td>
                        <td>Prof John Doe</td>
                        <td>April 24 123123</td>
                        <td>APril 24 2132312</td>
                        <td>ACCEPTED</td>
                      </tr>
                      <tr>
                        <td>123123123</td>
                        <td>John Doe</td>
                        <td>BSECE 1-3</td>
                        <td>3</td>
                        <td>Prof John Doe</td>
                        <td>April 24 123123</td>
                        <td>APril 24 2132312</td>
                        <td>ACCEPTED</td>
                      </tr>
                      <tr>
                        <td>123123123</td>
                        <td>John Doe</td>
                        <td>BSECE 1-3</td>
                        <td>3</td>
                        <td>Prof John Doe</td>
                        <td>April 24 123123</td>
                        <td>APril 24 2132312</td>
                        <td>ACCEPTED</td>
                      </tr>
                      <tr>
                        <td>123123123</td>
                        <td>John Doe</td>
                        <td>BSECE 1-3</td>
                        <td>3</td>
                        <td>Prof John Doe</td>
                        <td>April 24 123123</td>
                        <td>APril 24 2132312</td>
                        <td>ACCEPTED</td>
                      </tr>
                      <tr>
                        <td>123123123</td>
                        <td>John Doe</td>
                        <td>BSECE 1-3</td>
                        <td>3</td>
                        <td>Prof John Doe</td>
                        <td>April 24 123123</td>
                        <td>APril 24 2132312</td>
                        <td>ACCEPTED</td>
                      </tr>
                      <tr>
                        <td>123123123</td>
                        <td>John Doe</td>
                        <td>BSECE 1-3</td>
                        <td>3</td>
                        <td>Prof John Doe</td>
                        <td>April 24 123123</td>
                        <td>APril 24 2132312</td>
                        <td>ACCEPTED</td>
                      </tr>
                      <tr>
                        <td>123123123</td>
                        <td>John Doe</td>
                        <td>BSECE 1-3</td>
                        <td>3</td>
                        <td>Prof John Doe</td>
                        <td>April 24 123123</td>
                        <td>APril 24 2132312</td>
                        <td>ACCEPTED</td>
                      </tr>
                      <tr>
                        <td>123123123</td>
                        <td>John Doe</td>
                        <td>BSECE 1-3</td>
                        <td>3</td>
                        <td>Prof John Doe</td>
                        <td>April 24 123123</td>
                        <td>APril 24 2132312</td>
                        <td>ACCEPTED</td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          </main>
        </div>
        <br />
        <br />
        {/* <!-- Latests transaction history --> */}
        <div className="row">
          <div className="col d-flex justify-content-start align-items-end ps-3">
            <h5>Latest Transaction History</h5>
          </div>
        </div>
        <div className="row h-30">
          <main className="col-12 d-flex flex-column h-100">
            <div className="row mt-2 gx-0 overflow-auto">
              <div className="col">
                <div className="table-responsive-xxl">
                  <table
                    className="table table-sm table-hover"
                    style={{ minWidth: "1500px" }}
                  >
                    <thead className="table-dark">
                      <tr>
                        <th>Transaction Code</th>
                        <th>Borrower</th>
                        <th>Year and Section</th>
                        <th>Equipment Count</th>
                        <th>Professor</th>
                        <th>Borrowed At</th>
                        <th>Returned At</th>
                        <th>Status</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr>
                        <td>123123123</td>
                        <td>John Doe</td>
                        <td>BSECE 1-3</td>
                        <td>3</td>
                        <td>Prof John Doe</td>
                        <td>April 24 123123</td>
                        <td>APril 24 2132312</td>
                        <td>ACCEPTED</td>
                      </tr>
                      <tr>
                        <td>123123123</td>
                        <td>John Doe</td>
                        <td>BSECE 1-3</td>
                        <td>3</td>
                        <td>Prof John Doe</td>
                        <td>April 24 123123</td>
                        <td>APril 24 2132312</td>
                        <td>ACCEPTED</td>
                      </tr>
                      <tr>
                        <td>123123123</td>
                        <td>John Doe</td>
                        <td>BSECE 1-3</td>
                        <td>3</td>
                        <td>Prof John Doe</td>
                        <td>April 24 123123</td>
                        <td>APril 24 2132312</td>
                        <td>ACCEPTED</td>
                      </tr>
                      <tr>
                        <td>123123123</td>
                        <td>John Doe</td>
                        <td>BSECE 1-3</td>
                        <td>3</td>
                        <td>Prof John Doe</td>
                        <td>April 24 123123</td>
                        <td>APril 24 2132312</td>
                        <td>ACCEPTED</td>
                      </tr>
                      <tr>
                        <td>123123123</td>
                        <td>John Doe</td>
                        <td>BSECE 1-3</td>
                        <td>3</td>
                        <td>Prof John Doe</td>
                        <td>April 24 123123</td>
                        <td>APril 24 2132312</td>
                        <td>ACCEPTED</td>
                      </tr>
                      <tr>
                        <td>123123123</td>
                        <td>John Doe</td>
                        <td>BSECE 1-3</td>
                        <td>3</td>
                        <td>Prof John Doe</td>
                        <td>April 24 123123</td>
                        <td>APril 24 2132312</td>
                        <td>ACCEPTED</td>
                      </tr>
                      <tr>
                        <td>123123123</td>
                        <td>John Doe</td>
                        <td>BSECE 1-3</td>
                        <td>3</td>
                        <td>Prof John Doe</td>
                        <td>April 24 123123</td>
                        <td>APril 24 2132312</td>
                        <td>ACCEPTED</td>
                      </tr>
                      <tr>
                        <td>123123123</td>
                        <td>John Doe</td>
                        <td>BSECE 1-3</td>
                        <td>3</td>
                        <td>Prof John Doe</td>
                        <td>April 24 123123</td>
                        <td>APril 24 2132312</td>
                        <td>ACCEPTED</td>
                      </tr>
                      <tr>
                        <td>123123123</td>
                        <td>John Doe</td>
                        <td>BSECE 1-3</td>
                        <td>3</td>
                        <td>Prof John Doe</td>
                        <td>April 24 123123</td>
                        <td>APril 24 2132312</td>
                        <td>ACCEPTED</td>
                      </tr>
                      <tr>
                        <td>123123123</td>
                        <td>John Doe</td>
                        <td>BSECE 1-3</td>
                        <td>3</td>
                        <td>Prof John Doe</td>
                        <td>April 24 123123</td>
                        <td>APril 24 2132312</td>
                        <td>ACCEPTED</td>
                      </tr>
                      <tr>
                        <td>123123123</td>
                        <td>John Doe</td>
                        <td>BSECE 1-3</td>
                        <td>3</td>
                        <td>Prof John Doe</td>
                        <td>April 24 123123</td>
                        <td>APril 24 2132312</td>
                        <td>ACCEPTED</td>
                      </tr>
                      <tr>
                        <td>123123123</td>
                        <td>John Doe</td>
                        <td>BSECE 1-3</td>
                        <td>3</td>
                        <td>Prof John Doe</td>
                        <td>April 24 123123</td>
                        <td>APril 24 2132312</td>
                        <td>ACCEPTED</td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          </main>
        </div>
      </div>
      <div>
        <StudentMenuOffCanvas
          accountName={auth.fullName}
          accountProfileUrl={auth.profile}
          key={auth.username}
          accountType={auth.accountType === "STUDENT" ? "STUDENT" : "PROFESSOR"}
        />
      </div>
    </>
  );
};

export default StudentMenu;
