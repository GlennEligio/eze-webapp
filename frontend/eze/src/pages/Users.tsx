const Users = () => {
  return (
    <div className="container-md d-flex flex-column h-100">
      {/* <!-- Header --> */}
      <div className="row">
        <header>
          <div className="pt-5 pb-2">
            <div className="d-flex justify-content-between">
              <div className="my-auto">
                <i className="bi bi-arrow-left-circle fs-1"></i>
              </div>
              <div className="d-flex justify-content-end">
                <div className="d-flex align-items-center">
                  <i className="bi bi-people-fill fs-1"></i>
                </div>
                <div className="d-flex flex-column justify-content-center ms-3">
                  <span className="fs-3">Registered User</span>
                </div>
              </div>
            </div>
          </div>
        </header>
      </div>
      {/* <!-- Main --> */}
      <div className="row h-80">
        <main className="col-12 d-flex flex-column h-100">
          {/* <!-- User info table --> */}
          <div className="row mt-4 gx-0 overflow-auto">
            <div className="col-12 table-responsive-xxl">
              <table
                className="table table-hover"
                style={{ minWidth: "1200px" }}
              >
                <thead className="table-dark">
                  <tr>
                    <th>Full name</th>
                    <th>Username</th>
                    <th>User type</th>
                    <th>Date and Time Registered</th>
                    <th>Email</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td>John Glenn L. Eligio</td>
                    <td>glenneligio</td>
                    <td>Student Assistant</td>
                    <td>12:39 AM | Apr 24, 2015</td>
                    <td>jglenneligio@yaho.com</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
          {/* <!-- User action options --> */}
          <div className="row py-3 mt-auto">
            <div className="col-2"></div>
            <div className="col d-flex justify-content-center align-items-center">
              <div className="px-4">
                <button className="btn btn-outline-dark">
                  <i className="bi bi-person-plus-fill fs-5"></i>
                  <span>Add</span>
                </button>
              </div>
              <div className="px-4">
                <button className="btn btn-outline-dark">
                  <i className="bi bi-pencil-fill fs-5"></i>
                  <span>Edit</span>
                </button>
              </div>
              <div className="px-4">
                <button className="btn btn-outline-dark">
                  <i className="bi bi-trash-fill fs-5"></i>
                  <span>Delete</span>
                </button>
              </div>
            </div>
            <div className="col-2 d-flex flex-column align-items-end justify-content-center">
              <span>1:49 AM</span>
              <span>12 Oct 2019</span>
            </div>
          </div>
        </main>
      </div>
    </div>
  );
};

export default Users;
