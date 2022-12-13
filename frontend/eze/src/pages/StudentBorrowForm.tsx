import React, {
  FormEventHandler,
  MouseEventHandler,
  useEffect,
  useState,
} from "react";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import EquipmentService, { Equipment } from "../api/EquipmentService";
import ProfessorService, { Professor } from "../api/ProfessorService";
import StudentService, { StudentFull } from "../api/StudentService";
import SearchItemResult from "../components/StudentBorrow/SearchItemResult";
import AddTransaction from "../components/UI/Modal/AddTransaction";
import ShowEquipmentDetails from "../components/UI/Modal/ShowEquipmentDetails";
import ShowProfessorDetails from "../components/UI/Modal/ShowProfessorDetails";
import ShowTransactionDetails from "../components/UI/Modal/ShowTransactionDetails";
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
  const [eqDetailsToShow, setEqDetailsToShow] = useState<Equipment>();
  const [profDetailsToShow, setProfDetailsToShow] = useState<Professor>();
  const [student, setStudent] = useState<StudentFull>();
  const {
    data: professors,
    error: getProfessorsError,
    status: getProfessorsStatus,
    sendRequest: getProfessors,
  } = useHttp<Professor[]>(ProfessorService.getProfessors, false);
  const {
    data: equipments,
    error: getEquipmentsError,
    status: getEquipmentsStatus,
    sendRequest: getEquipments,
  } = useHttp<Equipment[]>(EquipmentService.getEquipments, false);
  const {
    sendRequest: getStudentByStudentNumber,
    data: studentData,
    error: getStudentByStudentNumberError,
    status: getStudentByStudentNumberStatus,
  } = useHttp<StudentFull>(StudentService.getStudentByStudentNumber, true);

  // populate professor search list state based on professor useHttp
  useEffect(() => {
    if (
      professors &&
      getProfessorsError === null &&
      getProfessorsStatus === "completed"
    ) {
      if (professors instanceof Array) {
        setProfessorList(professors);
      }
    }
  }, [professors, , getProfessorsStatus, getProfessorsError]);

  // populate equipment search list state based on equipment useHttp states
  useEffect(() => {
    if (
      equipments &&
      getEquipmentsError === null &&
      getEquipmentsStatus === "completed"
    ) {
      if (equipments instanceof Array) {
        setEquipmentList(equipments);
      }
    }
  }, [equipments, getEquipmentsStatus, getEquipmentsError]);

  // populate student state based on getStudentByStudentNumber based on useHttp state
  useEffect(() => {
    if (
      studentData &&
      getStudentByStudentNumberError === null &&
      getStudentByStudentNumberStatus === "completed"
    ) {
      setStudent(studentData);
    }
  }, [
    studentData,
    getStudentByStudentNumberError,
    getStudentByStudentNumberStatus,
  ]);

  const backBtnClickHandler: MouseEventHandler = (event) => {
    event.preventDefault();
    navigate("/");
  };

  const searchProfessorHandler: FormEventHandler = (event) => {
    event.preventDefault();

    const requestConfig: RequestConfig = {
      headers: {
        Authorization: `Bearer ${auth.accessToken}`,
      },
      method: "GET",
      relativeUrl: `/api/v1/professors?name=${professorName.toString()}`,
    };

    getProfessors(requestConfig);
  };

  const searchEquipmentHandler: FormEventHandler = (event) => {
    event.preventDefault();

    const params = new URLSearchParams({
      isBorrowed: "false",
      query: "name",
      value: equipmentName,
    }).toString();

    const requestConfig: RequestConfig = {
      method: "GET",
      headers: {
        Authorization: `Bearer ${auth.accessToken}`,
      },
      relativeUrl: `/api/v1/equipments?${params}`,
    };

    getEquipments(requestConfig);
  };

  const addEquipmentItemHandler = (newEq: Equipment) => {
    setSelectedEquipments((prevEqList) => {
      const itemDoesNotExist = prevEqList.every(
        (eq) => eq.barcode !== newEq.barcode
      );
      if (!itemDoesNotExist) return prevEqList;
      return [...prevEqList, newEq];
    });
  };

  const removeEquipmentItemHandler = (eqToRemove: Equipment) => {
    console.log("Removing equipment");
    setSelectedEquipments((prevEqList) => {
      return prevEqList.filter(
        (eq) => eq.equipmentCode !== eqToRemove.equipmentCode
      );
    });
  };

  const addProfessorItemHandler = (newProf: Professor) => {
    setSelectedProfessor(newProf);
  };

  const removeProfessorItemHandler = (prof: Professor) => {
    setSelectedProfessor(undefined);
  };

  return (
    <>
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
            <form onSubmit={searchProfessorHandler}>
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
                  <ul className="list-group">
                    {/* Selected Professor */}
                    {selectedProfessor && (
                      <SearchItemResult
                        action="REMOVE"
                        addItem={() => {}}
                        itemName={selectedProfessor.name}
                        removeItem={() =>
                          removeProfessorItemHandler(selectedProfessor)
                        }
                        onClick={() => {
                          setProfDetailsToShow(selectedProfessor);
                        }}
                        modalIdTarget="#showProfessorDetails"
                        key={
                          "Professor " + selectedProfessor.name + " selected"
                        }
                      />
                    )}
                  </ul>
                </div>
              </div>
            </form>
            {/* <!-- Professor search result --> */}
            <div className="row mb-3">
              <div className="col-6">
                <ul
                  className="list-group"
                  style={{ height: "15vh", overflowY: "auto" }}
                >
                  {professorList &&
                    professorList.map((p) => {
                      return (
                        <SearchItemResult
                          action="ADD"
                          addItem={() => addProfessorItemHandler(p)}
                          itemName={p.name}
                          removeItem={() => {}}
                          onClick={() => setProfDetailsToShow(p)}
                          modalIdTarget="#showProfessorDetails"
                          key={"Professor " + p.name + " result"}
                        />
                      );
                    })}
                </ul>
              </div>
            </div>
            {/* <!-- Equipment borrow form --> */}
            <form onSubmit={searchEquipmentHandler}>
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
                  {equipmentList &&
                    equipmentList.map((eq) => {
                      return (
                        <SearchItemResult
                          action="ADD"
                          addItem={() => addEquipmentItemHandler(eq)}
                          itemName={eq.name}
                          modalIdTarget="#showEquipmentDetails"
                          removeItem={() => {}}
                          onClick={() => setEqDetailsToShow(eq)}
                          key={eq.name + "result"}
                        />
                      );
                    })}
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
                  {selectedEquipments &&
                    selectedEquipments.map((eq) => {
                      return (
                        <SearchItemResult
                          action="REMOVE"
                          addItem={() => {}}
                          itemName={eq.name}
                          modalIdTarget="#showEquipmentDetails"
                          removeItem={() => removeEquipmentItemHandler(eq)}
                          onClick={() => setEqDetailsToShow(eq)}
                          key={eq.name + "selected"}
                        />
                      );
                    })}
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
      <div>
        <ShowProfessorDetails selectedProfessor={profDetailsToShow} />
        <ShowEquipmentDetails equipmentToShow={eqDetailsToShow} />
        <AddTransaction student={} />
      </div>
    </>
  );
};

export default StudentBorrowForm;
