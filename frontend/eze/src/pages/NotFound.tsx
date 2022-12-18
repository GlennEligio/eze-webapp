import React from "react";
import { Link } from "react-router-dom";

const NotFound = () => {
  return (
    <div className="container h-100">
      <div className="row h-100">
        <div className="col d-flex flex-column justify-content-center align-items-center">
          <div className="fs-1 text-center">
            <b>404 - PAGE NOT FOUND</b>
          </div>
          <div className="text-center text-wrap mb-2">
            The page you are looking for might have been removed, had its name
            changed, or is temporarily unavailable
          </div>
          <div>
            <Link
              className="btn btn-success rounded-pill px-3 shadow-sm"
              to={"/"}
            >
              Go back to homepage
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default NotFound;
