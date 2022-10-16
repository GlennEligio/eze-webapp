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

const BACKEND_URI = "http://localhost:8080";

const getEquipments = async (jwt: string) => {
  const responseObj: Equipment[] = await fetch(
    `${BACKEND_URI}/api/v1/equipments`,
    {
      method: "GET",
      headers: {
        Authorization: `Bearer ${jwt}`,
      },
    }
  ).then((response) => {
    if (response.ok) {
      return response.json();
    }
    throw new Error("Failed to fetch equipments");
  });
  return responseObj;
};

export { getEquipments };
