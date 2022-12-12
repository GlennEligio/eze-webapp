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
  const {
    data: equipments,
    error: getEquipmentsError,
    status: getEquipmentsStatus,
    sendRequest: getEquipments,
  } = useHttp<Equipment[]>(EquipmentService.getEquipments, false);

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
                      retrieveItemDetails={() => {}}
                      modalIdTarget=""
                      key={selectedProfessor.name + "selected"}
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
                style={{ height: "20vh", overflowY: "auto" }}
              >
                {professorList &&
                  professorList.map((p) => {
                    return (
                      <SearchItemResult
                        action="ADD"
                        addItem={() => addProfessorItemHandler(p)}
                        itemName={p.name}
                        removeItem={() => {}}
                        retrieveItemDetails={() => {}}
                        modalIdTarget=""
                        key={p.name}
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
                style={{ height: "20vh", overflowY: "auto" }}
              >
                {equipmentList &&
                  equipmentList.map((eq) => {
                    return (
                      <SearchItemResult
                        action="ADD"
                        addItem={() => addEquipmentItemHandler(eq)}
                        itemName={eq.name}
                        modalIdTarget="equipmentsDetail"
                        removeItem={() => {}}
                        retrieveItemDetails={() => {}}
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
                style={{ height: "20vh", overflowY: "auto" }}
              >
                {selectedEquipments &&
                  selectedEquipments.map((eq) => {
                    return (
                      <SearchItemResult
                        action="REMOVE"
                        addItem={() => {}}
                        itemName={eq.name}
                        modalIdTarget="equipmentsDetail"
                        removeItem={() => removeEquipmentItemHandler(eq)}
                        retrieveItemDetails={() => {}}
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
  );
};

export default StudentBorrowForm;
