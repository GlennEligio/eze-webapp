import { RequestConfig } from "../hooks/useHttp";
import { Equipment } from "./EquipmentService";
import { Professor } from "./ProfessorService";
import { StudentFull } from "./StudentService";

export interface Transaction {
  txCode: string;
  equipmentsCount: number;
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

const BACKEND_URI = "http://localhost:8080";

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
    throw new Error("Failed to fetch transactions");
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
    throw new Error("Failed to fetch transaction");
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
    throw new Error("Failed to create transaction");
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
    throw new Error("Failed to return equipments");
  });
  return responseObj;
};

export default {
  getTransactions,
  createTransaction,
  getTransactionByCode,
  returnEquipments,
};
