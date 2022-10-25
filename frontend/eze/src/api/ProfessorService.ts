import { RequestConfig } from "../hooks/useHttp";

export interface Professor {
  id: number;
  name: string;
  contactNumber: string;
}

export interface CreateUpdateProfessor {
  name?: string;
  contactNumber: string;
}

const BACKEND_URI = "http://localhost:8080";

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
    throw new Error("Failed to fetch professors");
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
    throw new Error("Failed to fetch professor");
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
    throw new Error("Failed to create professor");
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
    throw new Error("Failed to update professor");
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
    throw new Error("Failed to delete professor");
  });

  return responseObj;
};

export default {
  getProfessors,
  createProfessor,
  updateProfessor,
  deleteProfessor,
};
