import { MouseEventHandler, useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { useDispatch } from "react-redux";
import { IRootState } from "../store";
import { authActions } from "../store/authSlice";
import EzeMenuButton from "../components/Menu/EzeMenuButton";
import MenuHeader from "../components/Menu/MenuHeader";
import MenuClock from "../components/Menu/MenuClock";
import MenuOffcanvas from "../components/Menu/MenuOffcanvas";
import { ControlledMenu, MenuItem, useMenuState } from "@szhsin/react-menu";

function AdminMenu() {
  const auth = useSelector((state: IRootState) => state.auth);
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const [menuProps, toggleMenu] = useMenuState({ transition: true });
  const [anchorPoint, setAnchorPoint] = useState({ x: 0, y: 0 });

  // Checks if accessToken is present
  useEffect(() => {
    if (!!auth.accessToken && ["SADMIN", "ADMIN"].includes(auth.accountType))
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
    <div className="container-lg d-flex flex-column h-100">
      <div className="row">
        <MenuHeader
          name={auth.fullName}
          type={auth.accountType}
          key={auth.accessToken}
          data-bs-toggle="offcanvas"
          data-bs-target="#menuOffCanvas"
          imageUrl={auth.profile}
        />
      </div>
      <div className="row">
        <main className="col-12">
          <div className="mt-2 d-flex flex-column gx-0">
            <div className="row flex-grow-1 gx-2">
              <div className="col-6 d-flex flex-column">
                <div className="row gx-0 flex-grow-1">
                  <EzeMenuButton
                    backgroundColor="#a43ae3"
                    imageLoc="/img/Contacts.png"
                    title="Faculty"
                    key={"Faculty"}
                    destPage="/faculty"
                  />
                </div>
                <div className="row gx-0 gy-1 flex-grow-1 mt-1">
                  <EzeMenuButton
                    backgroundColor="#f2a114"
                    imageLoc="/img/resized_inventory.jpg"
                    title="Inventory"
                    key={"Inventory"}
                    destPage="/equipments"
                    leftSpacer={true}
                  />
                  <EzeMenuButton
                    backgroundColor="#d68a3a"
                    imageLoc="/img/User Accounts.png"
                    title="Accounts"
                    key="User Accounts"
                    destPage="/accounts"
                  />
                </div>
                <div className="row gx-0 gy-1 flex-grow-1 mt-1">
                  <EzeMenuButton
                    backgroundColor="#662d91"
                    imageLoc="/img/Calendar.png"
                    title="Schedule"
                    key="Schedule"
                    destPage="/schedule"
                    leftSpacer={true}
                  />
                  <EzeMenuButton
                    backgroundColor="#d24826"
                    imageLoc="/img/camera2.png"
                    title="Camera"
                    key="Camera"
                    destPage="/camera"
                  />
                </div>
              </div>
              <div className="col-3 d-flex flex-column">
                <div className="row gx-0 flex-grow-1">
                  <EzeMenuButton
                    backgroundColor="#00aeef"
                    imageLoc="/img/Documents Library.png"
                    title="Student Database"
                    key="Student Database"
                    imgMaxHeight="100%"
                    imgMaxWidth="100%"
                    destPage="/students"
                  />
                </div>
                <div className="row gx-0 flex-grow-1 mt-2">
                  <EzeMenuButton
                    onContextMenu={transactionClickHandler}
                    backgroundColor="#48ac3f"
                    imageLoc="/img/Sync Center.png"
                    title="Transactions"
                    key="Transactions"
                    imgMaxHeight="100%"
                    imgMaxWidth="100%"
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
                      <MenuItem onClick={() => navigate("/history")}>
                        History
                      </MenuItem>
                    </ControlledMenu>
                  </EzeMenuButton>
                </div>
              </div>
              <div className="col-3 d-flex flex-column">
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
      <div>
        <MenuOffcanvas onLogoutClick={logout} />
      </div>
    </div>
  );
}

export default AdminMenu;
