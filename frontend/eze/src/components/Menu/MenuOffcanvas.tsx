import React, { FC } from "react";
import { Link } from "react-router-dom";

interface MenuOffcanvasProps {
  name?: string;
  accountType?: string;
  image?: string;
  onLogoutClick: () => void;
}

const MenuOffcanvas: FC<MenuOffcanvasProps> = (props) => {
  return (
    <div
      className="offcanvas offcanvas-end"
      tabIndex={-1}
      id="menuOffCanvas"
      aria-labelledby="menuOffCanvasLabel"
    >
      <div className="offcanvas-header">
        <h5 className="offcanvas-title" id="menuOffCanvasLabel">
          Account
        </h5>
        <button
          type="button"
          className="btn-close"
          data-bs-dismiss="offcanvas"
          aria-label="Close"
        ></button>
      </div>
      <div className="offcanvas-body">
        <div>
          <nav className="nav flex-column">
            <a className="nav-link disabled">Account Settings</a>
            <a className="nav-link active">Main Menu</a>
            <Link
              to={"/login"}
              className="nav-link"
              onClick={() => props.onLogoutClick()}
            >
              Logout
            </Link>
          </nav>
        </div>
      </div>
    </div>
  );
};

export default MenuOffcanvas;
