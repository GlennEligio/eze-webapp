import { useNavigate } from "react-router-dom";
import { MouseEventHandler, useEffect, useState } from "react";
import StudentService, { StudentFull } from "../api/StudentService";
import ProfessorService, { Professor } from "../api/ProfessorService";
import TransactionService, { Transaction } from "../api/TransactionService";
import EquipmentService, { Equipment } from "../api/EquipmentService";
import { useSelector } from "react-redux";
import { IRootState } from "../store";
import { transactionAction } from "../store/transactionSlice";
import useHttp, { RequestConfig } from "../hooks/useHttp";
import { useDispatch } from "react-redux";
import TransactionItem from "../components/Transaction/TransactionItem";
import ShowTransactionDetails from "../components/UI/Modal/ShowTransactionDetails";
import validator from "validator";

function ReturnForm() {
  // Borrow Form input states
  const [studentNumber, setStudentNumber] = useState("");
  const [professorName, setProfessorName] = useState("");
  const [professorContactNumber, setProfessorContactNumber] = useState("");
  const [equipmentBarcode, setEquipmentBarcode] = useState("");
  const [studentName, setStudentName] = useState("");
  const [profile, setProfile] = useState("");
  const [yearAndSection, setYearAndSection] = useState("");
  const [student, setStudent] = useState<StudentFull>();
  const [professor, setProfessor] = useState<Professor>();
  const [equipments, setEquipments] = useState<Equipment[]>([]);
  const dispatch = useDispatch();
  const transaction = useSelector((state: IRootState) => state.transaction);
  const auth = useSelector((state: IRootState) => state.auth);
  const navigate = useNavigate();

  // useHttp for Transaction list
  const {
    sendRequest: getTransactions,
    data: transactions,
    error: getTransactionsError,
    status: getTransactionsStatus,
  } = useHttp<Transaction[]>(TransactionService.getTransactions, true);

  // useHttp for Student search
  const {
    sendRequest: getStudentByStudentNumber,
    data: studentData,
    error: getStudentByStudentNumberError,
    status: getStudentByStudentNumberStatus,
  } = useHttp<StudentFull>(StudentService.getStudentByStudentNumber, false);

  // useHttp for Professor Search
  const {
    sendRequest: getProfessorByName,
    data: professorData,
    error: getProfessorByNameError,
    status: getProfessorByNameStatus,
  } = useHttp<Professor>(ProfessorService.getProfessorByName, false);

  // useHttp for Equipment Search
  const {
    sendRequest: getEquipmentByBarcode,
    data: equipmentData,
    error: getEquipmentByBarcodeError,
    status: getEquipmentByBarcodeStatus,
  } = useHttp<Equipment>(EquipmentService.getEquipmentByBarcode, false);

  // useHttp for creating Transaction/Borrow
  const {
    sendRequest: returnEquipments,
    data: updatedTransaction,
    error: returnEquipmentsError,
    status: returnEquipmentsStatus,
  } = useHttp<Transaction>(TransactionService.returnEquipments, false);

  // Get transactions on component mount
  useEffect(() => {
    const params = new URLSearchParams({
      returned: "false",
      completed: "false",
      historical: "false",
    }).toString();
    const requestConfig: RequestConfig = {
      headers: {
        Authorization: `Bearer ${auth.accessToken}`,
      },
      relativeUrl: "/api/v1/transactions?" + params,
    };
    getTransactions(requestConfig);
  }, [auth.accessToken]);

  // Populate Transactions in Redux Store
  useEffect(() => {
    if (
      transactions &&
      getTransactionsStatus === "completed" &&
      getTransactionsError === null
    ) {
      dispatch(transactionAction.addTransactions({ transactions }));
    }
  }, [transactions, getTransactionsError, getTransactionsStatus]);

  // Populate Student State and its inputs when data from getStudent useHttp updates
  useEffect(() => {
    if (
      studentData &&
      getStudentByStudentNumberError === null &&
      getStudentByStudentNumberStatus === "completed"
    ) {
      setStudent(studentData);
      setStudentName(studentData.fullName);
      setYearAndSection(studentData.yearAndSection.sectionName);
      if (
        validator.isURL(studentData.profile, { protocols: ["http", "https"] })
      ) {
        fetch(studentData.profile)
          .then((response) => {
            if (
              response.ok &&
              response.headers.get("Content-type")?.startsWith("image")
            ) {
              setProfile(studentData.profile);
            } else {
              setProfile("");
            }
          })
          .catch(() => {
            setProfile("");
          });
      }
    }
  }, [
    studentData,
    getStudentByStudentNumberError,
    getStudentByStudentNumberStatus,
  ]);

  // Populate Professor State and its input
  useEffect(() => {
    if (
      professorData &&
      getProfessorByNameError === null &&
      getProfessorByNameStatus === "completed"
    ) {
      setProfessor(professorData);
      setProfessorContactNumber(professorData.contactNumber);
    }
  }, [professorData, getProfessorByNameError, getProfessorByNameStatus]);

  // Populate Equipment State and its inputs
  useEffect(() => {
    if (
      equipmentData &&
      getEquipmentByBarcodeError === null &&
      getEquipmentByBarcodeStatus === "completed"
    ) {
      setEquipments((oldEqs) => [...oldEqs, equipmentData]);
    }
  }, [equipmentData, getEquipmentByBarcodeError, getEquipmentByBarcodeStatus]);

  // Update or remove the Transaction in the Redux Store
  useEffect(() => {
    if (
      updatedTransaction &&
      returnEquipmentsError === null &&
      returnEquipmentsStatus === "completed"
    ) {
      if (updatedTransaction.equipmentsCount === 0) {
        dispatch(
          transactionAction.removeTransaction({
            txCode: updatedTransaction.txCode,
          })
        );
      } else {
        dispatch(
          transactionAction.updateTransaction({
            transaction: updatedTransaction,
          })
        );
      }
      setEquipments([]);
    }
  }, [updatedTransaction, returnEquipmentsError, returnEquipmentsStatus]);

  // Back button navigation handler
  const backBtnHandler: MouseEventHandler = () => {
    navigate("/");
  };

  // Search student handler
  const searchStudentHandler: MouseEventHandler = () => {
    // Check if student number is empty
    if (validator.isEmpty(studentNumber)) return;
    const params = { complete: "true" };
    const requestConfig: RequestConfig = {
      headers: {
        Authorization: `Bearer ${auth.accessToken}`,
      },
      relativeUrl: `/api/v1/students/${studentNumber}?${new URLSearchParams(
        params
      ).toString()}`,
    };
    getStudentByStudentNumber(requestConfig);
  };

  // Search professor handler
  const searchProfessorHandler: MouseEventHandler = () => {
    // Check if professor input is not empty
    if (validator.isEmpty(professorName)) return;
    const requestConfig: RequestConfig = {
      headers: {
        Authorization: `Bearer ${auth.accessToken}`,
      },
      relativeUrl: `/api/v1/professors/${professorName}`,
    };
    getProfessorByName(requestConfig);
  };

  // Search equipment handler
  const searchEquipmentHandler: MouseEventHandler = () => {
    // Check if barcode input is not empty
    if (validator.isEmpty(equipmentBarcode)) return;
    // Check if the barcode already exist in the equipments list state
    let alreadyExist = false;
    equipments.forEach((e) => {
      if (e.barcode === equipmentBarcode) {
        alreadyExist = true;
      }
    });

    if (alreadyExist) return;

    const requestConfig: RequestConfig = {
      headers: {
        Authorization: `Bearer ${auth.accessToken}`,
      },
      relativeUrl: `/api/v1/equipments/${equipmentBarcode}?query=barcode`,
    };
    getEquipmentByBarcode(requestConfig);
  };

  // Clear Equipments list
  const clearEquipmentsHandler: MouseEventHandler = () => {
    setEquipments([]);
  };

  // Return Equipments handler
  const returnEquipmentHandler = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (student && professor && equipments.length > 0) {
      const params = {
        borrower: student.studentNumber,
        professor: professor.name,
        barcodes: Array.from(new Set<Equipment>(equipments))
          .map((e) => e.barcode)
          .join(","),
      };
      const requestConfig: RequestConfig = {
        headers: {
          Authorization: `Bearer ${auth.accessToken}`,
        },
        relativeUrl: `/api/v1/transactions/return?${new URLSearchParams(
          params
        ).toString()}`,
      };
      returnEquipments(requestConfig);
    }
  };

  // Transaction Item click Handler to update selectedTransaction in Redux Store
  const transactionItemClickHandler = (selectedTransaction: Transaction) => {
    if (
      selectedTransaction.txCode === transaction.selectedTransaction?.txCode
    ) {
      dispatch(
        transactionAction.updateSelectedTransaction({
          selectedTransaction: null,
        })
      );
      return;
    }
    dispatch(
      transactionAction.updateSelectedTransaction({
        selectedTransaction,
      })
    );
  };

  return (
    <div className="container-md d-flex flex-column h-100">
      <div className="row">
        <header>
          <div className="pt-2 pb-2">
            <div className="d-flex justify-content-between">
              <div className="my-auto" onClick={backBtnHandler}>
                <i className="bi bi-arrow-left-circle fs-1"></i>
              </div>
              <div className="d-flex justify-content-end">
                <div className="d-flex align-items-center">
                  <i className="bi bi-textarea-resize fs-1"></i>
                </div>
                <div className="d-flex flex-column justify-content-center ms-3">
                  <span className="fs-3">Return form</span>
                </div>
              </div>
            </div>
          </div>
        </header>
      </div>
      <div className="row h-80">
        <main className="col-12 d-flex flex-column h-100">
          <div className="row">
            <div className="col-3"></div>
            {/* <div className="col-5 d-flex align-items-center justify-content-center">
              <span className="me-2">Scan borrower's right index finger</span>
              <i className="bi bi-fingerprint fs-3"></i>
            </div> */}
            <div className="col-4"></div>
          </div>
          <div className="row">
            <div className="col-3 d-flex">
              <img
                className="border border-1 border-dark img-fluid"
                src={
                  !!profile ? profile : "/img/icons8_user_filled_100px_2.png"
                }
                alt="Default borrower"
              />
            </div>
            <div className="col-5">
              <form>
                <div className="input-group mb-3">
                  <input
                    type="text"
                    className="form-control"
                    placeholder="Student Number"
                    onChange={(e) => setStudentNumber(e.target.value)}
                    value={studentNumber}
                  />
                  <a
                    type="button"
                    className="btn btn-outline-secondary"
                    onClick={searchStudentHandler}
                  >
                    <i className="bi bi-search"></i>
                  </a>
                </div>
                <div className="mt-3">
                  <input
                    className="form-control"
                    type="text"
                    placeholder="Borrower's Name"
                    disabled
                    value={studentName}
                  />
                </div>
                <div className="mt-3">
                  <input
                    className="form-control"
                    type="text"
                    placeholder="Year and Section"
                    disabled
                    value={yearAndSection}
                  />
                </div>
                <div className="mt-3 row">
                  <div className="col-6">
                    <div className="input-group">
                      <input
                        type="text"
                        className="form-control"
                        placeholder="Professor Name"
                        onChange={(e) => setProfessorName(e.target.value)}
                        value={professorName}
                      />
                      <a
                        type="button"
                        className="btn btn-outline-secondary"
                        onClick={searchProfessorHandler}
                      >
                        <i className="bi bi-search"></i>
                      </a>
                    </div>
                  </div>
                  <div className="col-6">
                    <input
                      className="form-control"
                      type="text"
                      placeholder="Professor's Number"
                      value={professorContactNumber}
                      disabled
                    />
                  </div>
                </div>
              </form>
            </div>
            <div className="col-4 d-flex align-items-end">
              <form className="w-100" onSubmit={returnEquipmentHandler}>
                <p className="text-center fs-5">12:30 AM | 12 Oct 2019</p>
                <div className="input-group mb-3">
                  <input
                    type="text"
                    className="form-control"
                    placeholder="Enter barcode"
                    onChange={(e) => setEquipmentBarcode(e.target.value)}
                    value={equipmentBarcode}
                  />
                  <a
                    type="button"
                    className="btn btn-outline-secondary"
                    onClick={searchEquipmentHandler}
                  >
                    <i className="bi bi-search"></i>
                  </a>
                </div>
                <div className="mt-3">
                  <input
                    className="form-control"
                    type="text"
                    placeholder="Equipments..."
                    disabled
                    value={equipments.map((e) => e.name).join(", ")}
                  />
                </div>
                <div className="mt-3 row gx-2">
                  <div className="col-7">
                    <button
                      type="button"
                      className="btn btn-secondary w-100"
                      onClick={clearEquipmentsHandler}
                    >
                      Clear Equipments
                    </button>
                  </div>
                  <div className="col-5">
                    <button type="submit" className="btn btn-secondary w-100">
                      Return
                    </button>
                  </div>
                </div>
              </form>
            </div>
          </div>
          <div className="row mt-4 gx-0 overflow-auto">
            <div className="col-12 table-responsive-xxl">
              <table
                className="table table-hover"
                style={{ minWidth: "1500px" }}
              >
                <thead className="table-dark">
                  <tr>
                    <th>Transaction Code</th>
                    <th>Borrower</th>
                    <th>Year and Section</th>
                    <th>Equipment Count</th>
                    <th>Professor</th>
                    <th>Borrowed At</th>
                    <th>Returned At</th>
                    <th>Status</th>
                  </tr>
                </thead>
                <tbody>
                  {[...transaction.transactions].map((t) => {
                    return (
                      <TransactionItem
                        key={t.txCode}
                        data-bs-toggle="modal"
                        data-bs-target="#transactionDetailsModal"
                        transaction={t}
                        focused={
                          t.txCode === transaction.selectedTransaction?.txCode
                        }
                        onTransactionItemClick={transactionItemClickHandler}
                      />
                    );
                  })}
                </tbody>
              </table>
            </div>
          </div>
        </main>
      </div>
      <div className="row" style={{ marginTop: "auto" }}>
        <div className="col">
          <h3 className="text-center fs-5">
            Borrowed items: {transaction.transactions.length}
          </h3>
        </div>
      </div>
      <div>
        <ShowTransactionDetails
          params={new URLSearchParams({ complete: "true" }).toString()}
          type="BORROW/RETURN"
        />
      </div>
    </div>
  );
}

export default ReturnForm;
