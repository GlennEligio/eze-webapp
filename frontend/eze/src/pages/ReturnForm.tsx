const ReturnForm = () => {
  return (
    <div className="container-md d-flex flex-column h-100">
      <div className="row">
        <header>
          <div className="pt-5 pb-2">
            <div className="d-flex justify-content-between">
              <div className="my-auto">
                <i className="bi bi-arrow-left-circle fs-1"></i>
              </div>
              <div className="d-flex justify-content-end">
                <div className="d-flex align-items-center">
                  <i className="bi bi-textarea-resize fs-1"></i>
                </div>
                <div className="d-flex flex-column justify-content-center ms-3">
                  <span className="fs-3">Return form</span>
                </div>
              </div>
            </div>
          </div>
        </header>
      </div>
      <div className="row h-80">
        <main className="col-12 d-flex flex-column h-100">
          <div className="row">
            <div className="col-3"></div>
            <div className="col-5 d-flex align-items-center justify-content-center">
              <span className="me-2">Scan borrower's right index finger</span>
              <i className="bi bi-fingerprint fs-3"></i>
            </div>
            <div className="col-4"></div>
          </div>
          <div className="row">
            <div className="col-3 d-flex">
              <img
                className="border border-1 border-dark img-fluid"
                src="./img/icons8_user_filled_100px_2.png"
                alt="Default borrower"
              />
            </div>
            <div className="col-5">
              <form>
                <div>
                  <input
                    className="form-control"
                    type="text"
                    placeholder="Borrower's Name"
                  />
                </div>
                <div className="mt-3">
                  <input
                    className="form-control"
                    type="text"
                    placeholder="Student Number"
                  />
                </div>
                <div className="mt-3">
                  <input
                    className="form-control"
                    type="text"
                    placeholder="Year and Section"
                  />
                </div>
                <div className="mt-3 row">
                  <div className="col-6">
                    <input
                      className="form-control"
                      type="text"
                      placeholder="Professor's Name"
                    />
                  </div>
                  <div className="col-6">
                    <input
                      className="form-control"
                      type="text"
                      placeholder="Professor's Number"
                    />
                  </div>
                </div>
              </form>
            </div>
            <div className="col-4 d-flex align-items-end">
              <form className="w-100">
                <p className="text-center fs-5">12:30 AM | 12 Oct 2019</p>
                <div className="mt-3">
                  <input
                    className="form-control"
                    type="text"
                    placeholder="Enter barcode"
                  />
                </div>
                <div className="mt-3">
                  <input
                    className="form-control"
                    type="text"
                    placeholder="Equipment"
                  />
                </div>
                <div className="mt-3 row gx-2">
                  <div className="col-12">
                    <button className="btn btn-secondary w-100">
                      Return Equipments
                    </button>
                  </div>
                </div>
              </form>
            </div>
          </div>
          <div className="row mt-4 gx-0 overflow-auto">
            <div className="col-12 table-responsive-xxl">
              <table
                className="table table-hover"
                style={{ minWidth: "1200px" }}
              >
                <thead className="table-dark">
                  <tr>
                    <th>Student Number</th>
                    <th>Borrower</th>
                    <th>Year and Section</th>
                    <th>Equipments</th>
                    <th>Professor</th>
                    <th>Borrowed At</th>
                    <th>Returned At</th>
                    <th>Status</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td>2015-00129-MN-0</td>
                    <td>John Glenn L. Eligio</td>
                    <td>5-3</td>
                    <td>Voltemeter, Tester</td>
                    <td>Sir. Carascal</td>
                    <td>Apr 24 2015</td>
                    <td>Jan 24 2016</td>
                    <td>Accepted</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
          <div className="row py-3 mt-auto">
            <div className="col">
              <h3 className="text-center fs-5">Unreturned equipments: 1</h3>
            </div>
          </div>
        </main>
      </div>
    </div>
  );
};

export default ReturnForm;
