import { configureStore } from "@reduxjs/toolkit";
import authReducer, { AuthState } from "./authSlice";
import equipmentReducer, { EquipmentState } from "./equipmentSlice";
import uiReducer, { UiState } from "./uiSlice";
import accountReducer, { AccountState } from "./accountSlice";

export interface IRootState {
  auth: AuthState;
  equipment: EquipmentState;
  ui: UiState;
  account: AccountState;
}

const store = configureStore({
  reducer: {
    auth: authReducer,
    equipment: equipmentReducer,
    ui: uiReducer,
    account: accountReducer,
  },
  devTools: process.env.NODE_ENV !== "production",
});

export default store;
