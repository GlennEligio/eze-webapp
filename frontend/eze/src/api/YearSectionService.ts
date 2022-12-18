import { RequestConfig, ApiError } from "../hooks/useHttp";
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
    throw new ApiError("Failed to fetch YearSections");
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
    throw new ApiError("Failed to fetch YearSections");
  });
  return responseObj;
};

const upload = async (jwt: string, formData: FormData) => {
  return await fetch(`${BACKEND_URI}/api/v1/yearSections/upload`, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${jwt}`,
    },
    body: formData,
  });
};

const download = async (jwt: string) => {
  return await fetch(`${BACKEND_URI}/api/v1/yearSections/download`, {
    method: "GET",
    headers: {
      Authorization: `Bearer ${jwt}`,
    },
  });
};

// for validation
export const isValidYearSection = (ys: CreateYearSection) => {
  let valid = true;
  if (validator.isEmpty(ys.sectionName)) {
    valid = false;
  }
  if (!validator.isNumeric(ys.yearLevel.yearNumber + "")) {
    valid = false;
  }
  return valid;
};

export default { createYearSection, deleteYearSection, upload, download };
