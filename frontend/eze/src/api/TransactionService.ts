import { RequestConfig, ApiError } from "../hooks/useHttp";
import { Equipment, isValidEquipment } from "./EquipmentService";
import { isValidProfessor, Professor } from "./ProfessorService";
import { isValidStudent, StudentFull } from "./StudentService";
import validator from "validator";

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
  status: "PENDING" | "ACCEPTED" | "DENIED";
}

const windowObj = window as any;
const envObj = windowObj._env_;
const BACKEND_URI = `http://${envObj.REACT_APP_BACKEND_SERVICE_URI}`;

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
    `${BACKEND_URI}/api/v1/transactions`,
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
  createTransaction,
  getTransactionByCode,
  returnEquipments,
  download,
  upload,
};
