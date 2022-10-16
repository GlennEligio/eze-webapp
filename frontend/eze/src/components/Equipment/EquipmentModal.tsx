import React, { FC } from "react";
import AddEquipmentForm from "./AddEquipmentForm";

interface EquipmentModalProps {
  action: string;
}

const EquipmentModal: FC<React.PropsWithChildren<EquipmentModalProps>> = (
  props
) => {
  return (
    <div
      className="modal fade"
      id="equipmentModal"
      tabIndex={-1}
      aria-labelledby="equipmentModalLabel"
      aria-hidden="true"
    >
      {props.action === "ADD" && <AddEquipmentForm />}
    </div>
  );
};

export default EquipmentModal;
