import React, { FC } from "react";
import { Equipment } from "../../api/EquipmentService";

interface EquipmentItemProps {
  equipment: Equipment;
  key: React.Key;
  onUpdateSelectedEquipment: (equipment: Equipment) => void;
  focused: boolean;
}

const EquipmentItem: FC<EquipmentItemProps> = (props) => {
  const focusedStyle = "table-active";

  return (
    <tr
      onClick={() => props.onUpdateSelectedEquipment(props.equipment)}
      className={`${props.focused && focusedStyle}`}
    >
      <td>{props.equipment.id}</td>
      <td>{props.equipment.equipmentCode}</td>
      <td>{props.equipment.name}</td>
      <td>{props.equipment.status}</td>
      <td>{props.equipment.defectiveSince}</td>
      <td>
        {props.equipment.isDuplicable ? (
          <i className="bi bi-check2"></i>
        ) : (
          <i className="bi bi-x"></i>
        )}
      </td>
      <td>
        {props.equipment.isBorrowed ? (
          <i className="bi bi-check2"></i>
        ) : (
          <i className="bi bi-x"></i>
        )}
      </td>
      <td>{props.equipment.barcode}</td>
    </tr>
  );
};

export default EquipmentItem;
