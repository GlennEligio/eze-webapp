import { RequestConfig } from "../hooks/useHttp";

export enum AccountType {
  SADMIN = "SADMIN",
  ADMIN = "ADMIN",
  STUDENT_ASSISTANT = "SA",
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
}

export interface Account {
  id: string;
  fullName: string;
  username: string;
  email: string;
  type: AccountType;
  createdAt: string;
  active: boolean;
}

export interface CreateUpdateAccountDto {
  username: string;
  password: string;
  fullName: string;
  email: string;
  type: AccountType;
  active: boolean;
}

const BACKEND_URI = "http://localhost:8080";

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
    throw new Error("Invalid username/password");
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
    throw new Error("Failed to fetch accounts");
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
    throw new Error("Failed to create account");
  });
  return responseObj;
};

const updateAccount = async (requestConfig: RequestConfig) => {
  const responseObj: Account = await fetch(
    `${BACKEND_URI}${requestConfig.relativeUrl}` as string,
    {
      method: requestConfig.method || "PUT",
      body: requestConfig.body ? JSON.stringify(requestConfig.body) : null,
      headers: requestConfig.headers != null ? requestConfig.headers : {},
    }
  ).then((response) => {
    if (response.ok) {
      return response.json();
    }
    throw new Error("Failed to update account");
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
    throw new Error("Failed to update account");
  });
  return responseObj;
};

export default {
  login,
  getAccounts,
  createAccount,
  updateAccount,
  deleteAccount,
};
