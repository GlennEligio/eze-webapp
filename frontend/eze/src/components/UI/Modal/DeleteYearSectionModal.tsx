import React, { useState, useEffect, FC, useRef } from "react";
import useHttp, { RequestConfig } from "../../../hooks/useHttp";
import YearSectionService, {
  YearSection,
} from "../../../api/YearSectionService";
import { useSelector } from "react-redux";
import { IRootState } from "../../../store";
import { useDispatch } from "react-redux";
import { yearLevelAction } from "../../../store/yearLevelSlice";
import { YearLevel } from "../../../api/YearLevelService";
import validator from "validator";
import RequestStatusMessage from "../Other/RequestStatusMessage";

interface DeleteYearSectionModalProps {
  yearLevels: YearLevel[];
}

const DeleteYearSectionModal: FC<DeleteYearSectionModalProps> = (props) => {
  const auth = useSelector((state: IRootState) => state.auth);
  const dispatch = useDispatch();
  const modal = useRef<HTMLDivElement | null>(null);
  const [yearNumber, setYearNumber] = useState("");
  const [sectionName, setSectionName] = useState("");
  const [yearSections, setYearSections] = useState<YearSection[]>([]);
  const {
    sendRequest: deleteYearSection,
    data,
    error,
    status,
    resetHttpState,
  } = useHttp<boolean>(YearSectionService.deleteYearSection, false);

  // Add hidden.bs.modal eventHandler to Modal on Component Mount
  useEffect(() => {
    if (modal.current) {
      modal.current.addEventListener("hidden.bs.modal", () => {
        resetHttpState();
      });
    }
  }, []);

  // Remove YearSection in the YearLevel object in Context
  useEffect(() => {
    if (status === "completed" && error === null) {
      dispatch(
        yearLevelAction.removeYearLevelSection({
          yearNumber: Number.parseInt(yearNumber),
          sectionName: sectionName,
        })
      );
    }
  }, [status, error, data]);

  // Prepopulate the yearLevel state when props.yearLevel updates
  useEffect(() => {
    if (props.yearLevels && props.yearLevels.length > 0) {
      setYearNumber(props.yearLevels[0].yearNumber.toString());
      if (
        props.yearLevels[0].yearSections &&
        props.yearLevels[0].yearSections.length > 0
      ) {
        setSectionName(props.yearLevels[0].yearSections[0].sectionName);
      } else {
        setSectionName("");
      }
    } else {
      setYearNumber("");
    }
  }, [props.yearLevels]);

  // Update YearSections when props.yearNumber updates
  useEffect(() => {
    const yearLevels = [...props.yearLevels];
    const currentYearLevel = yearLevels.find(
      (yl) => yl.yearNumber === Number.parseInt(yearNumber)
    );
    if (currentYearLevel && currentYearLevel.yearSections) {
      setYearSections(currentYearLevel.yearSections);
      if (currentYearLevel.yearSections[0]) {
        setSectionName(currentYearLevel.yearSections[0].sectionName);
      }
    }
  }, [yearNumber, props.yearLevels]);

  // form submitHandler for deleting YearSection
  const deleteYearSectionHandler = (
    event: React.FormEvent<HTMLFormElement>
  ) => {
    event.preventDefault();
    console.log("Deleting YearSection");

    if (validator.isEmpty(sectionName)) return;

    const requestConf: RequestConfig = {
      headers: {
        Authorization: `Bearer ${auth.accessToken}`,
      },
      relativeUrl: `/api/v1/yearSections/${sectionName}`,
    };
    deleteYearSection(requestConf);
  };

  return (
    <div
      className="modal fade"
      id="deleteYearSectionModal"
      tabIndex={-1}
      aria-labelledby="deleteYearSectionModalLabel"
      aria-hidden="true"
      ref={modal}
    >
      <div className="modal-dialog modal-dialog-centered">
        <div className="modal-content">
          <div className="modal-header">
            <h1 className="modal-title fs-5">Delete Year Section</h1>
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
                loadingMessage="Deleting year section..."
                successMessage="Year section deleted"
                key="Delete Year Section"
              />
            }
            <form onSubmit={deleteYearSectionHandler}>
              <div className="form-floating mb-3">
                <select
                  className="form-select"
                  id="deleteYearSectionYearLevel"
                  onChange={(e) => setYearNumber(e.currentTarget.value)}
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
                <select
                  className="form-select"
                  id="deleteYearSectionName"
                  aria-label="IsDuplicable"
                  onChange={(e) => setSectionName(e.currentTarget.value)}
                  value={sectionName}
                >
                  {[...yearSections].map((ys) => {
                    return (
                      <option key={ys.sectionName} value={ys.sectionName}>
                        {ys.sectionName}
                      </option>
                    );
                  })}
                </select>
                <label htmlFor="deleteYearSectionName">Section Name</label>
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
                  Delete
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default DeleteYearSectionModal;
