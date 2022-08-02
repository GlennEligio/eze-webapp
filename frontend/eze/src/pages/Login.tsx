import { FormEvent } from "react";
import useHttp from "../hooks/useHttp";
import useInput from "../hooks/useInput";
interface LoginData {
  username: string;
  password: string;
}

const Login = () => {
  const loginRequest = async (loginData: LoginData) => {
    const responseObj = await fetch(
      "http://localhost:3200/api/accounts/login",
      {
        method: "POST",
        body: JSON.stringify(loginData),
        headers: {
          "Content-Type": "application/json",
        },
      }
    ).then((response) => response.json());
    return responseObj;
  };

  const validateUserPass = (input: string) => {
    let errorMessage = "";
    let valueIsValid = true;

    if (input.trim() === "") {
      errorMessage = "Please enter an input";
      valueIsValid = false;
    }

    return {
      valueIsValid,
      errorMessage,
    };
  };

  const { sendRequest, data, error, status } = useHttp(loginRequest, true);
  const {
    value: enteredUname,
    hasError: unameInputHasError,
    isValid: enteredUnameIsValid,
    valueChangeHandler: unameChangeHandler,
    inputBlurHandler: unameBlurHandler,
    reset: resetUnameInput,
  } = useInput(validateUserPass);
  const {
    value: enteredPass,
    hasError: passInputHasError,
    isValid: enteredPassIsValid,
    valueChangeHandler: passChangeHandler,
    inputBlurHandler: passBlurHandler,
    reset: resetPassInput,
  } = useInput(validateUserPass);

  const submitHandler = (event: FormEvent) => {
    event.preventDefault();

    if (!enteredUnameIsValid || !enteredPassIsValid) {
      console.log("Invalid input");
      return;
    }

    sendRequest({ username: enteredUname, password: enteredPass });

    resetPassInput();
    resetUnameInput();
  };

  const unameInputClasses = unameInputHasError ? "invalid" : "";
  const passInputClasses = passInputHasError ? "invalid" : "";

  return (
    <div className="container-fluid p-0 flex-grow-1 d-flex flex-column">
      <div className="row gx-0 h-100">
        <main className="h-100">
          <div className="h-100 w-100 d-flex flex-column justify-content-between align-items-stretch">
            <div className="h-50 d-flex justify-content-center align-items-center">
              <img className="img-fluid" src="/img/debut.jpg" alt="Eze logo" />
            </div>
            <div className="bg-dark h-50 row gx-0 w-100">
              <div className="col-4 d-flex flex-column justify-content-center w-100">
                <form onSubmit={submitHandler}>
                  <div className="row gx-0">
                    <div className="col d-flex justify-content-end">
                      <i className="bi bi-person-fill text-white fs-1"></i>
                    </div>
                    <div className="col">
                      <div className={`mb-3 ${unameInputClasses}`}>
                        <input
                          className="bg-dark text-white border border-secondary border-1 form-control form-control-lg"
                          type="text"
                          name="id"
                          id="username"
                          placeholder="Username"
                          value={enteredUname}
                          onChange={unameChangeHandler}
                          onBlur={unameBlurHandler}
                        />
                      </div>
                    </div>
                    <div className="col"></div>
                  </div>
                  <div className="row gx-0">
                    <div className="col d-flex justify-content-end">
                      <i className="bi bi-lock-fill text-white fs-1"></i>
                    </div>
                    <div className="col">
                      <div className={`mb-3 ${passInputClasses}`}>
                        <input
                          className="bg-dark text-white border border-secondary border-1 form-control form-control-lg"
                          type="text"
                          name="password"
                          id="password"
                          placeholder="Password"
                          value={enteredPass}
                          onChange={passChangeHandler}
                          onBlur={passBlurHandler}
                        />
                      </div>
                    </div>
                    <div className="col"></div>
                  </div>
                  <div className="row gx-0">
                    <div className="col"></div>
                    <div className="col">
                      <div className="w-100">
                        <button className="btn btn-lg btn-info w-100 rounded-5">
                          Login
                        </button>
                      </div>
                    </div>
                    <div className="col"></div>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </main>
      </div>
    </div>
  );
};

export default Login;
