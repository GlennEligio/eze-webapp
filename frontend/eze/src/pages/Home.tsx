import { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { Navigate } from "react-router-dom";
import { IRootState } from "../store";
import AdminMenu from "./AdminMenu";
import StudentMenu from "./StudentMenu";

const Home = () => {
  const auth = useSelector((state: IRootState) => state.auth);
  const [homeDisplay, setHomeDisplay] = useState<JSX.Element>(<></>);

  useEffect(() => {
    console.log(auth);
    if (
      !!auth.accessToken &&
      (auth.accountType === "ADMIN" || auth.accountType === "SADMIN")
    ) {
      setHomeDisplay(<AdminMenu />);
    } else if (!!auth.accessToken && auth.accountType === "STUDENT_ASSISTANT") {
      setHomeDisplay(<StudentMenu />);
    } else {
      setHomeDisplay(<Navigate to="/login" />);
    }
  }, [auth]);

  return homeDisplay;
};

export default Home;
