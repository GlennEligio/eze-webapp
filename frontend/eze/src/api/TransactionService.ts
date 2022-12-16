import { RequestConfig, ApiError } from "../hooks/useHttp";
import { Equipment, isValidEquipment } from "./EquipmentService";
import { isValidProfessor, Professor } from "./ProfessorService";
import { isValidStudent, StudentFull } from "./StudentService";
import validator from "validator";

export enum TxStatus {
  PENDING = "PENDING",
  ACCEPTED = "ACCEPTED",
  DENIED = "DENIED",
}

export interface Transaction {
  txCode: string;
  equipmentsCount: number;
  equipmentsHistCount: number;
  borrower: string;
  yearAndSection: string;
  professor: string;
  borrowedAt: string;
  returnedAt: string;
  status: string;
}

export interface TransactionFull {
  txCode: string;
  equipments: Equipment[];
  equipmentsHistory: Equipment[];
  borrower: string;
  yearAndSection: string;
  professor: string;
  borrowedAt: string;
  returnedAt: string;
  status: string;
}

export interface CreateUpdateTransaction {
  equipments: Equipment[];
  borrower: StudentFull;
  professor: Professor;
  status: TxStatus;
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

// TODO: Refactor api functions so it will be only one function that takes different inputs
const getTransactions = async (requestConfig: RequestConfig) => {
  const responseObj: Transaction[] = await fetch(
    `${BACKEND_URI}${requestConfig.relativeUrl}`,
    {
      method: requestConfig.method || "GET",
      headers: requestConfig.headers != null ? requestConfig.headers : {},
    }
  ).then((response) => {
    if (response.ok) {
      return response.json();
    }
    throw new ApiError("Failed to fetch transactions");
  });
  return responseObj;
};

const getStudentTransaction = async (requestConfig: RequestConfig) => {
  const responseObj: Transaction[] = await fetch(
    `${BACKEND_URI}${requestConfig.relativeUrl}`,
    {
      method: requestConfig.method || "GET",
      headers: requestConfig.headers != null ? requestConfig.headers : {},
    }
  ).then((response) => {
    if (response.ok) {
      return response.json();
    }
    throw new ApiError("Failed to fetch student transactions");
  });
  return responseObj;
};

const getTransactionByCode = async (requestConfig: RequestConfig) => {
  const responseObj: TransactionFull[] = await fetch(
    `${BACKEND_URI}${requestConfig.relativeUrl}`,
    {
      method: requestConfig.method || "GET",
      headers: requestConfig.headers != null ? requestConfig.headers : {},
    }
  ).then((response) => {
    if (response.ok) {
      return response.json();
    }
    throw new ApiError("Failed to fetch transaction");
  });
  return responseObj;
};

const createTransaction = async (requestConfig: RequestConfig) => {
  const responseObj: Transaction = await fetch(
    requestConfig.relativeUrl
      ? `${BACKEND_URI}${requestConfig.relativeUrl}`
      : `${BACKEND_URI}/api/v1/transactions`,
    {
      method: requestConfig.method || "POST",
      body: requestConfig.body ? JSON.stringify(requestConfig.body) : null,
      headers: requestConfig.headers != null ? requestConfig.headers : {},
    }
  ).then((response) => {
    if (response.ok) {
      return response.json();
    }
    throw new ApiError("Failed to create transaction");
  });
  return responseObj;
};

const returnEquipments = async (requestConfig: RequestConfig) => {
  const responseObj: Transaction = await fetch(
    `${BACKEND_URI}${requestConfig.relativeUrl}`,
    {
      method: requestConfig.method || "PUT",
      headers: requestConfig.headers || {},
    }
  ).then((response) => {
    if (response.ok) {
      return response.json();
    }
    throw new ApiError("Failed to return equipments");
  });
  return responseObj;
};

const cancelTransaction = async (requestConfig: RequestConfig) => {
  const responseObj: boolean = await fetch(
    !!requestConfig.relativeUrl
      ? `${BACKEND_URI}${requestConfig.relativeUrl}`
      : `${BACKEND_URI}/api/v1/transaction`,
    {
      method: requestConfig.method || "DELETE",
      headers: requestConfig.headers || {},
    }
  ).then((response) => {
    if (response.ok) {
      return true;
    }
    throw new ApiError("Failed to cancel transaction");
  });
  return responseObj;
};

const updateTransactionStatus = async (requestConfig: RequestConfig) => {
  const responseObj: boolean = await fetch(
    !!requestConfig.relativeUrl
      ? `${BACKEND_URI}${requestConfig.relativeUrl}`
      : `${BACKEND_URI}/api/v1/transaction`,
    {
      method: requestConfig.method || "PUT",
      headers: requestConfig.headers || {},
    }
  ).then((response) => {
    if (response.ok) {
      return true;
    }
    throw new ApiError("Failed to update transaction status");
  });
  return responseObj;
};

const upload = async (jwt: string, formData: FormData) => {
  return await fetch(`${BACKEND_URI}/api/v1/transactions/upload`, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${jwt}`,
    },
    body: formData,
  });
};

const download = async (jwt: string, params: string) => {
  return await fetch(`${BACKEND_URI}/api/v1/transactions/download${params}`, {
    method: "GET",
    headers: {
      Authorization: `Bearer ${jwt}`,
    },
  });
};

// for validation
export const isValidTransaction = (t: CreateUpdateTransaction) => {
  let valid = true;
  if (t.equipments.length <= 0) {
    console.log("Empty equipments");
    valid = false;
  }
  t.equipments.forEach((e) => {
    if (!isValidEquipment(e)) {
      console.log("Not a valid equipment");
      valid = false;
    }
  });
  if (!isValidStudent(t.borrower)) {
    console.log("Not a valid student");
    valid = false;
  }
  if (!isValidProfessor(t.professor)) {
    console.log("Not a valid professor");
    valid = false;
  }
  if (validator.isEmpty(t.status)) {
    console.log("Empty status");
    valid = false;
  }
  return valid;
};

export default {
  getTransactions,
  getStudentTransaction,
  createTransaction,
  getTransactionByCode,
  returnEquipments,
  cancelTransaction,
  updateTransactionStatus,
  download,
  upload,
};
