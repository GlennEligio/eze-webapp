function Equipments() {
  return (
    <div className="container-md d-flex flex-column h-100">
      <div className="row">
        <header>
          <div className="pt-5 pb-2">
            <div className="d-flex justify-content-between">
              <div className="my-auto">
                <i className="bi bi-arrow-left-circle fs-1 me-4"></i>
                <i className="bi bi-gear fs-1"></i>
              </div>
              <div className="d-flex justify-content-end">
                <div className="d-flex align-items-center">
                  <i className="bi bi-tools fs-1"></i>
                </div>
                <div className="d-flex flex-column justify-content-center ms-3">
                  <span className="fs-3">Equipment List</span>
                </div>
              </div>
            </div>
          </div>
        </header>
      </div>
      <div className="row h-80">
        <main className="col-12 d-flex flex-column h-100">
          <div className="row mt-2 gx-1">
            <div className="col d-flex align-items-center justify-content-end">
              <div className="d-flex align-items-center px-2">
                <i className="bi bi-arrow-repeat fs-4 me-1"></i>
              </div>
              <div className="d-flex align-items-center px-2 border-start border-dark border-1">
                <i className="bi bi-plus-circle fs-4 me-1"></i>
                <span>Add</span>
              </div>
              <div className="d-flex align-items-center px-2 border-start border-dark border-1">
                <i className="bi bi-pencil fs-4 me-1"></i>
                <span>Edit</span>
              </div>
              <div className="d-flex align-items-center px-2 border-start border-dark border-1">
                <i className="bi bi-trash fs-4 me-1"></i>
                <span>Delete</span>
              </div>
            </div>
          </div>
          <div className="row mt-2 gx-0 overflow-auto">
            <div className="col">
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
          </div>
          <div className="row py-3 mt-auto">
            <div className="col d-flex justify-content-between align-items-center">
              <div>
                <h5>List of Equipments: 6</h5>
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
}

export default Equipments;
