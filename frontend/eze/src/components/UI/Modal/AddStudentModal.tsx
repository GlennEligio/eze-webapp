import React, { useState, useEffect, FC, useRef } from "react";
import useHttp, { RequestConfig } from "../../../hooks/useHttp";
import StudentService, {
  CreateUpdateStudentDto,
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
  validateUrl,
} from "../../../validation/validations";
import RequestStatusMessage from "../Other/RequestStatusMessage";

interface AddStudentModalProps {
  yearLevels: YearLevel[];
}

const AddStudentModal: FC<AddStudentModalProps> = (props) => {
  const auth = useSelector((state: IRootState) => state.auth);
  const dispatch = useDispatch();
  const modal = useRef<HTMLDivElement | null>(null);
  const [birthday, setBirthday] = useState("");
  const [address, setAddress] = useState("");
  const [email, setEmail] = useState("");
  const [guardian, setGuardian] = useState("");
  const [guardianNumber, setGuardianNumber] = useState("");
  const [yearSections, setYearSections] = useState<YearSection[]>([]);
  const [yearLevels, setYearLevels] = useState<YearLevel[]>([]);
  const {
    sendRequest: createStudent,
    data,
    error,
    status,
    resetHttpState,
  } = useHttp<Student>(StudentService.createStudent, false);

  // useInput for the controlled input and validation
  const {
    value: studentNumber,
    hasError: studentNumberInputHasError,
    isValid: studentNumberIsValid,
    valueChangeHandler: studentNumberChangeHandler,
    inputBlurHandler: studentNumberBlurHandler,
    reset: resetStudentNumber,
    errorMessage: studentNumberErrorMessage,
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
  } = useInput(validateNotEmpty("Full name"), "", InputType.TEXT);
  const {
    value: yearAndSection,
    hasError: yearAndSectionInputHasError,
    isValid: yearAndSectionIsValid,
    valueChangeHandler: yearAndSectionChangeHandler,
    inputBlurHandler: yearAndSectionBlurHandler,
    errorMessage: yearAndSectionErrorMessage,
    set: setYearAndSection,
  } = useInput(validateNotEmpty("Year and section"), "", InputType.SELECT);
  const {
    value: yearNumber,
    hasError: yearNumberInputHasError,
    isValid: yearNumberIsValid,
    valueChangeHandler: yearNumberChangeHandler,
    inputBlurHandler: yearNumberBlurHandler,
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
  } = useInput(validatePhMobileNumber("Contact number"), "", InputType.TEXT);
  const {
    value: profile,
    hasError: profileInputHasError,
    isValid: profileIsValid,
    valueChangeHandler: profileChangeHandler,
    inputBlurHandler: profileBlurHandler,
    reset: resetProfileInput,
    errorMessage: profileErrorMessage,
  } = useInput(validateUrl("Profile image url"), "", InputType.TEXT);

  // add hidden.bs.modal eventHandler to Modal at Component Mount
  useEffect(() => {
    if (modal.current) {
      modal.current.addEventListener("hidden.bs.modal", () => {
        resetHttpState();
        resetStudentNumber();
        resetContactNumber();
        resetFullName();
        setAddress("");
        setBirthday("");
        setEmail("");
        setGuardian("");
        setGuardianNumber("");
        resetProfileInput();
      });
    }
  }, []);

  // Add new Student in Context when request is success
  useEffect(() => {
    if (status == "completed" && error === null && data) {
      dispatch(studentActions.addStudent({ newStudent: data }));
    }
  }, [data, status, error]);

  // Prepopulate the yearLevels state and the value of yearAndSection and yearNumber state
  useEffect(() => {
    if (props.yearLevels && props.yearLevels.length > 0) {
      const sortedYls = [...props.yearLevels].sort(
        (yl1, yl2) => yl1.yearNumber - yl2.yearNumber
      );
      setYearLevels(sortedYls);
      setYearNumber(sortedYls[0].yearNumber.toString());
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

  // form submitHandler for adding Student
  const addStudentHandler = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const newStudent: CreateUpdateStudentDto = {
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
      profile,
    };

    if (
      !studentNumberIsValid ||
      !fullNameIsValid ||
      !yearAndSectionIsValid ||
      !contactNumberIsValid ||
      !yearNumberIsValid ||
      !profileIsValid
    ) {
      return;
    }

    const requestConf: RequestConfig = {
      body: newStudent,
      headers: {
        Authorization: `Bearer ${auth.accessToken}`,
        "Content-type": "application/json",
      },
      relativeUrl: "/api/v1/students?complete=false",
    };
    createStudent(requestConf);
    resetStudentNumber();
    resetContactNumber();
    resetFullName();
    resetProfileInput();
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
      ref={modal}
    >
      <div className="modal-dialog modal-dialog-centered modal-dialog-scrollable">
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
            {
              <RequestStatusMessage
                data={data}
                error={error}
                status={status}
                loadingMessage="Adding student..."
                successMessage="Student added"
                key="Add Student"
              />
            }
            <form onSubmit={addStudentHandler}>
              {/** Student Number input */}
              <div className={studentNumberInputHasError ? "invalid" : ""}>
                {studentNumberInputHasError && (
                  <span>{studentNumberErrorMessage}</span>
                )}
                <div className="form-floating mb-3">
                  <input
                    type="text"
                    className="form-control"
                    id="newStudentSN"
                    onChange={studentNumberChangeHandler}
                    onBlur={studentNumberBlurHandler}
                    value={studentNumber}
                  />
                  <label htmlFor="newStudentSN">Student Number</label>
                </div>
              </div>
              {/** Full name input */}
              <div className={fullNameInputHasError ? "invalid" : ""}>
                {fullNameInputHasError && <span>{fullNameErrorMessage}</span>}
                <div className="form-floating mb-3">
                  <input
                    type="text"
                    className="form-control"
                    id="newStudentFullName"
                    onChange={fullNameChangeHandler}
                    onBlur={fullNameBlurHandler}
                    value={fullName}
                  />
                  <label htmlFor="newStudentFullName">Full name</label>
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
                    id="newStudentYearNumber"
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
                  <label htmlFor="newStudentYearNumber">Year Level</label>
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
                    id="newStudentYearSection"
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
                  <label htmlFor="newStudentYearSection">Section</label>
                </div>
              </div>
              {/** Email */}
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
              {/** Contact Number */}
              <div className={contactNumberInputHasError ? "invalid" : ""}>
                {contactNumberInputHasError && (
                  <span>{contactNumberErrorMessage}</span>
                )}
                <div className="form-floating mb-3">
                  <input
                    type="text"
                    className="form-control"
                    id="newStudentContactNumber"
                    onChange={contactNumberChangeHandler}
                    onBlur={contactNumberBlurHandler}
                    value={contactNumber}
                  />
                  <label htmlFor="newStudentContactNumber">
                    Contact Number
                  </label>
                </div>
              </div>
              {/** Profile image url input */}
              <div className={profileInputHasError ? "invalid" : ""}>
                {profileInputHasError && <span>{profileErrorMessage}</span>}
                <div className="form-floating mb-3">
                  <input
                    type="text"
                    className="form-control"
                    id="newStudentProfile"
                    onChange={profileChangeHandler}
                    onBlur={profileBlurHandler}
                    value={profile}
                  />
                  <label htmlFor="newStudentProfile">Profile image url</label>
                </div>
              </div>
              {/** Birthday */}
              <div className="form-floating mb-3">
                <input
                  type="datetime-local"
                  className="form-control"
                  id="newStudentBirthday"
                  onChange={(e) => setBirthday(e.target.value)}
                  value={birthday}
                />
                <label htmlFor="newStudentBirthday">Birthday</label>
              </div>
              {/** Address */}
              <div className="form-floating mb-3">
                <input
                  type="text"
                  className="form-control"
                  id="newStudentAddress"
                  onChange={(e) => setAddress(e.target.value)}
                  value={address}
                />
                <label htmlFor="newStudentAddress">Address</label>
              </div>
              {/** Guardian */}
              <div className="form-floating mb-3">
                <input
                  type="text"
                  className="form-control"
                  id="newStudentGuardian"
                  onChange={(e) => setGuardian(e.target.value)}
                  value={guardian}
                />
                <label htmlFor="newStudentGuardian">Guardian</label>
              </div>
              {/** Guardian Number */}
              <div className="form-floating mb-3">
                <input
                  type="text"
                  className="form-control"
                  id="newStudentGuardianNumber"
                  onChange={(e) => setGuardianNumber(e.target.value)}
                  value={guardianNumber}
                />
                <label htmlFor="newStudentGuardianNumber">
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
