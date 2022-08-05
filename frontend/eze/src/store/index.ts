import { configureStore } from "@reduxjs/toolkit";
import authReducer, { AuthState } from "./authSlice";

export interface IRootState {
  auth: AuthState;
}

const store = configureStore({
  reducer: {
    auth: authReducer,
  },
  devTools: process.env.NODE_ENV !== "production",
});

export default store;
