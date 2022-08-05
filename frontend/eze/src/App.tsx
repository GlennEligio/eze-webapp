import { useSelector } from "react-redux";
import { Routes, Route } from "react-router-dom";
import Admin from "./pages/AdminMenu";
import BorrowForm from "./pages/BorrowForm";
import ReturnForm from "./pages/ReturnForm";
import NotFound from "./pages/NotFound";
import Equipments from "./pages/Equipments";
import LoginLoading from "./pages/LoginLoading";
import Login from "./pages/Login";
import StudentMenu from "./pages/StudentMenu";
import Students from "./pages/Students";
import Users from "./pages/Users";
import "./App.css";
import { IRootState } from "./store";

const App = () => {
  const auth = useSelector((state: IRootState) => state.auth);

  return (
    <div className="vh-100 d-flex flex-column border-top border-info border-5">
      <Routes>
        <Route path="/loading" element={<LoginLoading />} />
        <Route path="/login" element={<Login />} />
        {!!auth.accessToken && (
          <>
            <Route path="/admin" element={<Admin />} />
            <Route path="/sa" element={<StudentMenu />} />
            <Route path="/borrow" element={<BorrowForm />} />
            <Route path="/return" element={<ReturnForm />} />
            <Route path="/equipments" element={<Equipments />} />
            <Route path="/students" element={<Students />} />
            <Route path="/users" element={<Users />} />
          </>
        )}

        <Route path="*" element={<NotFound />} />
      </Routes>
    </div>
  );
};

export default App;
