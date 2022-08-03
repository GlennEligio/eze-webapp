import { createSlice } from "@reduxjs/toolkit";

interface AUTH_STATE {
  accessToken: string;
  username: string;
  type: string;
}

const createIntialState = () => {
  const storedToken = localStorage.getItem("accessToken");
  const username = localStorage.getItem("username");
  const type = localStorage.getItem("type");
  return {
    accessToken: storedToken !== null ? (storedToken as string) : "",
    username: username !== null ? (username as string) : "",
    type: type !== null ? (type as string) : "",
  };
};

const INITIAL_STATE: AUTH_STATE = createIntialState();

const authSlice = createSlice({
  name: "auth",
  initialState: INITIAL_STATE,
  reducers: {
    saveAuth(state, action) {
      state.accessToken = action.payload.accessToken;
      state.type = action.payload.type;
      state.username = action.payload.username;
    },
    removeAuth(state) {
      state.accessToken = "";
      state.type = "";
      state.username = "";
    },
  },
});

export default authSlice.reducer;
export const authActions = authSlice.actions;
