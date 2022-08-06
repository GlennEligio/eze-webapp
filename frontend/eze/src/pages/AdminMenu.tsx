import { useEffect } from "react";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { IRootState } from "../store";
import MenuButton from "../components/UI/MenuButton";

function AdminMenu() {
  const auth = useSelector((state: IRootState) => state.auth);
  const navigate = useNavigate();

  useEffect(() => {
    if (!!auth.accessToken && auth.type === "ADMIN") return;
    navigate("/unauthorized");
  }, [auth.accessToken, auth.type, navigate]);

  return (
    <div className="container-md d-flex flex-column h-100">
      <div className="row">
        <header>
          <div className="border-bottom border-3 border-secondary pt-5 pb-2 gx-0">
            <div className="d-flex justify-content-between">
              <div className="my-auto">
                <span className="fs-2">Administrator Menu</span>
              </div>
              <div className="d-flex justify-content-end">
                <div className="d-flex flex-column justify-content-center">
                  <span>John Glenn Eligio</span>
                  <span>Administrator</span>
                </div>
                <div className="d-flex align-items-center ms-3">
                  <i className="bi bi-person-circle fs-1"></i>
                </div>
              </div>
            </div>
          </div>
        </header>
      </div>
      <div className="row flex-grow-1">
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
                    title="Resize inventory"
                    key={"Resize inventory"}
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
                <div className="row gx-0 flex-grow-1 border border-dark border-1 mt-2">
                  <div className="col d-flex flex-column align-items-center justify-content-center">
                    <p className="fs-4">SATURDAY</p>
                    <p className="text-danger" style={{ fontSize: "10rem" }}>
                      12
                    </p>
                    <p className="fs-4">October, 2019</p>
                    <h1 className="text-danger fs-4">1:19:24 AM</h1>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </main>
      </div>
    </div>
  );
}

export default AdminMenu;
