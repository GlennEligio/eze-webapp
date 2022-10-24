import React from "react";
import { Professor } from "../../api/ProfessorService";

interface ProfessorItemProps {
  professor: Professor;
  key: React.Key;
  onProfItemClick: (professor: Professor) => void;
  focused: boolean;
}

const ProfessorItem: React.FC<ProfessorItemProps> = (props) => {
  return (
    <tr
      onClick={() => props.onProfItemClick(props.professor)}
      className={props.focused ? `table-active` : ""}
    >
      <td>{props.professor.id}</td>
      <td>{props.professor.name}</td>
      <td>{props.professor.contactNumber}</td>
    </tr>
  );
};

export default ProfessorItem;
