import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { MouseEventHandler } from "react";
import useHttp, { RequestConfig } from "../hooks/useHttp";
import { Professor } from "../api/ProfessorService";
import ProfessorService from "../api/ProfessorService";
import { useDispatch } from "react-redux";
import { useSelector } from "react-redux";
import { IRootState } from "../store";
import { professorActions } from "../store/professorSlice";
import ProfessorItem from "../components/Professor/ProfessorItem";
import AddProfessorModal from "../components/UI/Modal/AddProfessorModal";
import DeleteProfessorModal from "../components/UI/Modal/DeleteProfessorModal";
import UpdateProfessorModal from "../components/UI/Modal/UpdateProfessorModal";

const Professors = () => {
  const professor = useSelector((state: IRootState) => state.professor);
  const auth = useSelector((state: IRootState) => state.auth);
  const dispatch = useDispatch();
  const {
    sendRequest: getProfessors,
    data,
    error,
    status,
  } = useHttp<Professor[]>(ProfessorService.getProfessors, true);
  const navigate = useNavigate();

  // Fetch data in the backend
  useEffect(() => {
    const requestConfig: RequestConfig = {
      headers: {
        Authorization: `Bearer ${auth.accessToken}`,
      },
      relativeUrl: "/api/v1/professors",
    };
    getProfessors(requestConfig);
  }, [auth.accessToken, getProfessors]);

  // Prepopulate the Professor in Redux using useHttp data
  useEffect(() => {
    if (status === "completed" && error === null && data && data.length > 0) {
      dispatch(professorActions.addProfessors({ professors: data }));
    }
  }, [data, status, error, dispatch]);

  // Back button navigation handler
  const backBtnHandler: MouseEventHandler = () => {
    navigate("/");
  };

  // Update selected professor handler
  const profItemClickHandler = (prof: Professor) => {
    const selectedProf = professor.selectedProfessor;
    if (selectedProf && selectedProf.name === prof.name) {
      dispatch(
        professorActions.updateSelectedProfessor({ selectedProfessor: null })
      );
      return;
    }
    dispatch(
      professorActions.updateSelectedProfessor({ selectedProfessor: prof })
    );
  };

  return (
    <div className="container-md d-flex flex-column h-100">
      {/* <!-- Header --> */}
      <div className="row">
        <header>
          <div className="pt-5 pb-2">
            <div className="d-flex justify-content-between">
              <div className="my-auto">
                <i
                  className="bi bi-arrow-left-circle fs-1"
                  onClick={backBtnHandler}
                ></i>
              </div>
              <div className="d-flex justify-content-end">
                <div className="d-flex align-items-center">
                  <i className="bi bi-person fs-1"></i>
                </div>
                <div className="d-flex flex-column justify-content-center ms-3">
                  <span className="fs-3">Professors Database</span>
                </div>
              </div>
            </div>
          </div>
        </header>
      </div>
      {/* <!-- Main --> */}
      <div className="row h-80">
        <main className="col-12 d-flex flex-column h-100">
          {/* <!-- Student Number search bar --> */}
          <div className="row mt-2 gx-1">
            <div className="col d-flex align-items-center justify-content-end">
              <button
                className="btn btn-primary"
                data-bs-target="#addProfessorModal"
                data-bs-toggle="modal"
              >
                <i className="bi bi-person-check-fill"></i> Add
              </button>
              <button
                className="btn btn-primary ms-2"
                data-bs-target="#deleteProfessorModal"
                data-bs-toggle="modal"
                disabled={professor.selectedProfessor === null}
              >
                <i className="bi bi-person-dash-fill"></i> Delete
              </button>
              <button
                className="btn btn-primary ms-2"
                data-bs-target="#updateProfessorModal"
                data-bs-toggle="modal"
                disabled={professor.selectedProfessor === null}
              >
                <i className="bi bi-person-lines-fill"></i> Update
              </button>
              <i className="bi bi-arrow-repeat fs-3 ms-2"></i>
            </div>
            <div className="col-4 d-flex align-items-center justify-content-end">
              <form className="w-100">
                <div className="input-group">
                  <input
                    className="form-control"
                    type="text"
                    placeholder="Search Professor Name"
                  />
                  <button
                    className="btn btn-secondary"
                    type="button"
                    disabled={true}
                  >
                    <i className="bi bi-search"></i>
                  </button>
                </div>
              </form>
            </div>
          </div>
          {/* <!-- User info table --> */}
          <div className="row mt-2 gx-0 overflow-auto">
            <div className="table-responsive-xxl">
              <table className="table table-hover">
                <thead className="table-dark">
                  <tr>
                    <th>Id</th>
                    <th>Name</th>
                    <th>Contact Number</th>
                  </tr>
                </thead>
                <tbody>
                  {professor.professors &&
                    professor.professors.length > 0 &&
                    [...professor.professors].map((p) => (
                      <ProfessorItem
                        key={p.id}
                        professor={p}
                        onProfItemClick={profItemClickHandler}
                        focused={professor.selectedProfessor?.name === p.name}
                      />
                    ))}
                </tbody>
              </table>
            </div>
          </div>
          {/* <!-- User registered count --> */}
          <div className="row py-3 mt-auto">
            <div className="col d-flex justify-content-between align-items-center">
              <div>
                <h5>
                  Overall registered professors: {professor.professors.length}
                </h5>
              </div>
            </div>
          </div>
        </main>
      </div>
      <div>
        <AddProfessorModal />
        <DeleteProfessorModal
          selectedProfessor={
            professor.selectedProfessor ? professor.selectedProfessor : null
          }
        />
        <UpdateProfessorModal
          selectedProfessor={
            professor.selectedProfessor ? professor.selectedProfessor : null
          }
        />
      </div>
    </div>
  );
};

export default Professors;
