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

// id: number;
// studentNumber: string;
// fullName: string;
// yearAndSection: string;
// contactNumber: string;
// birthday: string;
// address: string;
// email: string;
// guardian: string;
// guardianNumber: string;
// yearLevel: string;

const AddStudentModal = () => {
  const auth = useSelector((state: IRootState) => state.auth);
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
    sendRequest: createStudent,
    data,
    error,
    status,
  } = useHttp<Student>(StudentService.createStudent, false);

  useEffect(() => {
    if (status == "completed" && error === null) {
      dispatch(studentActions.addStudent({ newStudent: data }));
    }
  }, [data]);

  const onAddStudent = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    console.log("Adding Student");
    const newStudent: CreateUpdateStudentDto = {
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
      body: newStudent,
      headers: {
        Authorization: `Bearer ${auth.accessToken}`,
        "Content-type": "application/json",
      },
      relativeUrl: "/api/v1/students",
    };
    createStudent(requestConf);
    setStudentNumber("");
    setContactNumber("");
    setFullName("");
    setYearAndSection("");
    setYearNumber(5);
    setAddress("");
    setBirthday("");
    setEmail("");
    setGuardian("");
    setGuardianNumber("");
  };

  return (
    <div
      className="modal fade"
      id="addStudentModal"
      tabIndex={-1}
      aria-labelledby="addStudentModalLabel"
      aria-hidden="true"
    >
      <div className="modal-dialog modal-dialog-centered">
        <div className="modal-content">
          <div className="modal-header">
            <h1 className="modal-title fs-5" id="studentModalLabel">
              Add Student
            </h1>
            <button
              type="button"
              className="btn-close"
              data-bs-dismiss="modal"
              aria-label="Close"
            ></button>
          </div>
          <div className="modal-body">
            <form onSubmit={onAddStudent}>
              <div className="form-floating mb-3">
                <input
                  type="text"
                  className="form-control"
                  id="newStudentSN"
                  onChange={(e) => setStudentNumber(e.target.value)}
                  value={studentNumber}
                />
                <label htmlFor="newStudentSN">Student Number</label>
              </div>
              <div className="form-floating mb-3">
                <input
                  type="text"
                  className="form-control"
                  id="newStudentFullName"
                  onChange={(e) => setFullName(e.target.value)}
                  value={fullName}
                />
                <label htmlFor="newStudentFullName">Full name</label>
              </div>
              <div className="form-floating mb-3">
                <select
                  className="form-select"
                  id="newStudentYearSection"
                  onChange={(e) => setYearAndSection(e.currentTarget.value)}
                  value={yearAndSection}
                >
                  <option value="BSECE 5-1">BSECE 5-1</option>
                  <option value="BSECE 5-1P">BSECE 5-1P</option>
                </select>
                <label htmlFor="newStudentYearSection">Section</label>
              </div>
              <div className="form-floating mb-3">
                <select
                  className="form-select"
                  id="newStudentYearNumber"
                  onChange={(e) =>
                    setYearNumber(Number.parseInt(e.currentTarget.value))
                  }
                  value={yearNumber}
                >
                  <option value="5">5</option>
                </select>
                <label htmlFor="newStudentYearNumber">Year Level</label>
              </div>
              <div className="form-floating mb-3">
                <input
                  type="text"
                  className="form-control"
                  id="newStudentEmail"
                  onChange={(e) => setEmail(e.target.value)}
                  value={email}
                />
                <label htmlFor="newStudentFullname">Email</label>
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
                  Add Student
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AddStudentModal;
