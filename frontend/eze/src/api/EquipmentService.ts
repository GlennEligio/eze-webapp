import { RequestConfig } from "../hooks/useHttp";

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
  equipmentCode?: string;
  name: string;
  barcode: string;
  status: string;
  defectiveSince: string;
  isDuplicable: boolean;
}

const BACKEND_URI = "http://localhost:8080";

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
    throw new Error("Failed to fetch equipments");
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
    throw new Error("Failed to create equipment");
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
    throw new Error("Failed to update equipment");
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
    throw new Error("Failed to update equipment");
  });
  return responseObj;
};

export { getEquipments, createEquipment, updateEquipment, deleteEquipment };