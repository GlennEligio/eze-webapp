import { FC, MouseEventHandler, PropsWithChildren } from "react";
import { useNavigate } from "react-router-dom";

interface EzeMenuButtonProps {
  backgroundColor: string;
  imageLoc: string;
  title: string;
  destPage?: string;
  leftSpacer?: boolean;
  imgMaxHeight?: string;
  imgMaxWidth?: string;
  key: React.Key;
  onContextMenu?: MouseEventHandler;
}

const EzeMenuButton: FC<PropsWithChildren<EzeMenuButtonProps>> = (props) => {
  const navigate = useNavigate();

  return (
    <div
      className={`col d-flex flex-column align-items-center ${
        props.leftSpacer ? "me-2" : ""
      }`}
      style={{ backgroundColor: props.backgroundColor }}
      onClick={props.destPage ? () => navigate(props.destPage!) : () => {}}
      onContextMenu={props.onContextMenu}
    >
      <img
        style={{
          maxHeight: props.imgMaxHeight || "10rem",
          width: props.imgMaxWidth || "10rem",
        }}
        src={props.imageLoc}
        alt={props.title}
      />
      {props.children}
      <span className="fs-4 text-white mt-auto text-center">{props.title}</span>
    </div>
  );
};

export default EzeMenuButton;
