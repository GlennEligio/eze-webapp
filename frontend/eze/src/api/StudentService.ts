import { RequestConfig, ApiError } from "../hooks/useHttp";
import validator from "validator";
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
  yearLevel: number;
  profile: string;
}

export interface StudentFull {
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
  profile: string;
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
  profile: string;
}

const envSettings = (window as any)._env_;

const getBackendUri = () => {
  if (process.env.NODE_ENV === "development") {
    return envSettings.REACT_APP_BACKEND_SERVICE_URI_DEV;
  } else if (process.env.NODE_ENV === "production") {
    return envSettings.REACT_APP_BACKEND_SERVICE_URI_PROD;
  }
};

const BACKEND_URI = getBackendUri();

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
    throw new ApiError("Failed to fetch students");
  });
  return responseObj;
};

const getStudentByStudentNumber = async (requestConfig: RequestConfig) => {
  const responseObj: Student | StudentFull = await fetch(
    `${BACKEND_URI}${requestConfig.relativeUrl}` as string,
    {
      method: requestConfig.method || "GET",
      headers: requestConfig.headers != null ? requestConfig.headers : {},
    }
  ).then((response) => {
    if (response.ok) {
      return response.json();
    }
    throw new ApiError("Failed to fetch student");
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
    throw new ApiError("Failed to create student");
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
    throw new ApiError("Failed to update student");
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
    throw new ApiError("Failed to delete student");
  });
  return responseObj;
};

const upload = async (jwt: string, formData: FormData) => {
  return await fetch(`${BACKEND_URI}/api/v1/students/upload`, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${jwt}`,
    },
    body: formData,
  });
};

const download = async (jwt: string) => {
  return await fetch(`${BACKEND_URI}/api/v1/students/download`, {
    method: "GET",
    headers: {
      Authorization: `Bearer ${jwt}`,
    },
  });
};

// for validation
export const isValidStudent = (student: CreateUpdateStudentDto) => {
  let valid = true;
  if (
    !validator.matches(
      student.studentNumber,
      /^\d{4}-\d{5}-[(a-z)|(A-Z)]{2}-\d{2}$/
    )
  ) {
    console.log("Not a valid student number");
    valid = false;
  }
  if (validator.isEmpty(student.fullName)) {
    console.log("Empty name");
    valid = false;
  }
  if (validator.isEmpty(student.yearAndSection.sectionName)) {
    console.log("Empty sectionName");
    valid = false;
  }
  if (!validator.isMobilePhone(student.contactNumber, "en-PH")) {
    console.log("Not a valid phone number");
    valid = false;
  }
  if (!validator.isNumeric(student.yearLevel.yearNumber + "")) {
    console.log("Invalid yearNumber");
    valid = false;
  }
  return valid;
};

export default {
  getStudents,
  getStudentByStudentNumber,
  createStudent,
  updateStudent,
  deleteStudent,
  download,
  upload,
};
