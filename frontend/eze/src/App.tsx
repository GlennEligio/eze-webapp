import { useSelector } from "react-redux";
import { Routes, Route, Navigate } from "react-router-dom";
import BorrowForm from "./pages/BorrowForm";
import ReturnForm from "./pages/ReturnForm";
import NotFound from "./pages/NotFound";
import Equipments from "./pages/Equipments";
import LoginLoading from "./pages/LoginLoading";
import Login from "./pages/Login";
import Students from "./pages/Students";
import Accounts from "./pages/Accounts";
import "./App.css";
import { IRootState } from "./store";
import Unauthorized from "./pages/Unauthorized";
import Home from "./pages/Home";
import Professors from "./pages/Professors";
import TransactionHistory from "./pages/TransactionHistory";
import StudentTransactions from "./pages/StudentTransactions";
import StudentBorrowForm from "./pages/StudentBorrowForm";
import AccountSetting from "./pages/AccountSetting";

const App = () => {
  const auth = useSelector((state: IRootState) => state.auth);

  return (
    <div className="vh-100 d-flex flex-column border-top border-info border-5">
      <Routes>
        <Route path="/" element={<Home />} />
        {!!auth.accessToken && (
          <>
            <Route
              path="/accounts"
              element={
                auth.accountType === "ADMIN" ||
                auth.accountType === "SADMIN" ? (
                  <Accounts />
                ) : (
                  <Navigate to="/unauthorized" />
                )
              }
            />
            <Route path="/borrow" element={<BorrowForm />} />
            <Route path="/return" element={<ReturnForm />} />
            <Route path="/equipments" element={<Equipments />} />
            <Route path="/students" element={<Students />} />
            <Route path="/faculty" element={<Professors />} />
            <Route path="/history" element={<TransactionHistory />} />
            <Route path="/student/borrow" element={<StudentBorrowForm />} />
            <Route path="/account/settings" element={<AccountSetting />} />
            <Route
              path="/student/current-transactions"
              element={
                <StudentTransactions
                  historical="false"
                  cancellable={true}
                  returned="false"
                  type="BORROW/RETURN"
                  pageTitle="Current Transactions"
                />
              }
            />
            <Route
              path="/student/history-transactions"
              element={
                <StudentTransactions
                  historical="true"
                  cancellable={false}
                  returned="true"
                  type="HISTORY"
                  pageTitle="Transaction History"
                />
              }
            />
          </>
        )}
        <Route path="/loading" element={<LoginLoading />} />
        <Route path="/login" element={<Login />} />
        <Route path="/unauthorized" element={<Unauthorized />} />
        <Route path="*" element={<NotFound />} />
      </Routes>
    </div>
  );
};

export default App;
