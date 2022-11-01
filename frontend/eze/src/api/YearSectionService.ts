import { RequestConfig } from "../hooks/useHttp";
import validator from "validator";

export interface YearSection {
  id: number;
  sectionName: string;
}

export interface CreateYearSection {
  sectionName: string;
  yearLevel: {
    yearNumber: number;
  };
}

const BACKEND_URI = "http://localhost:8080";

const createYearSection = async (requestConfig: RequestConfig) => {
  const responseObj: YearSection = await fetch(
    `${BACKEND_URI}${requestConfig.relativeUrl}` as string,
    {
      method: requestConfig.method || "POST",
      body: requestConfig.body ? JSON.stringify(requestConfig.body) : null,
      headers: requestConfig.headers != null ? requestConfig.headers : {},
    }
  ).then((response) => {
    if (response.ok) {
      return response.json();
    }
    throw new Error("Failed to fetch YearSections");
  });
  return responseObj;
};

const deleteYearSection = async (requestConfig: RequestConfig) => {
  const responseObj: boolean = await fetch(
    `${BACKEND_URI}${requestConfig.relativeUrl}` as string,
    {
      method: requestConfig.method || "DELETE",
      headers: requestConfig.headers != null ? requestConfig.headers : {},
    }
  ).then((response) => {
    if (response.ok) {
      return true;
    }
    throw new Error("Failed to fetch YearSections");
  });
  return responseObj;
};

// for validation
export const isValidYearSection = (ys: CreateYearSection) => {
  let valid = true;
  if (validator.isEmpty(ys.sectionName)) {
    console.log("Empty sectionName");
    valid = false;
  }
  if (validator.isNumeric(ys.yearLevel.yearNumber + "")) {
    console.log("Not a numeric yearNumber");
    valid = false;
  }
  return valid;
};

export default { createYearSection, deleteYearSection };
