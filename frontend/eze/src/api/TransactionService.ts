import { RequestConfig } from "../hooks/useHttp";

export interface Transaction {
  txCode: number;
  equipmentsCount: number;
  borrower: string;
  yearAndSection: string;
  professor: string;
  borrowedAt: string;
  returnedAt: string;
  status: string;
}

const BACKEND_URI = "http://localhost:8080";

const getTransactions = async (requestConfig: RequestConfig) => {
  const responseObj: Transaction[] = await fetch(
    `${BACKEND_URI}/api/v1/transactions`,
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

export default { getTransactions };
