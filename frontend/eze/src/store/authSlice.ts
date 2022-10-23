import { createSlice } from "@reduxjs/toolkit";
import { Buffer } from "buffer";

export interface AuthState {
  accessToken: string;
  username: string;
  accountType: string;
  fullName: string;
}

interface JwtPayload {
  _id: string;
  iat: number;
  exp: number;
}

const checkJwtValidity = (jwt: string) => {
  let isValid = false;
  const parts = jwt.split(".");
  if (!!parts[1]) {
    const payload = JSON.parse(
      Buffer.from(parts[1], "base64").toString()
    ) as JwtPayload;

    const expDate = new Date(payload.exp * 1000);
    const curDate = new Date();

    if (expDate > curDate) {
      isValid = true;
    }
    return isValid;
  }
};

const createIntialState = () => {
  const storedToken = localStorage.getItem("accessToken");
  const username = localStorage.getItem("username");
  const type = localStorage.getItem("accountType");
  const name = localStorage.getItem("fullName");
  return {
    accessToken:
      storedToken !== null && checkJwtValidity(storedToken)
        ? (storedToken as string)
        : "",
    username: username !== null ? (username as string) : "",
    accountType: type !== null ? (type as string) : "",
    fullName: name !== null ? (name as string) : "",
  };
};

const INITIAL_STATE: AuthState = createIntialState();

const authSlice = createSlice({
  name: "auth",
  initialState: INITIAL_STATE,
  reducers: {
    saveAuth(state, action) {
      state.accessToken = action.payload.accessToken;
      localStorage.setItem("accessToken", action.payload.accessToken);

      state.accountType = action.payload.accountType;
      localStorage.setItem("accountType", action.payload.accountType);

      state.username = action.payload.username;
      localStorage.setItem("username", action.payload.username);

      state.fullName = action.payload.fullName;
      localStorage.setItem("fullName", action.payload.name);
    },
    removeAuth(state) {
      state.accessToken = "";
      localStorage.setItem("accessToken", "");
      state.accountType = "";
      localStorage.setItem("accountType", "");
      state.username = "";
      localStorage.setItem("username", "");
      state.fullName = "";
      localStorage.setItem("fullName", "");
    },
  },
});

export default authSlice.reducer;
export const authActions = authSlice.actions;
