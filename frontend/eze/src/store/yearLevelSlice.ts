import { createSlice } from "@reduxjs/toolkit";
import { YearLevel } from "../api/YearLevelService";

export interface YearLevelState {
  yearLevels: YearLevel[];
}

const INITIAL_STATE: YearLevelState = {
  yearLevels: [],
};

const yearLevelSlice = createSlice({
  name: "yearLevel",
  initialState: INITIAL_STATE,
  reducers: {
    addYearLevels(state, action) {
      state.yearLevels = action.payload.yearLevels;
    },
    addYearLevel(state, action) {
      state.yearLevels = [...state.yearLevels, action.payload.newYearLevel];
    },
    removeYearLevel(state, action) {
      state.yearLevels = state.yearLevels.filter(
        (yl) => yl.yearNumber !== action.payload.yearNumber
      );
    },
    addYearLevelSection(state, action) {
      state.yearLevels = state.yearLevels.map((yl) => {
        if (yl.yearNumber === action.payload.yearNumber) {
          yl.yearSections = [...yl.yearSections, action.payload.sectionName];
          return yl;
        }
        return yl;
      });
    },
    removeYearLevelSection(state, action) {
      state.yearLevels = state.yearLevels.map((yl) => {
        if (yl.yearNumber === action.payload.yearNumber) {
          yl.yearSections = yl.yearSections.filter(
            (ys) => ys.sectionName != action.payload.sectionName
          );
          return yl;
        }
        return yl;
      });
    },
  },
});

export default yearLevelSlice.reducer;
export const yearLevelAction = yearLevelSlice.actions;
