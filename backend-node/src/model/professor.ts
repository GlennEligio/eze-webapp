import mongoose from "mongoose";
import { IIndexable } from "../types/IIndexable";

export interface IProfessor extends IIndexable {
  name: string;
  contactNumber: string;
}

const professorSchema = new mongoose.Schema<IProfessor>({
  name: {
    type: String,
    required: true,
    trim: true,
    unique: true,
  },
  contactNumber: {
    type: String,
    required: true,
    trim: true,
    unique: true,
    validate(value: string) {
      if (!value.match(/^(09|\+639)\d{9}$/)) {
        throw new Error("Please enter a valid phone number");
      }
      return true;
    },
  },
});

professorSchema.virtual("transactions", {
  ref: "Transaction",
  localField: "_id",
  foreignField: "professor",
});

professorSchema.methods.toJSON = function () {
  const professor = this;
  const professorObj = professor.toObject();
  delete professorObj.__v;
  return professorObj;
};

const Professor = mongoose.model<IProfessor>("Professor", professorSchema);

export default Professor;
