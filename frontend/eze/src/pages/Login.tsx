const Login = () => {
  return (
    <div className="container-fluid p-0 flex-grow-1 d-flex flex-column">
      <div className="row gx-0 h-100">
        <main className="h-100">
          <div className="h-100 w-100 d-flex flex-column justify-content-between align-items-stretch">
            <div className="h-50 d-flex justify-content-center align-items-center">
              <img className="img-fluid" src="./img/debut.jpg" alt="Eze logo" />
            </div>
            <div className="bg-dark h-50 row gx-0 w-100">
              <div className="col-4 d-flex flex-column justify-content-center w-100">
                <form>
                  <div className="row gx-0">
                    <div className="col d-flex justify-content-end">
                      <i className="bi bi-person-fill text-white fs-1"></i>
                    </div>
                    <div className="col">
                      <div className="mb-3">
                        <input
                          className="bg-dark text-white border border-secondary border-1 form-control form-control-lg"
                          type="text"
                          name="id"
                          id="username"
                          placeholder="Username"
                        />
                      </div>
                    </div>
                    <div className="col"></div>
                  </div>

                  <div className="row gx-0">
                    <div className="col d-flex justify-content-end">
                      <i className="bi bi-lock-fill text-white fs-1"></i>
                    </div>
                    <div className="col">
                      <div className="mb-3">
                        <input
                          className="bg-dark text-white border border-secondary border-1 form-control form-control-lg"
                          type="text"
                          name="password"
                          id="password"
                          placeholder="Password"
                        />
                      </div>
                    </div>
                    <div className="col"></div>
                  </div>
                  <div className="row gx-0">
                    <div className="col"></div>
                    <div className="col">
                      <div className="w-100">
                        <button className="btn btn-lg btn-info w-100 rounded-5">
                          Login
                        </button>
                      </div>
                    </div>
                    <div className="col"></div>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </main>
      </div>
    </div>
  );
};

export default Login;
