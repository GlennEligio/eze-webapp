import React, { useState, useEffect } from "react";
import useHttp, { RequestConfig } from "../../../hooks/useHttp";
import StudentService from "../../../api/StudentService";
import { useSelector } from "react-redux";
import { IRootState } from "../../../store";
import { studentActions } from "../../../store/studentSlice";
import { useDispatch } from "react-redux";
import validator from "validator";

const DeleteStudentModal = () => {
  const auth = useSelector((state: IRootState) => state.auth);
  const student = useSelector((state: IRootState) => state.student);
  const dispatch = useDispatch();
  const [studentNumber, setStudentNumber] = useState("");
  const [fullName, setFullName] = useState("");
  const [yearAndSection, setYearAndSection] = useState("BSECE 5-1");
  const [contactNumber, setContactNumber] = useState("");
  const [birthday, setBirthday] = useState("");
  const [address, setAddress] = useState("");
  const [email, setEmail] = useState("");
  const [guardian, setGuardian] = useState("");
  const [guardianNumber, setGuardianNumber] = useState("");
  const [yearNumber, setYearNumber] = useState(5);

  const {
    sendRequest: deleteStudent,
    data,
    error,
    status,
  } = useHttp<boolean>(StudentService.deleteStudent, false);

  // Update the Student in the Context API
  useEffect(() => {
    if (status == "completed" && error === null) {
      dispatch(studentActions.removeStudent({ studentNumber: studentNumber }));
    }
  }, [data]);

  // Prepopulate the inputs based on selectedStudent in Context
  useEffect(() => {
    if (student.selectedStudent === null) return;
    setStudentNumber(
      student.selectedStudent.studentNumber
        ? student.selectedStudent.studentNumber
        : ""
    );
    setFullName(
      student.selectedStudent.fullName ? student.selectedStudent.fullName : ""
    );
    setYearAndSection(
      student.selectedStudent.yearAndSection
        ? student.selectedStudent.yearAndSection
        : "BSECE 5-1"
    );
    setContactNumber(
      student.selectedStudent.contactNumber
        ? student.selectedStudent.contactNumber
        : ""
    );
    setBirthday(
      student.selectedStudent.birthday ? student.selectedStudent.birthday : ""
    );
    setAddress(
      student.selectedStudent.address ? student.selectedStudent.address : ""
    );
    setEmail(
      student.selectedStudent.email ? student.selectedStudent.email : ""
    );
    setGuardian(
      student.selectedStudent.guardian ? student.selectedStudent.guardian : ""
    );
    setGuardianNumber(
      student.selectedStudent.guardianNumber
        ? student.selectedStudent.guardianNumber
        : ""
    );
    setYearNumber(
      student.selectedStudent.yearLevel ? student.selectedStudent.yearLevel : 5
    );
  }, [student.selectedStudent]);

  const deleteStudentHandler = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    console.log("Deleting Student");

    if (validator.isEmpty(studentNumber)) return;

    const requestConf: RequestConfig = {
      headers: {
        Authorization: `Bearer ${auth.accessToken}`,
      },
      relativeUrl: `/api/v1/students/${studentNumber}`,
    };
    deleteStudent(requestConf);
  };

  return (
    <div
      className="modal fade"
      id="deleteStudentModal"
      tabIndex={-1}
      aria-labelledby="deleteStudentModalLabel"
      aria-hidden="true"
    >
      <div className="modal-dialog modal-dialog-centered modal-dialog-scrollable">
        <div className="modal-content">
          <div className="modal-header">
            <h1 className="modal-title fs-5" id="studentModalLabel">
              Delete Student
            </h1>
            <button
              type="button"
              className="btn-close"
              data-bs-dismiss="modal"
              aria-label="Close"
            ></button>
          </div>
          <div className="modal-body">
            <form onSubmit={deleteStudentHandler}>
              {/** Student Number input */}
              <div className="form-floating mb-3">
                <input
                  type="text"
                  className="form-control"
                  id="updateStudentSN"
                  value={studentNumber}
                  disabled={true}
                />
                <label htmlFor="updateStudentSN">Student Number</label>
              </div>
              {/** Full name input */}
              <div className="form-floating mb-3">
                <input
                  type="text"
                  className="form-control"
                  id="updateStudentFullName"
                  value={fullName}
                  disabled={true}
                />
                <label htmlFor="updateStudentFullName">Full name</label>
              </div>
              {/** Year and Section input */}
              <div className="form-floating mb-3">
                <select
                  className="form-select"
                  id="updateStudentYearSection"
                  value={yearAndSection}
                  disabled={true}
                >
                  <option value="BSECE 5-1">BSECE 5-1</option>
                  <option value="BSECE 5-1P">BSECE 5-1P</option>
                </select>
                <label htmlFor="updateStudentYearSection">Section</label>
              </div>
              {/** Year Number */}
              <div className="form-floating mb-3">
                <select
                  className="form-select"
                  id="updateStudentYearNumber"
                  value={yearNumber}
                  disabled={true}
                >
                  <option value="5">5</option>
                </select>
                <label htmlFor="updateStudentYearNumber">Year Level</label>
              </div>
              {/** Email */}
              <div className="form-floating mb-3">
                <input
                  type="text"
                  className="form-control"
                  id="updateStudentEmail"
                  value={email}
                  disabled={true}
                />
                <label htmlFor="updateStudentFullname">Email</label>
              </div>
              {/** Contact Number */}
              <div className="form-floating mb-3">
                <input
                  type="text"
                  className="form-control"
                  id="updateStudentContactNumber"
                  value={contactNumber}
                  disabled={true}
                />
                <label htmlFor="updateStudentContactNumber">
                  Contact Number
                </label>
              </div>
              {/** Birthday */}
              <div className="form-floating mb-3">
                <input
                  type="datetime-local"
                  className="form-control"
                  id="updateStudentBirthday"
                  value={birthday}
                  disabled={true}
                />
                <label htmlFor="updateStudentBirthday">Birthday</label>
              </div>
              {/** Address */}
              <div className="form-floating mb-3">
                <input
                  type="text"
                  className="form-control"
                  id="updateStudentAddress"
                  value={address}
                  disabled={true}
                />
                <label htmlFor="updateStudentAddress">Address</label>
              </div>
              {/** Guardian */}
              <div className="form-floating mb-3">
                <input
                  type="text"
                  className="form-control"
                  id="updateStudentGuardian"
                  value={guardian}
                  disabled={true}
                />
                <label htmlFor="updateStudentGuardian">Guardian</label>
              </div>
              {/** Guardian Number */}
              <div className="form-floating mb-3">
                <input
                  type="text"
                  className="form-control"
                  id="updateStudentGuardianNumber"
                  value={guardianNumber}
                  disabled={true}
                />
                <label htmlFor="updateStudentGuardianNumber">
                  Guardian Number
                </label>
              </div>
              <div className="modal-footer">
                <button
                  type="button"
                  className="btn btn-secondary"
                  data-bs-dismiss="modal"
                >
                  Close
                </button>
                <button type="submit" className="btn btn-primary">
                  Delete Student
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default DeleteStudentModal;
