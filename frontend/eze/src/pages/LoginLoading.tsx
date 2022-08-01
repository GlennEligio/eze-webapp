const LoginLoading = () => {
  return (
    <div className="container-fluid gx-0 h-100">
      <div className="row d-flex flex-column justify-content-evenly h-100 gx-0">
        <div className="d-flex justify-content-center">
          <img
            className="w-50"
            src="./img/twitter_header_photo_1.png"
            alt="Eze logo"
          />
        </div>
        <div className="d-flex flex-column align-items-center">
          <div
            className="spinner-border text-info fs-3"
            style={{ width: "4rem", height: "4rem" }}
            role="status"
          >
            <span className="visually-hidden">Loading...</span>
          </div>
          <div className="mt-2">
            <span className="fs-4">Loading...</span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LoginLoading;
