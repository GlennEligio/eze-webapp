const mongoose = require("mongoose");
const validator = require("validator");

const studentSchema = new mongoose.Schema({
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
  },
  contactNumber: {
    type: String,
    trim: true,
  },
  birthday: {
    type: String,
  },
  address: {
    type: String,
    trim: true,
  },
  email: {
    type: String,
    trim: true,
    lowercase: true,
    validate(value) {
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
  },
  yearLevel: {
    type: String,
    trim: true,
  },
  image: {
    type: Buffer,
  },
});

const Student = mongoose.model("Student", studentSchema);

module.export = Student;
