import { useSelector } from "react-redux";
import { Routes, Route, Navigate } from "react-router-dom";
import AdminMenu from "./pages/AdminMenu";
import BorrowForm from "./pages/BorrowForm";
import ReturnForm from "./pages/ReturnForm";
import NotFound from "./pages/NotFound";
import Equipments from "./pages/Equipments";
import LoginLoading from "./pages/LoginLoading";
import Login from "./pages/Login";
import StudentMenu from "./pages/StudentMenu";
import Students from "./pages/Students";
import Accounts from "./pages/Accounts";
import "./App.css";
import { IRootState } from "./store";
import Unauthorized from "./pages/Unauthorized";

const App = () => {
  const auth = useSelector((state: IRootState) => state.auth);

  return (
    <div className="vh-100 d-flex flex-column border-top border-info border-5">
      <Routes>
        {!!auth.accessToken && (
          <>
            <Route
              path="/admin"
              element={
                auth.type === "ADMIN" ? (
                  <AdminMenu />
                ) : (
                  <Navigate to="/unauthorized" />
                )
              }
            />
            <Route
              path="/accounts"
              element={
                auth.type === "ADMIN" ? (
                  <Accounts />
                ) : (
                  <Navigate to="/unauthorized" />
                )
              }
            />
            <Route path="/sa" element={<StudentMenu />} />
            <Route path="/borrow" element={<BorrowForm />} />
            <Route path="/return" element={<ReturnForm />} />
            <Route path="/equipments" element={<Equipments />} />
            <Route path="/students" element={<Students />} />
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
