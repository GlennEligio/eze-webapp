import React, { useState, useEffect } from "react";
import useHttp, { RequestConfig } from "../../../hooks/useHttp";
import YearLevelService, {
  CreateYearLevelDto,
  isValidYearLevel,
  YearLevel,
} from "../../../api/YearLevelService";
import { useSelector } from "react-redux";
import { IRootState } from "../../../store";
import { useDispatch } from "react-redux";
import { yearLevelAction } from "../../../store/yearLevelSlice";
import useInput, { InputType } from "../../../hooks/useInput";
import { validatePositive } from "../../../validation/validations";

const AddYearLevelModal = () => {
  const auth = useSelector((state: IRootState) => state.auth);
  const dispatch = useDispatch();
  // const [yearNumber, setYearNumber] = useState(1);
  const {
    sendRequest: createYearLevel,
    data,
    error,
    status: requestStatus,
  } = useHttp<YearLevel>(YearLevelService.createYearLevel, false);

  // useInput for the controlled input and validation
  const {
    value: yearNumber,
    hasError: yearNumberInputHasError,
    isValid: yearNumberIsValid,
    valueChangeHandler: yearNumberChangeHandler,
    inputBlurHandler: yearNumberBlurHandler,
    reset: resetYearNumber,
    errorMessage: yearNumberErrorMessage,
  } = useInput(validatePositive("Year number"), "1", InputType.TEXT);

  // add YearLevel in the Redux after successful request
  useEffect(() => {
    if (requestStatus == "completed" && error === null) {
      dispatch(yearLevelAction.addYearLevel({ newYearLevel: data }));
    }
  }, [data, requestStatus, error]);

  const addYearLevelHandler = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    console.log("Adding YearLevel");
    const newYearNumber: CreateYearLevelDto = {
      yearNumber: Number.parseInt(yearNumber),
    };

    if (!yearNumberIsValid) {
      console.log("Invalid YearLevel");
      return;
    }

    const requestConf: RequestConfig = {
      body: newYearNumber,
      headers: {
        Authorization: `Bearer ${auth.accessToken}`,
        "Content-type": "application/json",
      },
      relativeUrl: "/api/v1/yearLevels",
    };
    createYearLevel(requestConf);
    resetYearNumber();
  };

  return (
    <div
      className="modal fade"
      id="addYearLevelModal"
      tabIndex={-1}
      aria-labelledby="addYearLevelModalLabel"
      aria-hidden="true"
    >
      <div className="modal-dialog modal-dialog-centered">
        <div className="modal-content">
          <div className="modal-header">
            <h1 className="modal-title fs-5" id="yearLevelModalLabel">
              Add Year level
            </h1>
            <button
              type="button"
              className="btn-close"
              data-bs-dismiss="modal"
              aria-label="Close"
            ></button>
          </div>
          <div className="modal-body">
            <form onSubmit={addYearLevelHandler}>
              <div className={yearNumberInputHasError ? "invalid" : ""}>
                {yearNumberInputHasError && (
                  <span>{yearNumberErrorMessage}</span>
                )}
                <div className="form-floating mb-3">
                  <input
                    type="text"
                    className="form-control"
                    id="newYearLevelNumber"
                    onChange={yearNumberChangeHandler}
                    onBlur={yearNumberBlurHandler}
                    value={yearNumber}
                  />
                  <label htmlFor="newAccountUsername">Year number</label>
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
                  Add Year Level
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AddYearLevelModal;
