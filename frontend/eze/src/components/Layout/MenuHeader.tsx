import { FC } from "react";

interface MenuHeaderProps {
  type: string;
  name: string;
  key: React.Key;
  "data-bs-target": string;
  "data-bs-toggle": string;
}

const MenuHeader: FC<MenuHeaderProps> = (props) => {
  const accountType =
    props.type === "USER" ? "Student Assistant" : "Administrator";

  return (
    <header>
      <div className="border-bottom border-3 border-secondary pt-2 gx-0">
        <div className="d-flex justify-content-between">
          <div className="my-auto">
            <span className="fs-2">{accountType}</span>
          </div>
          <div className="d-flex justify-content-end">
            <div className="d-flex flex-column justify-content-center">
              <span>{props.name}</span>
              <span>{accountType}</span>
            </div>
            <div className="d-flex align-items-center ms-3">
              <a
                href={props["data-bs-target"]}
                data-bs-toggle={props["data-bs-toggle"]}
                role="button"
                className="text-dark"
              >
                <i className="bi bi-person-circle fs-1"></i>
              </a>
            </div>
          </div>
        </div>
      </div>
    </header>
  );
};

export default MenuHeader;
