import { RequestConfig, ApiError } from "../hooks/useHttp";
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

const BACKEND_URI =
  process.env.NODE_ENV === "development"
    ? process.env.REACT_APP_BACKEND_GATEWAY_URI_DEV
    : process.env.REACT_APP_BACKEND_GATEWAY_URI_PROD;

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
    throw new ApiError("Failed to fetch year levels");
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
    throw new ApiError("Failed to add year level");
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
    throw new ApiError("Failed to delete year level");
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

const upload = async (jwt: string, formData: FormData) => {
  return await fetch(`${BACKEND_URI}/api/v1/yearLevels/upload`, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${jwt}`,
    },
    body: formData,
  });
};

const download = async (jwt: string) => {
  return await fetch(`${BACKEND_URI}/api/v1/yearLevels/download`, {
    method: "GET",
    headers: {
      Authorization: `Bearer ${jwt}`,
    },
  });
};

export default {
  getYearLevels,
  createYearLevel,
  deleteYearLevel,
  download,
  upload,
};
