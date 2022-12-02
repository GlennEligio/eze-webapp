import { RequestConfig, ApiError } from "../hooks/useHttp";
import validator from "validator";

export interface Equipment {
  id: number;
  equipmentCode: string;
  name: string;
  barcode: string;
  status: string;
  defectiveSince: string;
  isDuplicable: boolean;
  isBorrowed: boolean;
}

export interface CreateUpdateEquipmentDto {
  name: string;
  barcode: string;
  status: string;
  defectiveSince: string;
  isDuplicable: boolean;
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

const getEquipments = async (requestConfig: RequestConfig) => {
  const responseObj: Equipment[] = await fetch(
    `${BACKEND_URI}/api/v1/equipments`,
    {
      method: requestConfig.method || "GET",
      headers: requestConfig.headers != null ? requestConfig.headers : {},
    }
  ).then((response) => {
    if (response.ok) {
      return response.json();
    }
    throw new ApiError("Failed to fetch equipments");
  });
  return responseObj;
};

const getEquipmentByBarcode = async (requestConfig: RequestConfig) => {
  const responseObj: Equipment = await fetch(
    `${BACKEND_URI}${requestConfig.relativeUrl}`,
    {
      method: requestConfig.method || "GET",
      headers: requestConfig.headers != null ? requestConfig.headers : {},
    }
  ).then((response) => {
    if (response.ok) {
      return response.json();
    }
    throw new ApiError("Failed to fetch equipment");
  });
  return responseObj;
};

const createEquipment = async (requestConfig: RequestConfig) => {
  const responseObj: Equipment = await fetch(
    `${BACKEND_URI}/api/v1/equipments`,
    {
      method: requestConfig.method || "POST",
      body:
        requestConfig.body != null ? JSON.stringify(requestConfig.body) : null,
      headers: requestConfig.headers != null ? requestConfig.headers : {},
    }
  ).then((response) => {
    if (response.ok) {
      return response.json();
    }
    throw new ApiError("Failed to create equipment");
  });
  return responseObj;
};

const updateEquipment = async (requestConfig: RequestConfig) => {
  console.log("Updating equipment", requestConfig);
  const responseObj: Equipment = await fetch(
    !!requestConfig.relativeUrl
      ? `${BACKEND_URI}${requestConfig.relativeUrl}`
      : `${BACKEND_URI}/api/v1/equipments/${requestConfig.body?.equipmentCode}`,
    {
      method: requestConfig.method || "PUT",
      body:
        requestConfig.body != null ? JSON.stringify(requestConfig.body) : null,
      headers: requestConfig.headers != null ? requestConfig.headers : {},
    }
  ).then((response) => {
    if (response.ok) {
      return response.json();
    }
    throw new ApiError("Failed to update equipment");
  });
  return responseObj;
};

const deleteEquipment = async (requestConfig: RequestConfig) => {
  const responseObj: boolean = await fetch(
    !!requestConfig.relativeUrl
      ? `${BACKEND_URI}${requestConfig.relativeUrl}`
      : `${BACKEND_URI}/api/v1/equipments/${requestConfig.body?.equipmentCode}`,
    {
      method: requestConfig.method || "DELETE",
      headers: requestConfig.headers != null ? requestConfig.headers : {},
    }
  ).then((response) => {
    if (response.ok) {
      return true;
    }
    throw new ApiError("Failed to update equipment");
  });
  return responseObj;
};

const upload = async (jwt: string, formData: FormData) => {
  return await fetch(`${BACKEND_URI}/api/v1/equipments/upload`, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${jwt}`,
    },
    body: formData,
  });
};

const download = async (jwt: string) => {
  return await fetch(`${BACKEND_URI}/api/v1/equipments/download`, {
    method: "GET",
    headers: {
      Authorization: `Bearer ${jwt}`,
    },
  });
};

// for validation
export const isValidEquipment = (eq: CreateUpdateEquipmentDto) => {
  let valid = true;
  if (validator.isEmpty(eq.name)) {
    console.log("Empty name");
    valid = false;
  }
  if (validator.isEmpty(eq.barcode)) {
    console.log("Empty barcode");
    valid = false;
  }
  if (validator.isEmpty(eq.status)) {
    console.log("Empty status");
    valid = false;
  }
  if (eq.isDuplicable === undefined) {
    console.log("Empty isDuplicable");
    valid = false;
  }
  return valid;
};

export default {
  getEquipments,
  getEquipmentByBarcode,
  createEquipment,
  updateEquipment,
  deleteEquipment,
  download,
  upload,
};
