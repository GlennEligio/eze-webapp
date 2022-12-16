import { useSelector } from "react-redux";
import { useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";
import { MouseEventHandler, useEffect, useState } from "react";
import { IRootState } from "../store";
import { authActions } from "../store/authSlice";
import EzeMenuButton from "../components/Menu/EzeMenuButton";
import MenuHeader from "../components/Menu/MenuHeader";
import MenuClock from "../components/Menu/MenuClock";
import MenuOffcanvas from "../components/Menu/MenuOffcanvas";

const ProfessorMenu = () => {
  const auth = useSelector((state: IRootState) => state.auth);
  const dispatch = useDispatch();
  const navigate = useNavigate();

  useEffect(() => {
    if (
      !!auth.accessToken &&
      ["SADMIN", "STUDENT_ASSISTANT", "ADMIN", "PROF"].includes(
        auth.accountType
      )
    )
      return;
    navigate("/unauthorized");
  }, [auth.accessToken, auth.accountType, navigate]);

  const logout = () => {
    dispatch(authActions.removeAuth());
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
              <div className="col-6 d-flex flex-column">
                {/* <!-- Borrow Equipment --> */}
                <div className="row gx-0 flex-grow-1">
                  <EzeMenuButton
                    backgroundColor="#48ac3f"
                    destPage="/professor/current-transactions"
                    imageLoc="/img/Sync Center.png"
                    title="Current Transactions"
                    key={"Professor Current Transactions"}
                    leftSpacer={true}
                  />
                  <EzeMenuButton
                    backgroundColor="#00aeef"
                    destPage="/professor/history-transactions"
                    imageLoc="/img/Documents Library.png"
                    title="Transaction History"
                    key={"Professor Transaction History"}
                  />
                </div>
                {/* <!-- Account Setting --> */}
                <div className="row gx-0 flex-grow-1 mt-2">
                  <EzeMenuButton
                    backgroundColor="#d68a3a"
                    imageLoc="/img/User Accounts.png"
                    title="Account Setting"
                    key="Professor Account Setting"
                    destPage="/account/settings"
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

export default ProfessorMenu;
