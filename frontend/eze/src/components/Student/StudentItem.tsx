import React, { FC } from "react";
import { Student } from "../../api/StudentService";
interface StudentItemProps {
  student: Student;
  key: React.Key;
  onStudentRowClick: (student: Student) => void;
  focused: boolean;
}

const StudentItem: FC<StudentItemProps> = (props) => {
  return (
    <tr
      onClick={() => props.onStudentRowClick(props.student)}
      className={`${props.focused && "table-active"}`}
    >
      <td>{props.student.studentNumber}</td>
      <td>{props.student.fullName}</td>
      <td>{props.student.yearAndSection}</td>
      <td>{props.student.contactNumber}</td>
      <td>{props.student.birthday}</td>
      <td>{props.student.address}</td>
      <td>{props.student.email}</td>
      <td>{props.student.guardian}</td>
      <td>{props.student.guardianNumber}</td>
    </tr>
  );
};

export default StudentItem;
