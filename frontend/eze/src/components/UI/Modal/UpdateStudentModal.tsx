import React, { useState, useEffect, FC } from "react";
import useHttp, { RequestConfig } from "../../../hooks/useHttp";
import StudentService, {
  CreateUpdateStudentDto,
  isValidStudent,
  Student,
} from "../../../api/StudentService";
import { useSelector } from "react-redux";
import { IRootState } from "../../../store";
import { studentActions } from "../../../store/studentSlice";
import { useDispatch } from "react-redux";
import { YearLevel } from "../../../api/YearLevelService";
import { YearSection } from "../../../api/YearSectionService";
import useInput, { InputType } from "../../../hooks/useInput";
import {
  validateNotEmpty,
  validatePattern,
  validatePhMobileNumber,
  validatePositive,
} from "../../../validation/validations";

interface UpdateStudentModalProps {
  yearLevels: YearLevel[];
}

const UpdateStudentModal: FC<UpdateStudentModalProps> = (props) => {
  const auth = useSelector((state: IRootState) => state.auth);
  const student = useSelector((state: IRootState) => state.student);
  const dispatch = useDispatch();
  // const [studentNumber, setStudentNumber] = useState("");
  // const [fullName, setFullName] = useState("");
  // const [yearAndSection, setYearAndSection] = useState("BSECE 1-1");
  // const [contactNumber, setContactNumber] = useState("");
  const [birthday, setBirthday] = useState("");
  const [address, setAddress] = useState("");
  const [email, setEmail] = useState("");
  const [guardian, setGuardian] = useState("");
  const [guardianNumber, setGuardianNumber] = useState("");
  // const [yearNumber, setYearNumber] = useState(1);
  const [yearLevels, setYearLevels] = useState<YearLevel[]>([]);
  const [yearSections, setYearSections] = useState<YearSection[]>([]);

  const {
    sendRequest: updateStudent,
    data,
    error,
    status,
  } = useHttp<Student>(StudentService.updateStudent, false);

  // UseInput for controlled input
  // useInput for the controlled input and validation
  const {
    value: studentNumber,
    hasError: studentNumberInputHasError,
    isValid: studentNumberIsValid,
    valueChangeHandler: studentNumberChangeHandler,
    inputBlurHandler: studentNumberBlurHandler,
    reset: resetStudentNumber,
    errorMessage: studentNumberErrorMessage,
    set: setStudentNumber,
  } = useInput(
    validatePattern(
      "Student number",
      /^\d{4}-\d{5}-[(a-z)|(A-Z)]{2}-\d{2}$/,
      "must be a valid PUP Student No."
    ),
    "",
    InputType.TEXT
  );
  const {
    value: fullName,
    hasError: fullNameInputHasError,
    isValid: fullNameIsValid,
    valueChangeHandler: fullNameChangeHandler,
    inputBlurHandler: fullNameBlurHandler,
    reset: resetFullName,
    errorMessage: fullNameErrorMessage,
    set: setFullName,
  } = useInput(validateNotEmpty("Full name"), "", InputType.TEXT);
  const {
    value: yearAndSection,
    hasError: yearAndSectionInputHasError,
    isValid: yearAndSectionIsValid,
    valueChangeHandler: yearAndSectionChangeHandler,
    inputBlurHandler: yearAndSectionBlurHandler,
    reset: resetYearAndSection,
    errorMessage: yearAndSectionErrorMessage,
    set: setYearAndSection,
  } = useInput(validateNotEmpty("Year and section"), "", InputType.SELECT);
  const {
    value: yearNumber,
    hasError: yearNumberInputHasError,
    isValid: yearNumberIsValid,
    valueChangeHandler: yearNumberChangeHandler,
    inputBlurHandler: yearNumberBlurHandler,
    reset: resetYearNumber,
    errorMessage: yearNumberErrorMessage,
    set: setYearNumber,
  } = useInput(validatePositive("Year level"), "", InputType.SELECT);
  const {
    value: contactNumber,
    hasError: contactNumberInputHasError,
    isValid: contactNumberIsValid,
    valueChangeHandler: contactNumberChangeHandler,
    inputBlurHandler: contactNumberBlurHandler,
    reset: resetContactNumber,
    errorMessage: contactNumberErrorMessage,
    set: setContactNumber,
  } = useInput(validatePhMobileNumber("Contact number"), "", InputType.TEXT);

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
      student.selectedStudent.yearLevel
        ? student.selectedStudent.yearLevel.toString()
        : ""
    );
  }, [student.selectedStudent]);

  // Prepopulate the yearLevels state and the value of yearAndSection and yearNumber state
  useEffect(() => {
    if (props.yearLevels && props.yearLevels.length > 0) {
      const sortedYls = [...props.yearLevels].sort(
        (yl1, yl2) => yl1.yearNumber - yl2.yearNumber
      );
      setYearLevels(sortedYls);
    }
  }, [props.yearLevels]);

  // Update yearSections based on yearNumber selected
  useEffect(() => {
    const selectedYearLevel = yearLevels.find(
      (yl) => yl.yearNumber === Number.parseInt(yearNumber)
    );
    if (selectedYearLevel) {
      if (
        selectedYearLevel.yearSections &&
        selectedYearLevel.yearSections.length > 0
      ) {
        setYearSections(selectedYearLevel.yearSections);
        setYearAndSection(selectedYearLevel.yearSections[0].sectionName);
      } else {
        setYearSections([]);
      }
    }
  }, [yearLevels, yearNumber]);

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
        yearNumber: Number.parseInt(yearNumber),
      },
      address,
      birthday,
      email,
      guardian,
      guardianNumber,
    };

    if (!isValidStudent(updatedStudent)) {
      console.log("Invalid student");
      return;
    }

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
              <div className={studentNumberInputHasError ? "invalid" : ""}>
                {studentNumberInputHasError && (
                  <span>{studentNumberErrorMessage}</span>
                )}
                <div className="form-floating mb-3">
                  <input
                    type="text"
                    className="form-control"
                    id="updateStudentSN"
                    onChange={studentNumberChangeHandler}
                    onBlur={studentNumberBlurHandler}
                    value={studentNumber}
                  />
                  <label htmlFor="updateStudentSN">Student Number</label>
                </div>
              </div>
              {/** Full name input */}
              <div className={fullNameInputHasError ? "invalid" : ""}>
                {fullNameInputHasError && <span>{fullNameErrorMessage}</span>}
                <div className="form-floating mb-3">
                  <input
                    type="text"
                    className="form-control"
                    id="updateStudentFullName"
                    onChange={fullNameChangeHandler}
                    onBlur={fullNameBlurHandler}
                    value={fullName}
                  />
                  <label htmlFor="updateStudentFullName">Full name</label>
                </div>
              </div>
              {/** Year Number */}
              <div className={yearNumberInputHasError ? "invalid" : ""}>
                {yearNumberInputHasError && (
                  <span>{yearNumberErrorMessage}</span>
                )}
                <div className="form-floating mb-3">
                  <select
                    className="form-select"
                    id="updateStudentYearNumber"
                    onChange={yearNumberChangeHandler}
                    onBlur={yearNumberBlurHandler}
                    value={yearNumber}
                  >
                    {yearLevels.map((yl) => (
                      <option
                        key={yl.yearName}
                        value={yl.yearNumber.toString()}
                      >
                        {yl.yearName}
                      </option>
                    ))}
                  </select>
                  <label htmlFor="updateStudentYearNumber">Year Level</label>
                </div>
              </div>
              {/** Year and Section input */}
              <div className={yearAndSectionInputHasError ? "invalid" : ""}>
                {yearAndSectionInputHasError && (
                  <span>{yearAndSectionErrorMessage}</span>
                )}
                <div className="form-floating mb-3">
                  <select
                    className="form-select"
                    id="updateStudentYearSection"
                    onChange={yearAndSectionChangeHandler}
                    onBlur={yearAndSectionBlurHandler}
                    value={yearAndSection}
                  >
                    {yearSections &&
                      yearSections.length > 0 &&
                      yearSections.map((ys) => (
                        <option key={ys.sectionName} value={ys.sectionName}>
                          {ys.sectionName}
                        </option>
                      ))}
                  </select>
                  <label htmlFor="updateStudentYearSection">Section</label>
                </div>
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
              <div className={contactNumberInputHasError ? "invalid" : ""}>
                {contactNumberInputHasError && (
                  <span>{contactNumberErrorMessage}</span>
                )}
                <div className="form-floating mb-3">
                  <input
                    type="text"
                    className="form-control"
                    id="updateStudentContactNumber"
                    onChange={contactNumberChangeHandler}
                    onBlur={contactNumberBlurHandler}
                    value={contactNumber}
                  />
                  <label htmlFor="updateStudentContactNumber">
                    Contact Number
                  </label>
                </div>
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
