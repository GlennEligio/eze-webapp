import { createSlice } from "@reduxjs/toolkit";
import { Buffer } from "buffer";

export interface AuthState {
  accessToken: string;
  username: string;
  type: string;
  name: string;
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
  const type = localStorage.getItem("type");
  const name = localStorage.getItem("name");
  return {
    accessToken:
      storedToken !== null && checkJwtValidity(storedToken)
        ? (storedToken as string)
        : "",
    username: username !== null ? (username as string) : "",
    type: type !== null ? (type as string) : "",
    name: name !== null ? (name as string) : "",
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

      state.type = action.payload.type;
      localStorage.setItem("type", action.payload.type);

      state.username = action.payload.username;
      localStorage.setItem("username", action.payload.username);

      state.name = action.payload.name;
      localStorage.setItem("name", action.payload.name);
    },
    removeAuth(state) {
      state.accessToken = "";
      localStorage.setItem("accessToken", "");
      state.type = "";
      localStorage.setItem("type", "");
      state.username = "";
      localStorage.setItem("username", "");
      state.name = "";
      localStorage.setItem("name", "");
    },
  },
});

export default authSlice.reducer;
export const authActions = authSlice.actions;
