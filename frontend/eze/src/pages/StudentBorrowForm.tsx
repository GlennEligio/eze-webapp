import React, {
  FormEventHandler,
  MouseEventHandler,
  useEffect,
  useState,
} from "react";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { Equipment } from "../api/EquipmentService";
import ProfessorService, { Professor } from "../api/ProfessorService";
import SearchItemResult from "../components/StudentBorrow/SearchItemResult";
import useHttp, { RequestConfig } from "../hooks/useHttp";
import { IRootState } from "../store";

const StudentBorrowForm = () => {
  const auth = useSelector((state: IRootState) => state.auth);
  const navigate = useNavigate();
  const [professorName, setProfessorName] = useState("");
  const [equipmentName, setEquipmentName] = useState("");
  const [professorList, setProfessorList] = useState<Professor[]>([]);
  const [selectedProfessor, setSelectedProfessor] = useState<Professor>();
  const [equipmentList, setEquipmentList] = useState<Equipment[]>([]);
  const [selectedEquipments, setSelectedEquipments] = useState<Equipment[]>([]);
  const {
    data: professors,
    error: getProfessorsError,
    status: getProfessorsStatus,
    sendRequest: getProfessors,
  } = useHttp<Professor[]>(ProfessorService.getProfessors, false);

  useEffect(() => {
    if (
      professors &&
      getProfessorsError !== null &&
      getProfessorsStatus === "completed"
    ) {
      if (professors instanceof Array) {
        setProfessorList(professors);
      }
    }
  }, [professors, , getProfessorsStatus, getProfessorsError]);

  const backBtnClickHandler: MouseEventHandler = (event) => {
    event.preventDefault();
    navigate("/");
  };

  const searchProfessorHandler: FormEventHandler = (event) => {
    event.preventDefault();

    const param = new URLSearchParams({
      name: professorName,
    }).toString();

    const requestConfig: RequestConfig = {
      headers: {
        Authorization: `Bearer ${auth.accessToken}`,
      },
      method: "GET",
      relativeUrl: `/api/v1/transactions/${param}`,
    };

    getProfessors(requestConfig);
  };

  const searchEquipmentHandler: FormEventHandler = (event) => {
    event.preventDefault();
  };

  return (
    <div className="container-lg d-flex flex-column h-100">
      <div className="row">
        <header>
          <div className="pt-1 pb-2">
            <div className="d-flex justify-content-between">
              <div className="my-auto">
                <span>
                  <a role="button" onClick={backBtnClickHandler}>
                    <i className="bi-arrow-left-circle fs-1 back-button"></i>
                  </a>
                </span>
              </div>
              <div className="d-flex justify-content-end">
                <div className="d-flex align-items-center">
                  <i className="bi bi-bag-plus-fill fs-1"></i>
                </div>
                <div className="d-flex flex-column justify-content-center ms-3 flex-grow-1">
                  <span className="fs-3">Borrow Equipments</span>
                </div>
              </div>
            </div>
          </div>
        </header>
      </div>
      <div className="row">
        <main>
          {/* <!-- Borrower Student Number --> */}
          <div className="row">
            <div className="col-6">
              <div className="mb-3">
                <label htmlFor="borrowStudentNumber" className="form-label">
                  Borrower Student Number
                </label>
                <input
                  type="text"
                  className="form-control"
                  id="borrowStudentNumber"
                  value={auth.username}
                  readOnly
                />
              </div>
            </div>
          </div>
          {/* <!-- Professor --> */}
          <form>
            <div className="row">
              <label htmlFor="borrowProfessor" className="form-label">
                Professor
              </label>
            </div>
            <div className="row">
              <div className="col-6">
                <div className="input-group mb-3">
                  <input
                    type="text"
                    className="form-control"
                    id="borrowProfessor"
                    value={professorName}
                    onChange={(e) => setProfessorName(e.target.value)}
                  />
                  <button className="btn btn-secondary" type="submit">
                    <i className="bi bi-search"></i>
                  </button>
                </div>
              </div>
              <div className="col-1 d-flex justify-content-center align-items-start">
                <i className="bi bi-caret-right-fill fs-4"></i>
              </div>
              <div className="col-5">
                <ul
                  className="list-group"
                  style={{ maxHeight: "30vh", overflowY: "auto" }}
                >
                  <li className="list-group-item">
                    <div className="d-flex justify-content-between">
                      <a href="">Professor 1</a>
                      <i className="bi bi-plus-lg"></i>
                    </div>
                  </li>
                </ul>
              </div>
            </div>
          </form>
          {/* <!-- Professor search result --> */}
          <div className="row mb-3">
            <div className="col-6">
              <ul
                className="list-group"
                style={{ maxHeight: "20vh", overflowY: "auto" }}
              >
                <li className="list-group-item">
                  <div className="d-flex justify-content-between">
                    <a href="">Professor no. 1</a>
                    <i className="bi bi-dash-lg"></i>
                  </div>
                </li>
                <SearchItemResult action="ADD" addItem={} />
              </ul>
            </div>
          </div>
          {/* <!-- Equipment borrow form --> */}
          <form>
            <div className="row">
              {/* <!-- Equipment Search box --> */}
              <div className="col-6">
                <div className="mb-3">
                  <label htmlFor="borrowEquipment" className="form-label">
                    Equipment
                  </label>
                  <div className="input-group mb-3">
                    <input
                      type="text"
                      className="form-control"
                      id="borrowEquipment"
                      value={equipmentName}
                      onChange={(e) => setEquipmentName(e.target.value)}
                    />
                    <button className="btn btn-secondary" type="submit">
                      <i className="bi bi-search"></i>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </form>
          {/* <!-- Equipment search result and borrowed list --> */}
          <div className="row">
            {/* <!-- Equipment search result list --> */}
            <div className="col-6">
              <ul
                className="list-group"
                style={{ maxHeight: "20vh", overflowY: "auto" }}
              >
                <li className="list-group-item">
                  <div className="d-flex justify-content-between">
                    <a href="">Equipment no. 1</a>
                    <i className="bi bi-plus-lg"></i>
                  </div>
                </li>
                <li className="list-group-item">
                  <div className="d-flex justify-content-between">
                    <a href="">Equipment no.2</a>
                    <i className="bi bi-plus-lg"></i>
                  </div>
                </li>
                <li className="list-group-item">
                  <div className="d-flex justify-content-between">
                    <a href="">Equipment no. 3</a>
                    <i className="bi bi-plus-lg"></i>
                  </div>
                </li>
                <li className="list-group-item">
                  <div className="d-flex justify-content-between">
                    <a href="">Equipment no. 3</a>
                    <i className="bi bi-plus-lg"></i>
                  </div>
                </li>
                <li className="list-group-item">
                  <div className="d-flex justify-content-between">
                    <a href="">Equipment no. 3</a>
                    <i className="bi bi-plus-lg"></i>
                  </div>
                </li>
                <li className="list-group-item">
                  <div className="d-flex justify-content-between">
                    <a href="">Equipment no. 3</a>
                    <i className="bi bi-plus-lg"></i>
                  </div>
                </li>
                <li className="list-group-item">
                  <div className="d-flex justify-content-between">
                    <a href="">Equipment no. 3</a>
                    <i className="bi bi-plus-lg"></i>
                  </div>
                </li>
                <li className="list-group-item">
                  <div className="d-flex justify-content-between">
                    <a href="">Equipment no. 3</a>
                    <i className="bi bi-plus-lg"></i>
                  </div>
                </li>
              </ul>
            </div>
            <div className="col-1 d-flex justify-content-center align-items-center">
              <i className="bi bi-caret-right-fill fs-4"></i>
            </div>
            {/* <!-- Equipment selected --> */}
            <div className="col-5">
              <ul
                className="list-group"
                style={{ maxHeight: "20vh", overflowY: "auto" }}
              >
                <li className="list-group-item">
                  <div className="d-flex justify-content-between">
                    <a href="">Equipment no. 1</a>
                    <i className="bi bi-dash-lg"></i>
                  </div>
                </li>
                <li className="list-group-item">
                  <div className="d-flex justify-content-between">
                    <a href="">Equipment no. 2</a>
                    <i className="bi bi-dash-lg"></i>
                  </div>
                </li>
                <li className="list-group-item">
                  <div className="d-flex justify-content-between">
                    <a href="">Equipment no. 3</a>
                    <i className="bi bi-dash-lg"></i>
                  </div>
                </li>
                <li className="list-group-item">
                  <div className="d-flex justify-content-between">
                    <a href="">Equipment no. 3</a>
                    <i className="bi bi-dash-lg"></i>
                  </div>
                </li>
                <li className="list-group-item">
                  <div className="d-flex justify-content-between">
                    <a href="">Equipment no. 3</a>
                    <i className="bi bi-dash-lg"></i>
                  </div>
                </li>
                <li className="list-group-item">
                  <div className="d-flex justify-content-between">
                    <a href="">Equipment no. 3</a>
                    <i className="bi bi-dash-lg"></i>
                  </div>
                </li>
                <li className="list-group-item">
                  <div className="d-flex justify-content-between">
                    <a href="">Equipment no. 3</a>
                    <i className="bi bi-dash-lg"></i>
                  </div>
                </li>
                <li className="list-group-item">
                  <div className="d-flex justify-content-between">
                    <a href="">Equipment no. 3</a>
                    <i className="bi bi-dash-lg"></i>
                  </div>
                </li>
                <li className="list-group-item">
                  <div className="d-flex justify-content-between">
                    <a href="">Equipment no. 3</a>
                    <i className="bi bi-dash-lg"></i>
                  </div>
                </li>
                <li className="list-group-item">
                  <div className="d-flex justify-content-between">
                    <a href="">Equipment no. 3</a>
                    <i className="bi bi-dash-lg"></i>
                  </div>
                </li>
              </ul>
            </div>
          </div>
          <hr />
          <div className="row">
            <div className="col d-flex justify-content-end">
              <button className="btn btn-success" type="submit">
                Borrow Equipments
              </button>
            </div>
          </div>
        </main>
      </div>
    </div>
  );
};

export default StudentBorrowForm;
