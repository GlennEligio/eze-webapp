import React, { useState, useEffect, FC, useRef } from "react";
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
import {
  validateNotEmpty,
  validatePositive,
} from "../../../validation/validations";
import useInput, { InputType } from "../../../hooks/useInput";

interface AddYearSectionModalProps {
  yearLevels: YearLevel[];
}

const AddYearSectionModal: FC<AddYearSectionModalProps> = (props) => {
  const auth = useSelector((state: IRootState) => state.auth);
  const dispatch = useDispatch();
  const modal = useRef<HTMLDivElement | null>(null);
  const {
    sendRequest: createYearSection,
    data,
    error,
    status,
    resetHttpState,
  } = useHttp<YearSection>(YearSectionService.createYearSection, false);

  // useInput for the controlled input and validation
  const {
    value: yearNumber,
    hasError: yearNumberInputHasError,
    isValid: yearNumberIsValid,
    valueChangeHandler: yearNumberChangeHandler,
    inputBlurHandler: yearNumberBlurHandler,
    reset: resetYearNumber,
    errorMessage: yearNumberErrorMessage,
    set: setYearNumber,
  } = useInput(validatePositive("Year number"), "", InputType.SELECT);
  const {
    value: sectionName,
    hasError: sectionNameInputHasError,
    isValid: sectionNameIsValid,
    valueChangeHandler: sectionNameChangeHandler,
    inputBlurHandler: sectionNameBlurHandler,
    errorMessage: sectionNameErrorMessage,
    set: setSectionName,
  } = useInput(validateNotEmpty("Year number"), "", InputType.TEXT);

  // add hidden.bs.modal eventHandler in the Modal at Component Mount
  useEffect(() => {
    if (modal.current) {
      modal.current.addEventListener("hidden.bs.modal", () => {
        resetHttpState();
      });
    }
  }, []);

  // Adding YearSection to a YearLevel after a successful request
  useEffect(() => {
    if (status === "completed" && error === null) {
      dispatch(
        yearLevelAction.addYearLevelSection({
          yearNumber: Number.parseInt(yearNumber),
          yearSection: data,
        })
      );
    }
  }, [data, status, error]);

  useEffect(() => {
    if (props.yearLevels && props.yearLevels.length > 0) {
      setYearNumber(props.yearLevels[0].yearNumber.toString());
    } else {
      setYearNumber("");
    }
  }, [props.yearLevels]);

  const addYearSectionHandler = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    console.log("Adding YearSection");
    const newYearSection: CreateYearSection = {
      sectionName: sectionName,
      yearLevel: {
        yearNumber: Number.parseInt(yearNumber),
      },
    };

    if (!yearNumberIsValid || !sectionNameIsValid) {
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
              <div className={yearNumberInputHasError ? "invalid" : ""}>
                {yearNumberInputHasError && (
                  <span>{yearNumberErrorMessage}</span>
                )}
                <div className="form-floating mb-3">
                  <select
                    className="form-select"
                    id="newYearSectionYearLevel"
                    aria-label="IsDuplicable"
                    onChange={yearNumberChangeHandler}
                    onBlur={yearNumberBlurHandler}
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
              </div>
              <div className={sectionNameInputHasError ? "invalid" : ""}>
                {sectionNameInputHasError && (
                  <span>{sectionNameErrorMessage}</span>
                )}
                <div className="form-floating mb-3">
                  <input
                    type="text"
                    className="form-control"
                    id="newYearSectionName"
                    onChange={sectionNameChangeHandler}
                    onBlur={sectionNameBlurHandler}
                    value={sectionName}
                  />
                  <label htmlFor="newYearSectionName">Section name</label>
                </div>
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
