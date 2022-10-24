import { RequestConfig } from "../hooks/useHttp";

export interface Professor {
  id: number;
  name: string;
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

export default { getProfessors };
