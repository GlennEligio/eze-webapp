import validator from "validator";
import { RequestConfig, ApiError } from "../hooks/useHttp";

export enum AccountType {
  SADMIN = "SADMIN",
  ADMIN = "ADMIN",
  STUDENT_ASSISTANT = "SA",
  STUDENT = "STUDENT",
  PROF = "PROF",
}

export interface LoginRequestDto {
  username: string;
  password: string;
}

export interface LoginResponseDto {
  username: string;
  fullName: string;
  accountType: string;
  accessToken: string;
  refreshToken: string;
  profile: string;
}

export interface Account {
  id: string;
  fullName: string;
  username: string;
  email: string;
  type: AccountType;
  createdAt: string;
  active: boolean;
  profile: string;
}

export interface CreateUpdateAccountDto {
  username: string;
  password: string;
  fullName: string;
  email: string;
  type?: AccountType;
  active?: boolean;
  profile: string;
}

const envSettings = (window as any)._env_;

const getBackendUri = () => {
  if (
    process.env.NODE_ENV === "development" &&
    envSettings &&
    envSettings.REACT_APP_BACKEND_SERVICE_URI_DEV
  ) {
    return envSettings.REACT_APP_BACKEND_SERVICE_URI_DEV;
  } else if (
    process.env.NODE_ENV === "production" &&
    envSettings &&
    envSettings.REACT_APP_BACKEND_SERVICE_URI_PROD
  ) {
    return envSettings.REACT_APP_BACKEND_SERVICE_URI_PROD;
  } else {
    return "http://localhost:8080";
  }
};

const BACKEND_URI = getBackendUri();

const login = async (requestConfig: RequestConfig) => {
  const responseObj: LoginResponseDto = await fetch(
    `${BACKEND_URI}/api/v1/accounts/login`,
    {
      method: requestConfig.method || "POST",
      body:
        requestConfig.body != null ? JSON.stringify(requestConfig.body) : null,
      headers: requestConfig.headers != null ? requestConfig.headers : {},
    }
  ).then((response) => {
    if (response.ok) {
      return response.json();
    }
    throw new ApiError("Invalid username/password");
  });
  return responseObj;
};

const getAccounts = async (requestConfig: RequestConfig) => {
  const responseObj: Account[] = await fetch(`${BACKEND_URI}/api/v1/accounts`, {
    method: requestConfig.method || "GET",
    headers: requestConfig.headers != null ? requestConfig.headers : {},
  }).then((response) => {
    if (response.ok) {
      return response.json();
    }
    throw new ApiError("Failed to fetch accounts");
  });
  return responseObj;
};

const getAccountByUsername = async (requestConfig: RequestConfig) => {
  const responseObj: Account = await fetch(
    requestConfig.relativeUrl
      ? `${BACKEND_URI}${requestConfig.relativeUrl}`
      : `${BACKEND_URI}/api/v1/accounts`,
    {
      method: requestConfig.method || "GET",
      headers: requestConfig.headers != null ? requestConfig.headers : {},
    }
  ).then((response) => {
    if (response.ok) {
      return response.json();
    }
    throw new ApiError("Failed to fetch account");
  });
  return responseObj;
};

const createAccount = async (requestConfig: RequestConfig) => {
  const responseObj: Account = await fetch(`${BACKEND_URI}/api/v1/accounts`, {
    method: requestConfig.method || "POST",
    body: requestConfig.body ? JSON.stringify(requestConfig.body) : null,
    headers: requestConfig.headers != null ? requestConfig.headers : {},
  }).then((response) => {
    if (response.ok) {
      return response.json();
    }
    throw new ApiError("Failed to create account");
  });
  return responseObj;
};

const updateAccount = async (requestConfig: RequestConfig) => {
  const responseObj: Account = await fetch(
    requestConfig.relativeUrl
      ? `${BACKEND_URI}${requestConfig.relativeUrl}`
      : `${BACKEND_URI}/api/v1/accounts`,
    {
      method: requestConfig.method || "PUT",
      body: requestConfig.body ? JSON.stringify(requestConfig.body) : null,
      headers: requestConfig.headers != null ? requestConfig.headers : {},
    }
  ).then((response) => {
    if (response.ok) {
      return response.json();
    }
    throw new ApiError("Failed to update account");
  });
  return responseObj;
};

const deleteAccount = async (requestConfig: RequestConfig) => {
  const responseObj: boolean = await fetch(
    `${BACKEND_URI}${requestConfig.relativeUrl}` as string,
    {
      method: requestConfig.method || "DELETE",
      body: requestConfig.body ? JSON.stringify(requestConfig.body) : null,
      headers: requestConfig.headers != null ? requestConfig.headers : {},
    }
  ).then((response) => {
    if (response.ok) {
      return true;
    }
    throw new ApiError("Failed to update account");
  });
  return responseObj;
};

const upload = async (jwt: string, formData: FormData) => {
  return await fetch(`${BACKEND_URI}/api/v1/accounts/upload`, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${jwt}`,
    },
    body: formData,
  });
};

const download = async (jwt: string) => {
  return await fetch(`${BACKEND_URI}/api/v1/accounts/download`, {
    method: "GET",
    headers: {
      Authorization: `Bearer ${jwt}`,
    },
  });
};

// for validation
export const isValidAccount = (account: CreateUpdateAccountDto) => {
  let valid = true;
  if (validator.isEmpty(account.username)) {
    console.log("Empty username");
    valid = false;
  }
  if (validator.isEmpty(account.password)) {
    console.log("Empty password");
    valid = false;
  }
  if (validator.isEmpty(account.type || "")) {
    console.log("Empty type");
    valid = false;
  }
  if (validator.isEmpty(account.fullName)) {
    console.log("Empty full name");
    valid = false;
  }
  return valid;
};

export default {
  login,
  getAccounts,
  getAccountByUsername,
  createAccount,
  updateAccount,
  deleteAccount,
  download,
  upload,
};
