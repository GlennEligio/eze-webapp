import { FormEvent, useEffect } from "react";
import { useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";
import AccountService from "../api/AccountService";
import useHttp from "../hooks/useHttp";
import useInput from "../hooks/useInput";
import { authActions } from "../store/authSlice";
import { LoginResponseDto } from "../api/AccountService";

const Login = () => {
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
  const {
    sendRequest: login,
    data,
    error,
    status,
  } = useHttp<LoginResponseDto>(AccountService.login, false);
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
  const dispatch = useDispatch();
  const navigate = useNavigate();

  useEffect(() => {
    if (status === "completed") {
      if (data) {
        if (error === null) {
          const authResp = data as LoginResponseDto;
          dispatch(
            authActions.saveAuth({
              accessToken: authResp.accessToken,
              username: authResp.username,
              accountType: authResp.accountType,
              name: authResp.fullName,
            })
          );
          navigate("/");
          return;
        }
      }
    }
  }, [status, data, error, dispatch, navigate]);

  const submitHandler = (event: FormEvent) => {
    event.preventDefault();

    if (!enteredUnameIsValid || !enteredPassIsValid) {
      return;
    }
    login({
      requestBody: { username: enteredUname, password: enteredPass },
      requestConfig: null,
    });

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
                {status === "pending" && (
                  <div className="d-flex justify-content-center mb-3">
                    <div
                      className="spinner-border text-light me-3"
                      role="status"
                    >
                      <span className="visually-hidden">Loading...</span>
                    </div>
                    <span className="text-light fs-5">Logging in....</span>
                  </div>
                )}
                {error && (
                  <div className="d-flex justify-content-center mb-3">
                    <span className="text-light fs-5">{error}</span>
                  </div>
                )}
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
                          type="password"
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
