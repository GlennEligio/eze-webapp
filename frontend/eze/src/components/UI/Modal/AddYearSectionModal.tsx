import React, { useState, useEffect, FC } from "react";
import useHttp, { RequestConfig } from "../../../hooks/useHttp";
import YearSectionService, {
  CreateYearSection,
  isValidYearSection,
  YearSection,
} from "../../../api/YearSectionService";
import { useSelector } from "react-redux";
import { IRootState } from "../../../store";
import { useDispatch } from "react-redux";
import { yearLevelAction } from "../../../store/yearLevelSlice";
import { YearLevel } from "../../../api/YearLevelService";

interface AddYearSectionModalProps {
  yearLevels: YearLevel[];
}

const AddYearSectionModal: FC<AddYearSectionModalProps> = (props) => {
  const auth = useSelector((state: IRootState) => state.auth);
  const dispatch = useDispatch();
  const [yearNumber, setYearNumber] = useState(1);
  const [sectionName, setSectionName] = useState("");
  const {
    sendRequest: createYearSection,
    data,
    error,
    status,
  } = useHttp<YearSection>(YearSectionService.createYearSection, false);

  useEffect(() => {
    if (status == "completed" && error === null) {
      dispatch(
        yearLevelAction.addYearLevelSection({
          yearNumber: yearNumber,
          yearSection: data,
        })
      );
    }
  }, [data]);

  const addYearSectionHandler = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    console.log("Adding YearSection");
    const newYearSection: CreateYearSection = {
      sectionName: sectionName,
      yearLevel: {
        yearNumber: yearNumber,
      },
    };

    if (!isValidYearSection(newYearSection)) {
      console.log("Invalid YearSection");
      return;
    }

    const requestConf: RequestConfig = {
      body: newYearSection,
      headers: {
        Authorization: `Bearer ${auth.accessToken}`,
        "Content-type": "application/json",
      },
      relativeUrl: "/api/v1/yearSections",
    };
    createYearSection(requestConf);
    setSectionName("");
    setYearNumber(1);
  };

  return (
    <div
      className="modal fade"
      id="addYearSectionModal"
      tabIndex={-1}
      aria-labelledby="addYearSectionModalLabel"
      aria-hidden="true"
    >
      <div className="modal-dialog modal-dialog-centered">
        <div className="modal-content">
          <div className="modal-header">
            <h1 className="modal-title fs-5" id="yearSectionModalLabel">
              Add Year Section
            </h1>
            <button
              type="button"
              className="btn-close"
              data-bs-dismiss="modal"
              aria-label="Close"
            ></button>
          </div>
          <div className="modal-body">
            <form onSubmit={addYearSectionHandler}>
              <div className="form-floating mb-3">
                <select
                  className="form-select"
                  id="newYearSectionYearLevel"
                  aria-label="IsDuplicable"
                  onChange={(e) =>
                    setYearNumber(
                      e.currentTarget.value
                        ? Number.parseInt(e.currentTarget.value)
                        : 1
                    )
                  }
                  value={yearNumber}
                >
                  {props.yearLevels &&
                    [...props.yearLevels]
                      .sort((yl1, yl2) => yl1.yearNumber - yl2.yearNumber)
                      .map((yl) => {
                        return (
                          <option key={yl.yearNumber} value={yl.yearNumber}>
                            {yl.yearNumber}
                          </option>
                        );
                      })}
                </select>
                <label htmlFor="newYearSectionYearLevel">Year Level</label>
              </div>
              <div className="form-floating mb-3">
                <input
                  type="text"
                  className="form-control"
                  id="newYearSectionName"
                  onChange={(e) => setSectionName(e.target.value)}
                  value={sectionName}
                />
                <label htmlFor="newYearSectionName">Section name</label>
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
                  Add
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AddYearSectionModal;
