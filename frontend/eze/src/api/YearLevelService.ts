import { RequestConfig } from "../hooks/useHttp";
import { YearSection } from "./YearSectionService";
import validator from "validator";

export interface YearLevel {
  yearName: string;
  yearNumber: number;
  yearSections: YearSection[];
}

export interface CreateYearLevelDto {
  yearNumber: number;
}

const BACKEND_URI = "http://localhost:8080";

const getYearLevels = async (requestConfig: RequestConfig) => {
  const responseObj: YearLevel[] = await fetch(
    `${BACKEND_URI}${requestConfig.relativeUrl}` as string,
    {
      method: requestConfig.method || "GET",
      headers: requestConfig.headers != null ? requestConfig.headers : {},
    }
  ).then((response) => {
    if (response.ok) {
      return response.json();
    }
    throw new Error("Failed to fetch YearLevels");
  });
  return responseObj;
};

const createYearLevel = async (requestConfig: RequestConfig) => {
  const responseObj: YearLevel = await fetch(
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
    throw new Error("Failed to delete YearLevels");
  });
  return responseObj;
};

const deleteYearLevel = async (requestConfig: RequestConfig) => {
  const responseObj: true = await fetch(
    `${BACKEND_URI}${requestConfig.relativeUrl}` as string,
    {
      method: requestConfig.method || "DELETE",
      headers: requestConfig.headers != null ? requestConfig.headers : {},
    }
  ).then((response) => {
    console.log(response);
    if (response.ok) {
      return true;
    }
    throw new Error("Failed to fetch YearLevels");
  });
  return responseObj;
};

// for validation
export const isValidYearLevel = (yl: CreateYearLevelDto) => {
  if (validator.isEmpty(yl.yearNumber + "")) {
    console.log("Empty yearNumber");
    return false;
  }
  return true;
};

export default { getYearLevels, createYearLevel, deleteYearLevel };
