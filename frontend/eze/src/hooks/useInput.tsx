import { ChangeEvent, useState } from "react";
import { StringLiteral } from "typescript";

type ValidationFunction = (input: any) => {
  valueIsValid: boolean;
  errorMessage: string;
};

interface UseInputHookReturn<T> {
  value: T;
  hasError: boolean;
  isValid: boolean;
  errorMessage: string;
  reset: () => void;
  inputBlurHandler: () => void;
  valueChangeHandler: (event: ChangeEvent<HTMLInputElement>) => void;
}

const useInput = <T,>(
  validateFn: ValidationFunction,
  defaultValue: T
): UseInputHookReturn<T> => {
  const [enteredValue, setEnteredValue] = useState<T>(defaultValue);
  const [isTouched, setIsTouched] = useState(false);

  const { valueIsValid, errorMessage } = validateFn(enteredValue);
  const hasError = !valueIsValid && isTouched;

  const valueChangeHandler = (event: ChangeEvent<HTMLInputElement>) => {
    setEnteredValue(event.target.value as T);
  };

  const inputBlurHandler = () => {
    setIsTouched(true);
  };

  const reset = () => {
    setEnteredValue(defaultValue);
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
