import { MouseEventHandler, useEffect } from "react";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { useDispatch } from "react-redux";
import { IRootState } from "../store";
import { authActions } from "../store/authSlice";
import MenuButton from "../components/UI/MenuButton";
import MenuHeader from "../components/Layout/MenuHeader";
import MenuFooter from "../components/Layout/MenuFooter";
import MenuClock from "../components/Menu/MenuClock";

function AdminMenu() {
  const auth = useSelector((state: IRootState) => state.auth);
  const dispatch = useDispatch();
  const navigate = useNavigate();

  useEffect(() => {
    if (!!auth.accessToken && ["SADMIN", "ADMIN"].includes(auth.accountType))
      return;
    navigate("/unauthorized");
  }, [auth.accessToken, auth.accountType, navigate]);

  const logout: MouseEventHandler = (event) => {
    dispatch(authActions.removeAuth());
    navigate("/login");
  };

  return (
    <div className="container-md d-flex flex-column h-100">
      <div className="row">
        <MenuHeader
          name={auth.fullName}
          type={auth.accountType}
          key={auth.accessToken}
        />
      </div>
      <div className="row">
        <main className="col-12">
          <div className="mt-2 d-flex flex-column gx-0">
            <div className="row flex-grow-1 gx-2">
              <div className="col-6 d-flex flex-column">
                <div className="row gx-0 flex-grow-1">
                  <MenuButton
                    backgroundColor="#a43ae3"
                    imageLoc="/img/Contacts.png"
                    title="Faculty"
                    key={"Faculty"}
                    destPage="/faculty"
                  />
                </div>
                <div className="row gx-0 gy-1 flex-grow-1 mt-1">
                  <MenuButton
                    backgroundColor="#f2a114"
                    imageLoc="/img/resized_inventory.jpg"
                    title="Inventory"
                    key={"Inventory"}
                    destPage="/equipments"
                    leftSpacer={true}
                  />
                  <MenuButton
                    backgroundColor="#d68a3a"
                    imageLoc="/img/User Accounts.png"
                    title="Accounts"
                    key="User Accounts"
                    destPage="/accounts"
                  />
                </div>
                <div className="row gx-0 gy-1 flex-grow-1 mt-1">
                  <MenuButton
                    backgroundColor="#662d91"
                    imageLoc="/img/Calendar.png"
                    title="Schedule"
                    key="Schedule"
                    destPage="/schedules"
                    leftSpacer={true}
                  />
                  <MenuButton
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
                  <MenuButton
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
                  <MenuButton
                    backgroundColor="#48ac3f"
                    imageLoc="/img/Sync Center.png"
                    title="Transactions"
                    key="Transactions"
                    imgMaxHeight="100%"
                    imgMaxWidth="100%"
                    destPage="/transactions"
                  />
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
      {/* Footer */}
      <div className="row">
        <MenuFooter onClick={logout} />
      </div>
    </div>
  );
}

export default AdminMenu;
