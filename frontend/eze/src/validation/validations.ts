import validator from "validator";

export const validateNotEmpty = (inputName: string) => {
  return (inputValue: string) => {
    let errorMessage = "";
    let valueIsValid = true;

    if (validator.isEmpty(inputValue.trim())) {
      errorMessage = `${inputName} can't be empty`;
      valueIsValid = false;
    }

    return {
      valueIsValid,
      errorMessage,
    };
  };
};

export const validatePattern = (
  inputName: string,
  pattern: RegExp,
  message?: string
) => {
  return (inputValue: any) => {
    let errorMessage = "";
    let valueIsValid = true;

    if (!validator.matches(inputValue, pattern)) {
      errorMessage = `${inputName} ${message}`;
      valueIsValid = false;
    }

    return {
      valueIsValid,
      errorMessage,
    };
  };
};

export const validateContains = (
  inputName: string,
  listOfAllowedInputs: string[]
) => {
  return (inputValue: any) => {
    let errorMessage = "";
    let valueIsValid = true;

    if (!listOfAllowedInputs.includes(inputValue)) {
      errorMessage = `${inputName} can only be ${listOfAllowedInputs}`;
      valueIsValid = false;
    }

    return {
      valueIsValid,
      errorMessage,
    };
  };
};

export const validatePhMobileNumber = (inputName: string) => {
  return (inputValue: any) => {
    let errorMessage = "";
    let valueIsValid = true;

    if (!validator.isMobilePhone(inputValue, "en-PH")) {
      errorMessage = `${inputName} must be valid PH mobile number`;
      valueIsValid = false;
    }

    return {
      valueIsValid,
      errorMessage,
    };
  };
};

export const validatePositive = (inputName: string) => {
  return (inputValue: string) => {
    let errorMessage = "";
    let valueIsValid = true;

    if (!(validator.isNumeric(inputValue) && Number.parseInt(inputValue) > 0)) {
      errorMessage = `${inputName} must be positive integer`;
      valueIsValid = false;
    }

    return {
      valueIsValid,
      errorMessage,
    };
  };
};

export const validateNotUndefined = (inputName: string) => {
  return (inputValue: any) => {
    let errorMessage = "";
    let valueIsValid = true;

    if (inputValue !== undefined || inputValue !== null) {
      errorMessage = `${inputName} must be present`;
      valueIsValid = false;
    }

    return {
      valueIsValid,
      errorMessage,
    };
  };
};