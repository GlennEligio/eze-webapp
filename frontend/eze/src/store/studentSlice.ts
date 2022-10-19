import { createSlice } from "@reduxjs/toolkit";
import { Student } from "../api/StudentService";

export interface StudentState {
  students: Student[];
  selectedStudent: Student | null;
}

const INITIAL_STATE: StudentState = {
  students: [],
  selectedStudent: null,
};

const studentSlice = createSlice({
  name: "student",
  initialState: INITIAL_STATE,
  reducers: {
    addStudents(state, action) {
      state.students = action.payload.students;
    },
    updateSelectedStudent(state, action) {
      state.selectedStudent = action.payload.selectedStudent;
    },
    addStudent(state, action) {
      state.students = [...state.students, action.payload.newStudent];
    },
    updateStudent(state, action) {
      state.students = state.students.map((s) => {
        if (s.studentNumber === action.payload.student.studentNumber) {
          return action.payload.student;
        }
        return s;
      });
    },
    removeStudent(state, action) {
      state.students = state.students.filter(
        (s) => s.studentNumber !== action.payload.studentNumber
      );
    },
  },
});

export default studentSlice.reducer;
export const studentActions = studentSlice.actions;
