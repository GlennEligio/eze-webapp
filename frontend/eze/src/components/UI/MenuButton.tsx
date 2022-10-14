import { FC } from "react";
import { useNavigate } from "react-router-dom";

interface MenuButtonProps {
  backgroundColor: string;
  imageLoc: string;
  title: string;
  destPage: string;
  leftSpacer?: boolean;
  imgMaxHeight?: string;
  imgMaxWidth?: string;
  key: React.Key;
}

const MenuButton: FC<MenuButtonProps> = (props) => {
  const navigate = useNavigate();

  return (
    <div
      className={`col d-flex flex-column align-items-center ${
        props.leftSpacer ? "me-2" : ""
      }`}
      style={{ backgroundColor: props.backgroundColor }}
      onClick={() => navigate(props.destPage)}
    >
      <img
        style={{
          maxHeight: props.imgMaxHeight || "10rem",
          width: props.imgMaxWidth || "10rem",
        }}
        src={props.imageLoc}
        alt={props.title}
      />
      <span className="fs-4 text-white mt-auto">{props.title}</span>
    </div>
  );
};

export default MenuButton;
