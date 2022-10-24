import { createSlice } from "@reduxjs/toolkit";
import { Professor } from "../api/ProfessorService";

export interface ProfessorState {
  professors: Professor[];
  selectedProfessor: Professor | null;
}

const INITIAL_STATE: ProfessorState = {
  professors: [],
  selectedProfessor: null,
};

const professorSlice = createSlice({
  name: "professor",
  initialState: INITIAL_STATE,
  reducers: {
    addProfessors(state, action) {
      state.professors = action.payload.professors;
    },
    updateSelectedProfessor(state, action) {
      state.selectedProfessor = action.payload.selectedProfessor;
    },
    addProfessor(state, action) {
      state.professors = [...state.professors, action.payload.newProfessor];
    },
    updateProfessor(state, action) {
      state.professors = state.professors.map((p) => {
        if (p.name === action.payload.professor.name) {
          return action.payload.professor;
        }
        return p;
      });
    },
    removeProfessor(state, action) {
      state.professors = state.professors.filter(
        (p) => p.name !== action.payload.name
      );
    },
  },
});

export default professorSlice.reducer;
export const professorActions = professorSlice.actions;
