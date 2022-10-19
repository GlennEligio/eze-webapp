import { MouseEventHandler, useEffect } from "react";
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

const Students = () => {
  const navigate = useNavigate();
  const auth = useSelector((state: IRootState) => state.auth);
  const student = useSelector((state: IRootState) => state.student);
  const dispatch = useDispatch();
  const {
    sendRequest: getStudents,
    data,
    error,
    status,
  } = useHttp<Student[]>(StudentService.getStudents, true);

  const backBtnHandler: MouseEventHandler = () => {
    navigate("/");
  };

  // Call backend api to populate data in the useHttp
  useEffect(() => {
    const requestConf: RequestConfig = {
      headers: {
        Authorization: `Bearer ${auth.accessToken}`,
      },
      relativeUrl: "/api/v1/students",
    };
    getStudents(requestConf);
  }, [auth.accessToken]);

  // Pass the data in useHttp to the Student Context
  useEffect(() => {
    if (status === "completed" && error === null) {
      dispatch(studentActions.addStudents({ students: data }));
    }
  }, [data]);

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
              <div className="flex-grow-1 border-bottom border-info border-3">
                <span className="px-3">All Year Level</span>
              </div>
              <div className="border-bottom border-secondary border-3">
                <span className="px-3">First Year</span>
              </div>
              <div className="border-bottom border-secondary border-3">
                <span className="px-3">Second Year</span>
              </div>
              <div className="border-bottom border-secondary border-3">
                <span className="px-3">Third Year</span>
              </div>
              <div className="border-bottom border-secondary border-3">
                <span className="px-3">Fourth Year</span>
              </div>
              <div className="border-bottom border-secondary border-3">
                <span className="px-3">Fifth Year</span>
              </div>
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
                      <a className="dropdown-item" href="#">
                        Update
                      </a>
                    </li>
                    <li>
                      <a className="dropdown-item" href="#">
                        Delete
                      </a>
                    </li>
                  </ul>
                </div>
                <button className="btn btn-sm btn-primary me-1">
                  <i className="bi bi-person-fill"></i> Year Level
                </button>
                <button className="btn btn-sm btn-primary">
                  <i className="bi bi-person-fill"></i> Year Sections
                </button>
              </div>
            </div>
            <div className="col-4 d-flex align-items-center justify-content-end">
              <form className="w-100">
                <div className="input-group">
                  <input
                    className="form-control"
                    type="text"
                    placeholder="Search Student Number"
                  />
                  <button className="btn btn-outline-secondary" type="submit">
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
                style={{ minWidth: "1300px" }}
              >
                <thead className="table-dark">
                  <tr>
                    <th>Student Number</th>
                    <th>Full Name</th>
                    <th>Year and Section</th>
                    <th>Contact Number</th>
                    <th>Birthday</th>
                    <th>Address</th>
                    <th>Email</th>
                    <th>Guardian</th>
                    <th>Guardian Number</th>
                  </tr>
                </thead>
                <tbody>
                  {student.students !== null &&
                    student.students.length > 0 &&
                    student.students.map((s) => (
                      <StudentItem student={s} key={s.studentNumber} />
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
        <AddStudentModal />
      </div>
    </div>
  );
};

export default Students;
