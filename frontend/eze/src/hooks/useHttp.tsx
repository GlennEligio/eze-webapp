import { useReducer, useCallback } from "react";

enum RequestActionKind {
  SEND = "SEND",
  SUCCESS = "SUCCESS",
  ERROR = "ERROR",
}

interface RequestState {
  data: object | null;
  error: string | null;
  status: string | null;
}

interface RequestAction {
  type: string;
  responseData?: any;
  errorMessage?: any;
}

function httpReducer(state: RequestState, action: RequestAction): RequestState {
  if (action.type === RequestActionKind.SEND) {
    return {
      data: null,
      error: null,
      status: "pending",
    };
  }

  if (action.type === RequestActionKind.SUCCESS) {
    return {
      data: action.responseData,
      error: null,
      status: "completed",
    };
  }

  if (action.type === RequestActionKind.ERROR) {
    return {
      data: null,
      error: action.errorMessage,
      status: "completed",
    };
  }

  return state;
}

function useHttp(requestFunction: Function, startWithPending = false) {
  const [httpState, dispatch] = useReducer(httpReducer, {
    status: startWithPending ? "pending" : null,
    data: null,
    error: null,
  });

  const sendRequest = useCallback(
    async function (requestData: any) {
      dispatch({ type: RequestActionKind.SEND });
      try {
        const responseData = await requestFunction(requestData);
        dispatch({ type: RequestActionKind.SUCCESS, responseData });
      } catch (error) {
        dispatch({
          type: RequestActionKind.ERROR,
          errorMessage: (error as Error).message || "Something went wrong!",
        });
      }
    },
    [requestFunction]
  );

  return {
    sendRequest,
    ...httpState,
  };
}

export default useHttp;
