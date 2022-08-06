import MenuButton from "../components/UI/MenuButton";

const StudentMenu = () => {
  return (
    <div className="container-md d-flex flex-column h-100">
      {/* <!-- Header --> */}
      <div className="row">
        <header>
          <div className="border-bottom border-3 border-secondary pt-5 pb-2 gx-0">
            <div className="d-flex justify-content-between">
              <div className="my-auto">
                <span className="fs-2">Student Assistant Menu</span>
              </div>
              <div className="d-flex justify-content-end">
                <div className="d-flex flex-column justify-content-center">
                  <span>John Glenn Eligio</span>
                  <span>Student Assistant</span>
                </div>
                <div className="d-flex align-items-center ms-3">
                  <i className="bi bi-person-circle fs-1"></i>
                </div>
              </div>
            </div>
          </div>
        </header>
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
                    destPage="/borrow"
                    imageLoc="/img/resized_inventory.jpg"
                    title="Inventory"
                    key={"Inventory"}
                  />
                </div>
                {/* <!-- Schedule --> */}
                <div className="row gx-0 gy-1 flex-grow-1 mt-1">
                  <MenuButton
                    backgroundColor="#662d91"
                    destPage="/borrow"
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
                    destPage="/borrow"
                    imageLoc="/img/Sync Center.png"
                    title="Transactions"
                    key={"Transactions"}
                  />
                </div>
                {/* <!-- Student Database --> */}
                <div className="row gx-0 gy-1 flex-grow-1 mt-1">
                  <MenuButton
                    backgroundColor="#00aeef"
                    destPage="/borrow"
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
                    title="Calendar"
                    key={"Calendar"}
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
