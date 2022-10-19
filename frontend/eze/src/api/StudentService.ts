import { RequestConfig } from "../hooks/useHttp";

export interface Student {
  id: number;
  studentNumber: string;
  fullName: string;
  yearAndSection: {
    id: number;
    sectionName: string;
  };
  contactNumber: string;
  birthday: string;
  address: string;
  email: string;
  guardian: string;
  guardianNumber: string;
  yearLevel: {
    id: number;
    yearNumber: number;
    yearName: string;
  };
}

const BACKEND_URI = "http://localhost:8080";

const getStudents = async (requestConfig: RequestConfig) => {
  const responseObj: boolean = await fetch(
    `${BACKEND_URI}${requestConfig.relativeUrl}` as string,
    {
      method: requestConfig.method || "GET",
      headers: requestConfig.headers != null ? requestConfig.headers : {},
    }
  ).then((response) => {
    if (response.ok) {
      return true;
    }
    throw new Error("Failed to fetch students");
  });
  return responseObj;
};
