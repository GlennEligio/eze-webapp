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

interface RequestData {
  requestBody: { [prop: string]: any } | null;
  requestConfig: {
    jwt: string;
  } | null;
}

const createDataFetchReducer =
  <T,>() =>
  (state: RequestState<T>, action: RequestAction): RequestState<T> => {
    switch (action.type) {
      case RequestActionKind.SEND:
        return {
          ...state,
          isLoading: true,
          status: "pending",
        } as RequestState<T>;
      case RequestActionKind.SUCCESS: {
        return {
          ...state,
          isLoading: false,
          isError: false,
          status: "completed",
          data: action.responseData,
        } as RequestState<T>;
      }
      case RequestActionKind.ERROR:
        return {
          ...state,
          isLoading: false,
          isError: true,
          status: "completed",
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
    async function (requestData?: RequestData) {
      dispatch({ type: RequestActionKind.SEND });
      try {
        const responseData = await requestFunction(
          requestData?.requestConfig?.jwt,
          requestData?.requestBody
        );
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
