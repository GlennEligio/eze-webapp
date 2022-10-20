import React, { useState, useEffect } from "react";
import useHttp, { RequestConfig } from "../../../hooks/useHttp";
import YearLevelService from "../../../api/YearLevelService";
import { useSelector } from "react-redux";
import { IRootState } from "../../../store";
import { useDispatch } from "react-redux";
import { yearLevelAction } from "../../../store/yearLevelSlice";

const DeleteYearLevelModal = () => {
  const auth = useSelector((state: IRootState) => state.auth);
  const dispatch = useDispatch();
  const [yearNumber, setYearNumber] = useState(1);
  const {
    sendRequest: deleteYearLevel,
    data,
    error,
    status,
  } = useHttp<boolean>(YearLevelService.deleteYearLevel, false);

  useEffect(() => {
    if (status == "completed" && error === null) {
      dispatch(yearLevelAction.removeYearLevel({ yearNumber: yearNumber }));
    }
  }, [data]);

  const deleteYearLevelHandler = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    console.log("Deleting YearLevel");

    const requestConf: RequestConfig = {
      headers: {
        Authorization: `Bearer ${auth.accessToken}`,
      },
      relativeUrl: `/api/v1/yearLevels/${yearNumber}`,
    };
    deleteYearLevel(requestConf);
  };

  return (
    <div
      className="modal fade"
      id="deleteYearLevelModal"
      tabIndex={-1}
      aria-labelledby="deleteYearLevelModalLabel"
      aria-hidden="true"
    >
      <div className="modal-dialog modal-dialog-centered">
        <div className="modal-content">
          <div className="modal-header">
            <h1 className="modal-title fs-5" id="yearLevelModalLabel">
              Delete Year level
            </h1>
            <button
              type="button"
              className="btn-close"
              data-bs-dismiss="modal"
              aria-label="Close"
            ></button>
          </div>
          <div className="modal-body">
            <form onSubmit={deleteYearLevelHandler}>
              <div className="form-floating mb-3">
                <input
                  type="number"
                  className="form-control"
                  id="deleteYearLevelNumber"
                  min={1}
                  onChange={(e) =>
                    setYearNumber(
                      Number.parseInt(e.target.value ? e.target.value : "1")
                    )
                  }
                  value={yearNumber}
                />
                <label htmlFor="deleteAccountUsername">Year number</label>
              </div>
              <div className="modal-footer">
                <button
                  type="button"
                  className="btn btn-secondary"
                  data-bs-dismiss="modal"
                >
                  Close
                </button>
                <button type="submit" className="btn btn-primary">
                  Delete Year Level
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default DeleteYearLevelModal;
