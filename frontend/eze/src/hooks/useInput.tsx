import { ChangeEvent, useState } from "react";

const useInput = (validateFn: Function) => {
  const [enteredValue, setEnteredValue] = useState("");
  const [isTouched, setIsTouched] = useState(false);

  const { valueIsValid, errorMessage } = validateFn(enteredValue);
  const hasError = !valueIsValid && isTouched;

  const valueChangeHandler = (event: ChangeEvent<HTMLInputElement>) => {
    setEnteredValue(event.target.value);
  };

  const inputBlurHandler = () => {
    setIsTouched(true);
  };

  const reset = () => {
    setEnteredValue("");
    setIsTouched(false);
  };

  return {
    value: enteredValue,
    hasError,
    isValid: valueIsValid,
    errorMessage,
    reset,
    inputBlurHandler,
    valueChangeHandler,
  };
};

export default useInput;
