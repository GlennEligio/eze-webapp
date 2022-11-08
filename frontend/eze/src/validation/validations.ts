import validator from "validator";

export const validateNotEmpty = (inputName: string) => {
  return (inputValue: any) => {
    let errorMessage = "";
    let valueIsValid = true;

    if (validator.isEmpty(inputValue)) {
      errorMessage = `${inputName} can't be empty`;
      valueIsValid = false;
    }

    return {
      valueIsValid,
      errorMessage,
    };
  };
};

export const validatePattern = (inputName: string, pattern: string) => {
  return (inputValue: any) => {
    let errorMessage = "";
    let valueIsValid = true;

    if (validator.isEmpty(inputValue)) {
      errorMessage = `${inputName} can't be empty`;
      valueIsValid = false;
    }

    return {
      valueIsValid,
      errorMessage,
    };
  };
};

export const validatePhMobileNumber = (inputName: string, number: string) => {
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
