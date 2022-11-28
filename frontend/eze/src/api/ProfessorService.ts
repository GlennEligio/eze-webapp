import { RequestConfig, ApiError } from "../hooks/useHttp";
import validator from "validator";

export interface Professor {
  id: number;
  name: string;
  contactNumber: string;
}

export interface CreateUpdateProfessor {
  name: string;
  contactNumber: string;
}

const BACKEND_URI =
  process.env.NODE_ENV === "development"
    ? process.env.REACT_APP_BACKEND_GATEWAY_URI_DEV
    : process.env.REACT_APP_BACKEND_GATEWAY_URI_PROD;

const getProfessors = async (requestConfig: RequestConfig) => {
  const responseObj: Professor[] = await fetch(
    `${BACKEND_URI}/api/v1/professors`,
    {
      method: requestConfig.method || "GET",
      headers: requestConfig.headers != null ? requestConfig.headers : {},
    }
  ).then((response) => {
    if (response.ok) {
      return response.json();
    }
    throw new ApiError("Failed to fetch professors");
  });
  return responseObj;
};

const getProfessorByName = async (requestConfig: RequestConfig) => {
  const responseObj: Professor = await fetch(
    `${BACKEND_URI}${requestConfig.relativeUrl}`,
    {
      method: requestConfig.method || "GET",
      headers: requestConfig.headers != null ? requestConfig.headers : {},
    }
  ).then((response) => {
    if (response.ok) {
      return response.json();
    }
    throw new ApiError("Failed to fetch professor");
  });
  return responseObj;
};

const createProfessor = async (requestConfig: RequestConfig) => {
  const responseObj: Professor = await fetch(
    `${BACKEND_URI}/api/v1/professors`,
    {
      method: requestConfig.method || "POST",
      body: requestConfig.body ? JSON.stringify(requestConfig.body) : null,
      headers: requestConfig.headers != null ? requestConfig.headers : {},
    }
  ).then((response) => {
    if (response.ok) {
      return response.json();
    }
    throw new ApiError("Failed to create professor");
  });

  return responseObj;
};

const updateProfessor = async (requestConfig: RequestConfig) => {
  const responseObj: Professor = await fetch(
    `${BACKEND_URI}${requestConfig.relativeUrl!}`,
    {
      method: requestConfig.method || "PUT",
      body: requestConfig.body ? JSON.stringify(requestConfig.body) : null,
      headers: requestConfig.headers != null ? requestConfig.headers : {},
    }
  ).then((response) => {
    if (response.ok) {
      return response.json();
    }
    throw new ApiError("Failed to update professor");
  });

  return responseObj;
};

const deleteProfessor = async (requestConfig: RequestConfig) => {
  const responseObj: boolean = await fetch(
    `${BACKEND_URI}${requestConfig.relativeUrl!}`,
    {
      method: requestConfig.method || "DELETE",
      headers: requestConfig.headers != null ? requestConfig.headers : {},
    }
  ).then((response) => {
    if (response.ok) {
      return true;
    }
    throw new ApiError("Failed to delete professor");
  });

  return responseObj;
};

const upload = async (jwt: string, formData: FormData) => {
  return await fetch(`${BACKEND_URI}/api/v1/professors/upload`, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${jwt}`,
    },
    body: formData,
  });
};

const download = async (jwt: string) => {
  return await fetch(`${BACKEND_URI}/api/v1/professors/download`, {
    method: "GET",
    headers: {
      Authorization: `Bearer ${jwt}`,
    },
  });
};

// for validation
export const isValidProfessor = (prof: CreateUpdateProfessor) => {
  let valid = true;
  if (validator.isEmpty(prof.name!)) {
    console.log("Empty name");
    valid = false;
  }
  if (!validator.isMobilePhone(prof.contactNumber, "en-PH")) {
    console.log("Not a valid phone number");
    valid = false;
  }
  return valid;
};

export default {
  getProfessors,
  createProfessor,
  updateProfessor,
  deleteProfessor,
  getProfessorByName,
  upload,
  download,
};
