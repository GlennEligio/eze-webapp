export interface LoginRequestDto {
  username: string;
  password: string;
}

export interface LoginResponseDto {
  username: string;
  fullName: string;
  accountType: string;
  accessToken: string;
  refreshToken: string;
}

const BACKEND_URI = "http://localhost:8080";

const login = async (loginData: LoginRequestDto) => {
  const responseObj: LoginResponseDto = await fetch(
    `${BACKEND_URI}/api/v1/accounts/login`,
    {
      method: "POST",
      body: JSON.stringify(loginData),
      headers: {
        "Content-Type": "application/json",
      },
    }
  ).then((response) => {
    if (response.ok) {
      return response.json();
    }
    throw new Error("Invalid username/password");
  });
  return responseObj;
};

export default { login };
