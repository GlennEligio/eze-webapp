import mongoose from "mongoose";
import validator from "validator";
import { IIndexable } from "../types/IIndexable";

export interface IStudent extends IIndexable {
  studentNumber: string;
  fullname: string;
  yearAndSection: string;
  contactNumber?: string;
  birthday?: string;
  address?: string;
  email?: string;
  guardian?: string;
  guardianNumber?: string;
  yearLevel?: string;
  image?: Buffer;
}

const studentSchema = new mongoose.Schema<IStudent>({
  studentNumber: {
    type: String,
    required: true,
    trim: true,
    unique: true,
  },
  fullname: {
    type: String,
    required: true,
    trim: true,
  },
  yearAndSection: {
    type: String,
    required: true,
    trim: true,
  },
  contactNumber: {
    type: String,
    trim: true,
    validate(value: string) {
      if (!value.match(/^(09|\+639)\d{9}$/)) {
        throw new Error("Please enter a valid phone number");
      }
      return true;
    },
  },
  birthday: {
    type: String,
    trim: true,
  },
  address: {
    type: String,
    trim: true,
  },
  email: {
    type: String,
    trim: true,
    lowercase: true,
    validate(value: string) {
      if (!validator.isEmail(value)) {
        throw new Error("Email is invalid");
      }
      return true;
    },
  },
  guardian: {
    type: String,
    trim: true,
  },
  guardianNumber: {
    type: String,
    trim: true,
    validate(value: string) {
      if (!value.match(/^(09|\+639)\d{9}$/)) {
        throw new Error("Please enter a valid phone number");
      }
      return true;
    },
  },
  yearLevel: {
    type: String,
    trim: true,
  },
  image: {
    type: Buffer,
  },
});

studentSchema.methods.toJSON = function () {
  const student = this;
  const studentObj = student.toObject();
  delete studentObj.__v;
  return studentObj;
};

const Student = mongoose.model<IStudent>("Student", studentSchema);

export default Student;
