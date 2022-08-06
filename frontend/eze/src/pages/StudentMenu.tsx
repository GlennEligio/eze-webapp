import MenuButton from "../components/UI/MenuButton";
import MenuHeader from "../components/Layout/MenuHeader";
import { useSelector } from "react-redux";
import { IRootState } from "../store";

const StudentMenu = () => {
  const auth = useSelector((state: IRootState) => state.auth);

  return (
    <div className="container-md d-flex flex-column h-100">
      {/* <!-- Header --> */}
      <div className="row">
        <MenuHeader name={auth.name} type={auth.type} key={auth.accessToken} />
      </div>
      {/* <!-- Main --> */}
      <div className="row flex-grow-1">
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
};

export default StudentMenu;
