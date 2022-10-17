import { createSlice } from "@reduxjs/toolkit";
import { Equipment } from "../api/EquipmentService";

export interface EquipmentState {
  equipments: Equipment[];
  selectedEquipment: Equipment | null;
}

const INITIAL_STATE: EquipmentState = {
  equipments: [],
  selectedEquipment: null,
};

const equipmentSlice = createSlice({
  name: "equipment",
  initialState: INITIAL_STATE,
  reducers: {
    saveEquipments(state, action) {
      state.equipments = action.payload.equipments;
    },
    updateSelectedEquipment(state, action) {
      state.selectedEquipment = action.payload.selectedEquipment;
    },
    addEquipment(state, action) {
      state.equipments = [...state.equipments, action.payload.newEquipment];
    },
    updateEquipment(state, action) {
      state.equipments = state.equipments.map((e) => {
        if (e.equipmentCode === action.payload.equipment.equipmentCode) {
          return action.payload.equipment;
        }
        return e;
      });
    },
    removeEquipment(state, action) {
      state.equipments = state.equipments.filter(
        (eq) => eq.equipmentCode !== action.payload.equipmentCode
      );
    },
  },
});

export default equipmentSlice.reducer;
export const equipmentActions = equipmentSlice.actions;
