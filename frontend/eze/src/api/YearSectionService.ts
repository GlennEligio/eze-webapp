import { RequestConfig } from "../hooks/useHttp";

export interface YearSection {
  id: number;
  sectionName: string;
}

const BACKEND_URI = "http://localhost:8080";

const getAllSections = async (requestConfig: RequestConfig) => {
  const responseObj: YearSection[] = await fetch(
    `${BACKEND_URI}${requestConfig.relativeUrl}` as string,
    {
      method: requestConfig.method || "GET",
      headers: requestConfig.headers != null ? requestConfig.headers : {},
    }
  ).then((response) => {
    if (response.ok) {
      return response.json();
    }
    throw new Error("Failed to fetch YearSections");
  });
  return responseObj;
};

export default { getAllSections };
