import { useSelector } from "react-redux";
import { useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";
import { MouseEventHandler, useEffect } from "react";
import { IRootState } from "../store";
import { authActions } from "../store/authSlice";
import MenuButton from "../components/UI/MenuButton";
import MenuHeader from "../components/Layout/MenuHeader";
import MenuFooter from "../components/Layout/MenuFooter";
import MenuClock from "../components/Menu/MenuClock";

const StudentMenu = () => {
  const auth = useSelector((state: IRootState) => state.auth);
  const dispatch = useDispatch();
  const navigate = useNavigate();

  useEffect(() => {
    if (!!auth.accessToken && (auth.type === "USER" || auth.type === "ADMIN"))
      return;
    navigate("/unauthorized");
  }, [auth.accessToken, auth.type, navigate]);

  const logout: MouseEventHandler = (event) => {
    dispatch(authActions.removeAuth());
    navigate("/login");
  };

  return (
    <div className="container-md d-flex flex-column h-100">
      {/* <!-- Header --> */}
      <div className="row">
        <MenuHeader name={auth.name} type={auth.type} key={auth.accessToken} />
      </div>
      {/* <!-- Main --> */}
      <div className="row">
        <main className="col-12">
          <div className="mt-2 d-flex flex-column gx-0">
            <div className="row flex-grow-1 gx-2">
              {/* <!-- First column SA-Menu --> */}
              <div className="col-3 d-flex flex-column">
                {/* <!--Faculty--> */}
                <div className="row gx-0 flex-grow-1">
                  <MenuButton
                    backgroundColor="#41d696"
                    destPage="/borrow"
                    imageLoc="/img/resized_add.jpg"
                    title="Add Transaction"
                    key={"Add Transaction"}
                  />
                </div>
                {/* <!-- Inventory --> */}
                <div className="row gx-0 gy-1 flex-grow-1 mt-1">
                  <MenuButton
                    backgroundColor="#f2a114"
                    destPage="/equipments"
                    imageLoc="/img/resized_inventory.jpg"
                    title="Inventory"
                    key={"Inventory"}
                  />
                </div>
                {/* <!-- Schedule --> */}
                <div className="row gx-0 gy-1 flex-grow-1 mt-1">
                  <MenuButton
                    backgroundColor="#662d91"
                    destPage="/schedules"
                    imageLoc="./img/Calendar.png"
                    title="Schedule"
                    key={"Schedule"}
                  />
                </div>
              </div>
              {/* <!-- Second column SA-Menu--> */}
              <div className="col-3 d-flex flex-column">
                {/* <!--Transactions--> */}
                <div className="row gx-0 flex-grow-1">
                  <MenuButton
                    backgroundColor="#48ac3f"
                    destPage="/transactions"
                    imageLoc="/img/Sync Center.png"
                    title="Transactions"
                    key={"Transactions"}
                  />
                </div>
                {/* <!-- Student Database --> */}
                <div className="row gx-0 gy-1 flex-grow-1 mt-1">
                  <MenuButton
                    backgroundColor="#00aeef"
                    destPage="/students"
                    imageLoc="/img/Documents Library.png"
                    title="Student Database"
                    key={"Student Database"}
                  />
                </div>
                {/* <!-- Camera --> */}
                <div className="row gx-0 gy-1 flex-grow-1 mt-1">
                  <MenuButton
                    backgroundColor="#d24826"
                    destPage="/borrow"
                    imageLoc="/img/camera2.png"
                    title="Camera"
                    key={"Camera"}
                  />
                </div>
              </div>
              <div className="col-6 d-flex flex-column">
                <div className="row gx-0 border border-dark border-1">
                  <div className="col">
                    <img
                      style={{ maxWidth: "100%" }}
                      src="/img/twitter_header_photo_1.png"
                      alt="EZE twitter logo"
                    />
                  </div>
                </div>
                <MenuClock />
              </div>
            </div>
          </div>
        </main>
      </div>
      <div className="row">
        <MenuFooter onClick={logout} />
      </div>
    </div>
  );
};

export default StudentMenu;
