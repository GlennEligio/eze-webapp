import { configureStore } from "@reduxjs/toolkit";
import authReducer, { AuthState } from "./authSlice";
import equipmentReducer, { EquipmentState } from "./equipmentSlice";

export interface IRootState {
  auth: AuthState;
  equipment: EquipmentState;
}

const store = configureStore({
  reducer: {
    auth: authReducer,
    equipment: equipmentReducer,
  },
  devTools: process.env.NODE_ENV !== "production",
});

export default store;
