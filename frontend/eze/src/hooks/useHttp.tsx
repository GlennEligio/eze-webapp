import { useReducer, useCallback } from "react";

enum RequestActionKind {
  SEND = "SEND",
  SUCCESS = "SUCCESS",
  ERROR = "ERROR",
}

interface RequestState<T> {
  data: T | null;
  error: string | null;
  status: "pending" | "completed" | null;
}

interface RequestAction {
  type: string;
  responseData?: any;
  errorMessage?: any;
}

export interface RequestConfig {
  body?: { [props: string]: any };
  headers?: {
    [header: string]: string;
  };
  method?: string;
  relativeUrl?: string;
}

const createDataFetchReducer =
  <T,>() =>
  (state: RequestState<T>, action: RequestAction): RequestState<T> => {
    switch (action.type) {
      case RequestActionKind.SEND:
        return {
          ...state,
          status: "pending",
          error: null,
          data: null,
        } as RequestState<T>;
      case RequestActionKind.SUCCESS: {
        return {
          ...state,
          error: null,
          status: "completed",
          data: action.responseData,
        } as RequestState<T>;
      }
      case RequestActionKind.ERROR:
        return {
          ...state,
          error: action.errorMessage,
          status: "completed",
          data: null,
        } as RequestState<T>;
      default:
        throw new Error("Action not supported");
    }
  };

// function httpReducer(state: RequestState, action: RequestAction): RequestState {
//   if (action.type === RequestActionKind.SEND) {
//     return {
//       data: null,
//       error: null,
//       status: "pending",
//     };
//   }

//   if (action.type === RequestActionKind.SUCCESS) {
//     return {
//       data: action.responseData,
//       error: null,
//       status: "completed",
//     };
//   }

//   if (action.type === RequestActionKind.ERROR) {
//     return {
//       data: null,
//       error: action.errorMessage,
//       status: "completed",
//     };
//   }

//   return state;
// }

function useHttp<T>(requestFunction: Function, startWithPending = false) {
  const [httpState, dispatch] = useReducer(createDataFetchReducer<T>(), {
    status: startWithPending ? "pending" : null,
    data: null,
    error: null,
  });

  const sendRequest = useCallback(
    async function (requestConfig?: RequestConfig) {
      console.log("Send request:", requestConfig);
      dispatch({ type: RequestActionKind.SEND });
      try {
        const responseData = await requestFunction(requestConfig);
        console.log("Response Data: ", responseData);
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
