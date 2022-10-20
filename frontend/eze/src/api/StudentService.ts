import { RequestConfig } from "../hooks/useHttp";

export interface Student {
  id: number;
  studentNumber: string;
  fullName: string;
  yearAndSection: string;
  contactNumber: string;
  birthday: string;
  address: string;
  email: string;
  guardian: string;
  guardianNumber: string;
  yearNumber: number;
}

export interface CreateUpdateStudentDto {
  studentNumber: string;
  fullName: string;
  yearAndSection: {
    sectionName: string;
  };
  contactNumber: string;
  birthday?: string;
  address?: string;
  email?: string;
  guardian?: string;
  guardianNumber?: string;
  yearLevel: {
    yearNumber: number;
  };
}

const BACKEND_URI = "http://localhost:8080";

const getStudents = async (requestConfig: RequestConfig) => {
  const responseObj: Student[] = await fetch(
    `${BACKEND_URI}${requestConfig.relativeUrl}` as string,
    {
      method: requestConfig.method || "GET",
      headers: requestConfig.headers != null ? requestConfig.headers : {},
    }
  ).then((response) => {
    if (response.ok) {
      return response.json();
    }
    throw new Error("Failed to fetch students");
  });
  return responseObj;
};

const createStudent = async (requestConfig: RequestConfig) => {
  const responseObj: Student = await fetch(
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
    throw new Error("Failed to create student");
  });
  return responseObj;
};

const updateStudent = async (requestConfig: RequestConfig) => {
  const responseObj: Student = await fetch(
    `${BACKEND_URI}${requestConfig.relativeUrl}` as string,
    {
      method: requestConfig.method || "PUT",
      body: requestConfig.body ? JSON.stringify(requestConfig.body) : null,
      headers: requestConfig.headers != null ? requestConfig.headers : {},
    }
  ).then((response) => {
    if (response.ok) {
      return response.json();
    }
    throw new Error("Failed to update student");
  });
  return responseObj;
};

const deleteStudent = async (requestConfig: RequestConfig) => {
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
    throw new Error("Failed to delete student");
  });
  return responseObj;
};

export default { getStudents, createStudent, updateStudent, deleteStudent };
