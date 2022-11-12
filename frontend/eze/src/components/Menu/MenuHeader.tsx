import { FC, useCallback, useEffect, useState } from "react";
import validator from "validator";

interface MenuHeaderProps {
  type: string;
  name: string;
  imageUrl: string;
  key: React.Key;
  "data-bs-target": string;
  "data-bs-toggle": string;
}

const MenuHeader: FC<MenuHeaderProps> = (props) => {
  const [image, setImage] = useState("");

  useEffect(() => {
    // prevents sending request to localhost
    if (!validator.isURL(props.imageUrl, { protocols: ["http", "https"] })) {
      return;
    }

    fetch(props.imageUrl)
      .then((response) => {
        if (
          response.ok &&
          response.headers.get("Content-type")?.startsWith("image")
        ) {
          setImage(props.imageUrl);
        } else {
          setImage("");
        }
      })
      .catch(() => {
        setImage("");
      });
  }, [props.imageUrl]);

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
                {!!image ? (
                  <img
                    src={image}
                    alt="Account profile"
                    width={"45px"}
                    height={"45px"}
                  />
                ) : (
                  <i className="bi bi-person-circle fs-1"></i>
                )}
              </a>
            </div>
          </div>
        </div>
      </div>
    </header>
  );
};

export default MenuHeader;
