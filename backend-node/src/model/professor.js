const mongoose = required("mongoose");

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
  },
});

const Professor = mongoose.model("Professor", professorSchema);

module.exports = Professor;
