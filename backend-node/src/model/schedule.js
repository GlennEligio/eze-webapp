const mongoose = require("mongoose");

// TODO: Finish schema
const scheduleSchema = new mongoose.Schema({
  subjectCode: {
    type: String,
    required: true,
  },
  subjectName: {
    type: String,
    required: true,
  },
  day: {
    type: String,
    required: true,
  },
  time: {
    type: String,
    required: true,
  },
  room: {
    type: String,
    required: true,
  },
  professor: {
    type: mongoose.Schema.Types.ObjectId,
    required: true,
    ref: "Professor",
  },
  yearLevel: {
    type: String,
    required: true,
  },
  yearAndSection: {
    type: String,
    required: true,
  },
});

const Schedule = mongoose.model("Schedule", scheduleSchema);

module.exports = Schedule;
