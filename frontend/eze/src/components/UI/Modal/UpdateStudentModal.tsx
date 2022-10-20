import React, { useState, useEffect } from "react";
import useHttp, { RequestConfig } from "../../../hooks/useHttp";
import StudentService, {
  CreateUpdateStudentDto,
  Student,
} from "../../../api/StudentService";
import { useSelector } from "react-redux";
import { IRootState } from "../../../store";
import { studentActions } from "../../../store/studentSlice";
import { useDispatch } from "react-redux";

const UpdateStudentModal = () => {
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
    sendRequest: updateStudent,
    data,
    error,
    status,
  } = useHttp<Student>(StudentService.updateStudent, false);

  // Update the Student in the Context API
  useEffect(() => {
    if (status == "completed" && error === null) {
      dispatch(studentActions.updateStudent({ student: data }));
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
      student.selectedStudent.yearNumber
        ? student.selectedStudent.yearNumber
        : 5
    );
  }, [student.selectedStudent]);

  const updateStudentHandler = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    console.log("Updating Student");
    const updatedStudent: CreateUpdateStudentDto = {
      studentNumber,
      contactNumber,
      fullName,
      yearAndSection: {
        sectionName: yearAndSection,
      },
      yearLevel: {
        yearNumber: yearNumber,
      },
      address,
      birthday,
      email,
      guardian,
      guardianNumber,
    };

    const requestConf: RequestConfig = {
      body: updatedStudent,
      headers: {
        Authorization: `Bearer ${auth.accessToken}`,
        "Content-type": "application/json",
      },
      relativeUrl: `/api/v1/students/${studentNumber}`,
    };
    updateStudent(requestConf);
  };

  return (
    <div
      className="modal fade"
      id="updateStudentModal"
      tabIndex={-1}
      aria-labelledby="updateStudentModalLabel"
      aria-hidden="true"
    >
      <div className="modal-dialog modal-dialog-centered modal-dialog-scrollable">
        <div className="modal-content">
          <div className="modal-header">
            <h1 className="modal-title fs-5" id="studentModalLabel">
              Update Student
            </h1>
            <button
              type="button"
              className="btn-close"
              data-bs-dismiss="modal"
              aria-label="Close"
            ></button>
          </div>
          <div className="modal-body">
            <form onSubmit={updateStudentHandler}>
              {/** Student Number input */}
              <div className="form-floating mb-3">
                <input
                  type="text"
                  className="form-control"
                  id="updateStudentSN"
                  onChange={(e) => setStudentNumber(e.target.value)}
                  value={studentNumber}
                />
                <label htmlFor="updateStudentSN">Student Number</label>
              </div>
              {/** Full name input */}
              <div className="form-floating mb-3">
                <input
                  type="text"
                  className="form-control"
                  id="updateStudentFullName"
                  onChange={(e) => setFullName(e.target.value)}
                  value={fullName}
                />
                <label htmlFor="updateStudentFullName">Full name</label>
              </div>
              {/** Year and Section input */}
              <div className="form-floating mb-3">
                <select
                  className="form-select"
                  id="updateStudentYearSection"
                  onChange={(e) => setYearAndSection(e.currentTarget.value)}
                  value={yearAndSection}
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
                  onChange={(e) =>
                    setYearNumber(Number.parseInt(e.currentTarget.value))
                  }
                  value={yearNumber}
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
                  onChange={(e) => setEmail(e.target.value)}
                  value={email}
                />
                <label htmlFor="updateStudentFullname">Email</label>
              </div>
              {/** Contact Number */}
              <div className="form-floating mb-3">
                <input
                  type="text"
                  className="form-control"
                  id="updateStudentContactNumber"
                  onChange={(e) => setContactNumber(e.target.value)}
                  value={contactNumber}
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
                  onChange={(e) => setBirthday(e.target.value)}
                  value={birthday}
                />
                <label htmlFor="updateStudentBirthday">Birthday</label>
              </div>
              {/** Address */}
              <div className="form-floating mb-3">
                <input
                  type="text"
                  className="form-control"
                  id="updateStudentAddress"
                  onChange={(e) => setAddress(e.target.value)}
                  value={address}
                />
                <label htmlFor="updateStudentAddress">Address</label>
              </div>
              {/** Guardian */}
              <div className="form-floating mb-3">
                <input
                  type="text"
                  className="form-control"
                  id="updateStudentGuardian"
                  onChange={(e) => setGuardian(e.target.value)}
                  value={guardian}
                />
                <label htmlFor="updateStudentGuardian">Guardian</label>
              </div>
              {/** Guardian Number */}
              <div className="form-floating mb-3">
                <input
                  type="text"
                  className="form-control"
                  id="updateStudentGuardianNumber"
                  onChange={(e) => setGuardianNumber(e.target.value)}
                  value={guardianNumber}
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
                  Update Student
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default UpdateStudentModal;
