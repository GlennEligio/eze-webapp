import React from "react";
import { Link } from "react-router-dom";

const Unauthorized = () => {
  return (
    <div className="container h-100">
      <div className="row h-100">
        <div className="col d-flex flex-column justify-content-center align-items-center">
          <div className="fs-1 text-center">
            <b>401 - UNAUTHORIZED</b>
          </div>
          <div className="text-center text-wrap mb-2">
            It appears you don't have permission to access this page. Please
            make sure you're authorized to view this content.
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

export default Unauthorized;
