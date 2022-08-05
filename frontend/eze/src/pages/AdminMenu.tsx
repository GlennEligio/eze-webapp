import { useEffect } from "react";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { IRootState } from "../store";

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
                  <div
                    className="col d-flex flex-column align-items-center"
                    style={{ backgroundColor: "#a43ae3" }}
                  >
                    <img
                      style={{ maxHeight: "10rem", maxWidth: "10rem" }}
                      src="/img/Contacts.png"
                      alt="Faculty"
                    />
                    <span className="fs-4 text-white">Faculty</span>
                  </div>
                </div>
                <div className="row gx-0 gy-1 flex-grow-1 mt-1">
                  <div
                    className="col d-flex flex-column align-items-center me-md-2"
                    style={{ backgroundColor: "#f2a114" }}
                  >
                    <img
                      style={{ maxHeight: "10rem", maxWidth: "10rem" }}
                      src="/img/resized_inventory.jpg"
                      alt="Resize inventory"
                    />
                    <span className="fs-4 text-white text-center">
                      Inventory
                    </span>
                  </div>
                  <div
                    className="col d-flex flex-column align-items-center"
                    style={{ backgroundColor: "#d68a3a" }}
                  >
                    <img
                      style={{ maxHeight: "10rem", maxWidth: "10rem" }}
                      src="/img/User Accounts.png"
                      alt="User Accounts"
                    />
                    <span className="fs-4 text-white text-center">Account</span>
                  </div>
                </div>
                <div className="row gx-0 gy-1 flex-grow-1 mt-1">
                  <div
                    className="col d-flex flex-column align-items-center me-md-2"
                    style={{ backgroundColor: "#662d91" }}
                  >
                    <img
                      style={{ maxHeight: "10rem", maxWidth: "10rem" }}
                      src="/img/Calendar.png"
                      alt="Schedule"
                    />
                    <span className="fs-4 text-white text-center">
                      Calendar
                    </span>
                  </div>
                  <div
                    className="col d-flex flex-column align-items-center"
                    style={{ backgroundColor: "#d24826" }}
                  >
                    <img
                      style={{ maxHeight: "10rem", maxWidth: "10rem" }}
                      src="/img/camera2.png"
                      alt="Camera"
                    />
                    <span className="fs-4 text-white text-center">Camera</span>
                  </div>
                </div>
              </div>
              <div className="col-3 d-flex flex-column">
                <div className="row gx-0 flex-grow-1">
                  <div
                    className="col d-flex flex-column align-items-center justify-content-between"
                    style={{ backgroundColor: "#00aeef" }}
                  >
                    <img
                      style={{ maxHeight: "100%", maxWidth: "100%" }}
                      src="/img/Documents Library.png"
                      alt="Student Database"
                    />
                    <span className="fs-4 text-white text-center">
                      Student Database
                    </span>
                  </div>
                </div>
                <div className="row gx-0 flex-grow-1 mt-2">
                  <div
                    className="col d-flex flex-column align-items-center justify-content-between"
                    style={{ backgroundColor: "#48ac3f" }}
                  >
                    <img
                      style={{ maxHeight: "100%", maxWidth: "100%" }}
                      src="/img/Sync Center.png"
                      alt="Transactions"
                    />
                    <span className="fs-4 text-white text-center">
                      Transactions
                    </span>
                  </div>
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
