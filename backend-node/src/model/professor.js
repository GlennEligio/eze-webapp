const mongoose = require("mongoose");

const professorSchema = new mongoose.Schema({
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
    validate(value) {
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

const Professor = mongoose.model("Professor", professorSchema);

module.exports = Professor;
