import { createSlice } from "@reduxjs/toolkit";

export interface UiState {
  equipmentModal: string | null;
}

const INITIAL_STATE: UiState = {
  equipmentModal: null,
};

const uiSlice = createSlice({
  name: "ui",
  initialState: INITIAL_STATE,
  reducers: {
    updateEquipmentModal(state, action) {
      state.equipmentModal = action.payload.equipmentModal;
    },
  },
});

export default uiSlice.reducer;
export const uiActions = uiSlice.actions;
