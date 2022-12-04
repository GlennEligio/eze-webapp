import { useSelector } from "react-redux";
import { useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";
import { MouseEventHandler, useEffect, useState } from "react";
import { IRootState } from "../store";
import { authActions } from "../store/authSlice";
import EzeMenuButton from "../components/Menu/EzeMenuButton";
import MenuHeader from "../components/Menu/MenuHeader";
import MenuClock from "../components/Menu/MenuClock";
import { ControlledMenu, MenuItem, useMenuState } from "@szhsin/react-menu";
import MenuOffcanvas from "../components/Menu/MenuOffcanvas";

const StudentAssistantMenu = () => {
  const auth = useSelector((state: IRootState) => state.auth);
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const [menuProps, toggleMenu] = useMenuState({ transition: true });
  const [anchorPoint, setAnchorPoint] = useState({ x: 0, y: 0 });

  useEffect(() => {
    if (
      !!auth.accessToken &&
      ["SADMIN", "STUDENT_ASSISTANT", "ADMIN"].includes(auth.accountType)
    )
      return;
    navigate("/unauthorized");
  }, [auth.accessToken, auth.accountType, navigate]);

  const logout = () => {
    dispatch(authActions.removeAuth());
  };

  const transactionClickHandler: MouseEventHandler = (e) => {
    e.preventDefault();
    setAnchorPoint({ x: e.clientX, y: e.clientY });
    toggleMenu(true);
  };

  return (
    <div className="container-md d-flex flex-column h-100">
      {/* <!-- Header --> */}
      <div className="row">
        <MenuHeader
          name={auth.fullName}
          type={auth.accountType}
          key={auth.accessToken}
          data-bs-target="#menuOffCanvas"
          data-bs-toggle="offcanvas"
          imageUrl={auth.profile}
        />
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
                  <EzeMenuButton
                    onContextMenu={transactionClickHandler}
                    backgroundColor="#41d696"
                    imageLoc="/img/resized_add.jpg"
                    title="Add Transaction"
                    key={"Add Transaction"}
                  >
                    <ControlledMenu
                      {...menuProps}
                      anchorPoint={anchorPoint}
                      direction="right"
                      onClose={() => toggleMenu(false)}
                      menuClassName={"eze-menu"}
                    >
                      <MenuItem onClick={() => navigate("/borrow")}>
                        Borrow
                      </MenuItem>
                      <MenuItem onClick={() => navigate("/return")}>
                        Return
                      </MenuItem>
                    </ControlledMenu>
                  </EzeMenuButton>
                </div>
                {/* <!-- Inventory --> */}
                <div className="row gx-0 gy-1 flex-grow-1 mt-1">
                  <EzeMenuButton
                    backgroundColor="#f2a114"
                    destPage="/equipments"
                    imageLoc="/img/resized_inventory.jpg"
                    title="Inventory"
                    key={"Inventory"}
                  />
                </div>
                {/* <!-- Schedule --> */}
                <div className="row gx-0 gy-1 flex-grow-1 mt-1">
                  <EzeMenuButton
                    backgroundColor="#662d91"
                    destPage="/"
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
                  <EzeMenuButton
                    backgroundColor="#48ac3f"
                    destPage="/history"
                    imageLoc="/img/Sync Center.png"
                    title="Transactions"
                    key={"Transactions"}
                  />
                </div>
                {/* <!-- Student Database --> */}
                <div className="row gx-0 gy-1 flex-grow-1 mt-1">
                  <EzeMenuButton
                    backgroundColor="#00aeef"
                    destPage="/students"
                    imageLoc="/img/Documents Library.png"
                    title="Student Database"
                    key={"Student Database"}
                  />
                </div>
                {/* <!-- Camera --> */}
                <div className="row gx-0 gy-1 flex-grow-1 mt-1">
                  <EzeMenuButton
                    backgroundColor="#d24826"
                    destPage="/"
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
      {/* <div className="row">
        <MenuFooter onClick={logout} />
      </div> */}
      <div>
        <MenuOffcanvas onLogoutClick={logout} />
      </div>
    </div>
  );
};

export default StudentAssistantMenu;
