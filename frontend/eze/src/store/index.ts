import { configureStore } from "@reduxjs/toolkit";
import authReducer, { AuthState } from "./authSlice";
import equipmentReducer, { EquipmentState } from "./equipmentSlice";
import uiReducer, { UiState } from "./uiSlice";
import accountReducer, { AccountState } from "./accountSlice";
import studentReducer, { StudentState } from "./studentSlice";
import yearLevelReducer, { YearLevelState } from "./yearLevelSlice";
import professorReducer, { ProfessorState } from "./professorSlice";

export interface IRootState {
  auth: AuthState;
  equipment: EquipmentState;
  ui: UiState;
  account: AccountState;
  student: StudentState;
  yearLevel: YearLevelState;
  professor: ProfessorState;
}

const store = configureStore({
  reducer: {
    auth: authReducer,
    equipment: equipmentReducer,
    ui: uiReducer,
    account: accountReducer,
    student: studentReducer,
    yearLevel: yearLevelReducer,
    professor: professorReducer,
  },
  devTools: process.env.NODE_ENV !== "production",
});

export default store;
