import React, { useState, useEffect, FC, useRef } from "react";
import useHttp, { RequestConfig } from "../../../hooks/useHttp";
import YearLevelService, { YearLevel } from "../../../api/YearLevelService";
import { useSelector } from "react-redux";
import { IRootState } from "../../../store";
import { useDispatch } from "react-redux";
import { yearLevelAction } from "../../../store/yearLevelSlice";
import validator from "validator";
import RequestStatusMessage from "../Other/RequestStatusMessage";

interface DeleteYearLevelModalProps {
  yearLevels: YearLevel[];
}

const DeleteYearLevelModal: FC<DeleteYearLevelModalProps> = (props) => {
  const auth = useSelector((state: IRootState) => state.auth);
  const dispatch = useDispatch();
  const modal = useRef<HTMLDivElement | null>(null);
  const [yearNumber, setYearNumber] = useState("");
  const [yearLevels, setYearLevels] = useState<YearLevel[]>([]);
  const {
    sendRequest: deleteYearLevel,
    error,
    data,
    status,
    resetHttpState,
  } = useHttp<boolean>(YearLevelService.deleteYearLevel, false);

  // add hidden.bs.modal eventHandler to Modal at Component Mount
  useEffect(() => {
    if (modal.current) {
      modal.current.addEventListener("hidden.bs.modal", () => {
        resetHttpState();
      });
    }
  }, []);

  // remove yearLevel in Context upon success
  useEffect(() => {
    if (status === "completed" && error === null) {
      dispatch(
        yearLevelAction.removeYearLevel({
          yearNumber: Number.parseInt(yearNumber),
        })
      );
    }
  }, [status, data, error]);

  // prepopulate yearNumber and yearLevel state
  useEffect(() => {
    if (props.yearLevels && props.yearLevels.length > 0) {
      const sortedYearLevel = [...props.yearLevels].sort(
        (yl1, yl2) => yl1.yearNumber - yl2.yearNumber
      );
      setYearLevels(sortedYearLevel);
      setYearNumber(
        sortedYearLevel[0] && sortedYearLevel[0].yearNumber.toString()
      );
    } else {
      setYearLevels([]);
      setYearNumber("");
    }
  }, [props.yearLevels]);

  const deleteYearLevelHandler = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    console.log("Deleting YearLevel");

    if (!validator.isNumeric(yearNumber + "")) return;

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
            {
              <RequestStatusMessage
                data={data}
                error={error}
                status={status}
                loadingMessage="Deleting year level"
                successMessage="Year level deleted"
              />
            }
            <form onSubmit={deleteYearLevelHandler}>
              <div className="form-floating mb-3">
                <select
                  className="form-select"
                  id="deleteYearLevelNumber"
                  onChange={(e) => setYearNumber(e.currentTarget.value)}
                  value={yearNumber}
                >
                  {yearLevels.map((yl) => {
                    return (
                      <option key={yl.yearNumber} value={yl.yearNumber}>
                        {yl.yearNumber}
                      </option>
                    );
                  })}
                </select>
                <label htmlFor="deleteYearLevelNumber">Year Level</label>
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
