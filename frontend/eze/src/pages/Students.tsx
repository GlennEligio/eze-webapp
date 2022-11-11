import { MouseEventHandler, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import StudentItem from "../components/Student/StudentItem";
import MiniClock from "../components/UI/Other/MiniClock";
import useHttp, { RequestConfig } from "../hooks/useHttp";
import StudentService, { Student } from "../api/StudentService";
import { studentActions } from "../store/studentSlice";
import { useDispatch } from "react-redux";
import { useSelector } from "react-redux";
import { IRootState } from "../store";
import AddStudentModal from "../components/UI/Modal/AddStudentModal";
import UpdateStudentModal from "../components/UI/Modal/UpdateStudentModal";
import DeleteStudentModal from "../components/UI/Modal/DeleteStudentModal";
import AddYearLevelModal from "../components/UI/Modal/AddYearLevelModal";
import YearLevelService, { YearLevel } from "../api/YearLevelService";
import { yearLevelAction } from "../store/yearLevelSlice";
import DeleteYearLevelModal from "../components/UI/Modal/DeleteYearLevelModal";
import AddYearSectionModal from "../components/UI/Modal/AddYearSectionModal";
import DeleteYearSectionModal from "../components/UI/Modal/DeleteYearSectionModal";
import ImportExportModal from "../components/UI/Modal/ImportExportModal";
import YearSectionService from "../api/YearSectionService";

const Students = () => {
  const navigate = useNavigate();
  const auth = useSelector((state: IRootState) => state.auth);
  const student = useSelector((state: IRootState) => state.student);
  const yearLevel = useSelector((state: IRootState) => state.yearLevel);
  const [query, setQuery] = useState("");
  const [displayedStudents, setDisplayedStudents] = useState<Student[]>([]);
  const [selectedYearLevel, setSelectedYearLevel] = useState(0);
  const dispatch = useDispatch();
  const {
    sendRequest: getStudents,
    data: students,
    error: stdError,
    status: stdStatus,
  } = useHttp<Student[]>(StudentService.getStudents, true);
  const {
    sendRequest: getYearLevels,
    data: yearLevels,
    error: ylError,
    status: ylStatus,
  } = useHttp<YearLevel[]>(YearLevelService.getYearLevels, true);

  // Call backend api to populate data in the useHttp
  useEffect(() => {
    // Student
    const stdRequestConf: RequestConfig = {
      headers: {
        Authorization: `Bearer ${auth.accessToken}`,
      },
      relativeUrl: "/api/v1/students",
    };
    getStudents(stdRequestConf);

    // YearLevel
    const ylRequestConf: RequestConfig = {
      headers: {
        Authorization: `Bearer ${auth.accessToken}`,
      },
      relativeUrl: "/api/v1/yearLevels",
    };
    getYearLevels(ylRequestConf);
  }, [auth.accessToken]);

  // Pass the students in useHttp to the Student Context
  useEffect(() => {
    if (stdStatus === "completed" && stdError === null) {
      dispatch(studentActions.addStudents({ students: students }));
    }
  }, [students]);

  // Pass the yearLevels in useHttp to the YearLevel Context
  useEffect(() => {
    if (ylStatus === "completed" && ylError === null) {
      dispatch(yearLevelAction.addYearLevels({ yearLevels: yearLevels }));
    }
  }, [yearLevels]);

  // Updates the students shown based on the selectedYearLevel
  useEffect(() => {
    const studentsContext = [...student.students];
    if (selectedYearLevel === 0) {
      setDisplayedStudents(studentsContext);
      return;
    }
    setDisplayedStudents(
      studentsContext.filter((s) => s.yearLevel === selectedYearLevel)
    );
  }, [selectedYearLevel, student.students]);

  // Back btn handler
  const backBtnHandler: MouseEventHandler = () => {
    navigate("/");
  };

  const yearLevelClickHandler = (yearLevel: number) => {
    setSelectedYearLevel(yearLevel);
  };

  // Updates selectedStudent in Context
  const studentRowClickHandler = (selectedStudent: Student) => {
    if (
      student.selectedStudent?.studentNumber === selectedStudent.studentNumber
    ) {
      dispatch(studentActions.updateSelectedStudent({ selectedStudent: null }));
      return;
    }
    dispatch(
      studentActions.updateSelectedStudent({ selectedStudent: selectedStudent })
    );
  };

  return (
    <div className="container-md d-flex flex-column h-100">
      {/* <!-- Header --> */}
      <div className="row">
        <header>
          <div className="pt-5 pb-2">
            <div className="d-flex justify-content-between">
              <div className="my-auto">
                <i
                  className="bi bi-arrow-left-circle fs-1"
                  onClick={backBtnHandler}
                ></i>
                <a
                  href="#importExportStudentModal"
                  data-bs-toggle="modal"
                  className="text-dark ms-3"
                >
                  <i className="bi bi-gear fs-1"></i>
                </a>
              </div>
              <div className="d-flex justify-content-end">
                <div className="d-flex align-items-center">
                  <i className="bi bi-person fs-1"></i>
                </div>
                <div className="d-flex flex-column justify-content-center ms-3">
                  <span className="fs-3">Student Database</span>
                </div>
              </div>
            </div>
          </div>
        </header>
      </div>
      {/* <!-- Main --> */}
      <div className="row h-80">
        <main className="col-12 d-flex flex-column h-100">
          {/* <!-- Year level filter --> */}
          <div className="row">
            <div className="col d-flex align-items-center">
              <div
                className={`flex-grow-1 border-3 border-bottom ${
                  selectedYearLevel === 0 ? "border-info" : "border-secondary"
                }`}
                onClick={() => yearLevelClickHandler(0)}
              >
                <span className="px-3">All Year Level</span>
              </div>
              {yearLevel.yearLevels &&
                yearLevel.yearLevels.length > 0 &&
                [...yearLevel.yearLevels]
                  .sort((yl1, yl2) => yl1.yearNumber - yl2.yearNumber)
                  .map((yl) => {
                    return (
                      <div
                        key={yl.yearNumber}
                        className={`border-bottom border-3 ${
                          selectedYearLevel === yl.yearNumber
                            ? `border-info`
                            : `border-secondary`
                        }`}
                        onClick={() => yearLevelClickHandler(yl.yearNumber)}
                      >
                        <span className="px-3">{yl.yearName} Year</span>
                      </div>
                    );
                  })}
            </div>
          </div>
          {/* <!-- Student Number search bar --> */}
          <div className="row mt-2 gx-1">
            <div className="col d-flex align-items-center justify-content-end">
              <i className="bi bi-arrow-repeat fs-3"></i>
            </div>
            <div className="col d-flex align-items-center">
              <div className="flex-grow-1 d-flex justify-content-center">
                <div className="btn-group dropdown-center me-1">
                  <button
                    type="button"
                    className="btn btn-sm btn-primary dropdown-toggle"
                    data-bs-toggle="dropdown"
                    aria-expanded="false"
                  >
                    Student
                  </button>
                  <ul className="dropdown-menu">
                    {/** Buttons for Student CRUD Modals */}
                    <li>
                      <a
                        className="dropdown-item"
                        href="#addStudentModal"
                        data-bs-toggle="modal"
                      >
                        Add
                      </a>
                    </li>
                    <li>
                      <a
                        className={`dropdown-item ${
                          student.selectedStudent === null && "disabled"
                        }`}
                        href="#updateStudentModal"
                        data-bs-toggle="modal"
                      >
                        Update
                      </a>
                    </li>
                    <li>
                      <a
                        className={`dropdown-item ${
                          student.selectedStudent === null && "disabled"
                        }`}
                        href="#deleteStudentModal"
                        data-bs-toggle="modal"
                      >
                        Delete
                      </a>
                    </li>
                  </ul>
                </div>
                <div className="btn-group dropdown-center me-1">
                  <button
                    type="button"
                    className="btn btn-sm btn-primary dropdown-toggle"
                    data-bs-toggle="dropdown"
                    aria-expanded="false"
                  >
                    Year Level
                  </button>
                  <ul className="dropdown-menu">
                    {/** Buttons for Year Level CRUD Modals */}
                    <li>
                      <a
                        className="dropdown-item"
                        href="#addYearLevelModal"
                        data-bs-toggle="modal"
                      >
                        Add
                      </a>
                    </li>
                    <li>
                      <a
                        className="dropdown-item"
                        href="#deleteYearLevelModal"
                        data-bs-toggle="modal"
                      >
                        Delete
                      </a>
                    </li>
                    <li>
                      <a
                        className="dropdown-item"
                        href="#importExportYearLevelModal"
                        data-bs-toggle="modal"
                      >
                        Download/Upload
                      </a>
                    </li>
                  </ul>
                </div>
                <div className="btn-group dropdown-center me-1">
                  <button
                    type="button"
                    className="btn btn-sm btn-primary dropdown-toggle"
                    data-bs-toggle="dropdown"
                    aria-expanded="false"
                  >
                    Year Section
                  </button>
                  <ul className="dropdown-menu">
                    {/** Buttons for Year Section CRUD Modals */}
                    <li>
                      <a
                        className="dropdown-item"
                        href="#addYearSectionModal"
                        data-bs-toggle="modal"
                      >
                        Add
                      </a>
                    </li>
                    <li>
                      <a
                        className="dropdown-item"
                        href="#deleteYearSectionModal"
                        data-bs-toggle="modal"
                      >
                        Delete
                      </a>
                    </li>
                    <li>
                      <a
                        className="dropdown-item"
                        href="#importExportYearSectionModal"
                        data-bs-toggle="modal"
                      >
                        Download/Upload
                      </a>
                    </li>
                  </ul>
                </div>
              </div>
            </div>
            <div className="col-4 d-flex align-items-center justify-content-end">
              <form className="w-100">
                <div className="input-group">
                  <input
                    className="form-control"
                    type="text"
                    placeholder="Search Student Number"
                    onChange={(e) => setQuery(e.target.value)}
                  />
                  <button
                    className="btn btn-secondary"
                    type="button"
                    disabled={true}
                  >
                    <i className="bi bi-search"></i>
                  </button>
                </div>
              </form>
            </div>
          </div>
          {/* <!-- User info table --> */}
          <div className="row mt-2 gx-0 overflow-auto">
            <div className="table-responsive-xxl">
              <table
                className="table table-hover"
                style={{ minWidth: "1500px" }}
              >
                <thead className="table-dark">
                  <tr>
                    <th>Student Number</th>
                    <th>Full Name</th>
                    <th>Year and Section</th>
                    <th>Contact Number</th>
                    <th>Profile image url</th>
                    <th>Birthday</th>
                    <th>Address</th>
                    <th>Email</th>
                    <th>Guardian</th>
                    <th>Guardian Number</th>
                  </tr>
                </thead>
                <tbody>
                  {displayedStudents !== null &&
                    displayedStudents.length > 0 &&
                    displayedStudents
                      .filter((s) => s.studentNumber.includes(query))
                      .map((s) => (
                        <StudentItem
                          student={s}
                          key={s.studentNumber}
                          onStudentRowClick={studentRowClickHandler}
                          focused={
                            student.selectedStudent?.studentNumber ===
                            s.studentNumber
                          }
                        />
                      ))}
                </tbody>
              </table>
            </div>
          </div>
          {/* <!-- User registered count --> */}
          <div className="row py-3 mt-auto">
            <div className="col d-flex justify-content-between align-items-center">
              <div>
                <h5>Overall registered student: {student.students.length}</h5>
              </div>
              <MiniClock />
            </div>
          </div>
        </main>
      </div>
      <div>
        <AddStudentModal yearLevels={yearLevel.yearLevels} />
        <UpdateStudentModal yearLevels={yearLevel.yearLevels} />
        <DeleteStudentModal />
        <AddYearLevelModal />
        <DeleteYearLevelModal yearLevels={yearLevel.yearLevels} />
        <AddYearSectionModal yearLevels={yearLevel.yearLevels} />
        <DeleteYearSectionModal yearLevels={yearLevel.yearLevels} />
        <ImportExportModal
          itemName="Student"
          downloadFunction={StudentService.download}
          uploadFunction={StudentService.upload}
          jwt={auth.accessToken}
          key="Student"
        />
        <ImportExportModal
          itemName="YearLevel"
          downloadFunction={YearLevelService.download}
          uploadFunction={YearLevelService.upload}
          jwt={auth.accessToken}
          key="YearLevel"
        />
        <ImportExportModal
          itemName="YearSection"
          downloadFunction={YearSectionService.download}
          uploadFunction={YearSectionService.upload}
          jwt={auth.accessToken}
          key="YearSection"
        />
      </div>
    </div>
  );
};

export default Students;
