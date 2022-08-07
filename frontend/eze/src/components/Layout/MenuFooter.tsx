import { FC, MouseEventHandler } from "react";

interface MenuFooterProps {
  onClick: MouseEventHandler<HTMLButtonElement>;
}

const MenuFooter: FC<MenuFooterProps> = (props) => {
  return (
    <div className="col d-flex justify-content-end p-3">
      <button className="btn btn-danger btn-sm" onClick={props.onClick}>
        <i className="bi bi-box-arrow-left fs-5 me-2"></i>
        <span className="fs-5">Logout</span>
      </button>
    </div>
  );
};

export default MenuFooter;
