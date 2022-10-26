import { useNavigate } from "react-router-dom";
import { MouseEventHandler, useEffect, useState } from "react";
import { Student } from "../api/StudentService";
import { Professor } from "../api/ProfessorService";
import { Equipment } from "../api/EquipmentService";
import { useSelector } from "react-redux";
import { IRootState } from "../store";
import { transactionAction } from "../store/transactionSlice";
import TransactionService, { Transaction } from "../api/TransactionService";
import useHttp, { RequestConfig } from "../hooks/useHttp";
import { useDispatch } from "react-redux";
import TransactionItem from "../components/Transaction/TransactionItem";

function BorrowForm() {
  const [student, setStudent] = useState<Student>();
  const [professor, setProfessor] = useState<Professor>();
  const [equipments, setEquipments] = useState<Equipment[]>([]);
  const dispatch = useDispatch();
  const transaction = useSelector((state: IRootState) => state.transaction);
  const auth = useSelector((state: IRootState) => state.auth);
  const {
    sendRequest: getTransactions,
    data: transactions,
    error,
    status,
  } = useHttp<Transaction[]>(TransactionService.getTransactions, true);
  const navigate = useNavigate();

  // Get transactions on component mount
  useEffect(() => {
    const requestConfig: RequestConfig = {
      headers: {
        Authorization: `Bearer ${auth.accessToken}`,
      },
      relativeUrl: "/api/v1/transactions",
    };
    getTransactions(requestConfig);
  }, [auth.accessToken]);

  // Populate Transactions in Redux Store
  useEffect(() => {
    if (transactions && status === "completed" && error === null) {
      dispatch(transactionAction.addTransactions({ transactions }));
    }
  }, [transaction, error, status]);

  const backBtnHandler: MouseEventHandler = () => {
    navigate("/");
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
                  <span className="fs-3">Borrow form</span>
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
            <div className="col-5 d-flex align-items-center justify-content-center">
              <span className="me-2">Scan borrower's right index finger</span>
              <i className="bi bi-fingerprint fs-3"></i>
            </div>
            <div className="col-4"></div>
          </div>
          <div className="row">
            <div className="col-3 d-flex">
              <img
                className="border border-1 border-dark img-fluid"
                src="/img/icons8_user_filled_100px_2.png"
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
                  />
                  <a type="button" className="btn btn-outline-secondary">
                    <i className="bi bi-search"></i>
                  </a>
                </div>
                <div className="mt-3">
                  <input
                    className="form-control"
                    type="text"
                    placeholder="Borrower's Name"
                  />
                </div>
                <div className="mt-3">
                  <input
                    className="form-control"
                    type="text"
                    placeholder="Year and Section"
                  />
                </div>
                <div className="mt-3 row">
                  <div className="col-6">
                    <div className="input-group">
                      <input
                        type="text"
                        className="form-control"
                        placeholder="Professor Name"
                      />
                      <a type="button" className="btn btn-outline-secondary">
                        <i className="bi bi-search"></i>
                      </a>
                    </div>
                  </div>
                  <div className="col-6">
                    <input
                      className="form-control"
                      type="text"
                      placeholder="Professor's Number"
                    />
                  </div>
                </div>
              </form>
            </div>
            <div className="col-4 d-flex align-items-end">
              <form className="w-100">
                <p className="text-center fs-5">12:30 AM | 12 Oct 2019</p>
                <div className="input-group mb-3">
                  <input
                    type="text"
                    className="form-control"
                    placeholder="Enter barcode"
                  />
                  <a type="button" className="btn btn-outline-secondary">
                    <i className="bi bi-search"></i>
                  </a>
                </div>
                <div className="mt-3">
                  <input
                    className="form-control"
                    type="text"
                    placeholder="Equipments..."
                  />
                </div>
                <div className="mt-3 row gx-2">
                  <div className="col-7">
                    <button type="button" className="btn btn-secondary w-100">
                      Add Equipment
                    </button>
                  </div>
                  <div className="col-5">
                    <button type="button" className="btn btn-secondary w-100">
                      Borrow
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
                        transaction={t}
                        focused={false}
                        onTransactionItemClick={() => {}}
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
    </div>
  );
}

export default BorrowForm;
