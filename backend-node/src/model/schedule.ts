import mongoose from "mongoose";
import { IIndexable } from "../types/IIndexable";

export interface ISchedule extends IIndexable {
  subjectCode: String;
  subjectName: String;
  day: String;
  time: String;
  room: String;
  yearLevel: String;
  yearAndSection: String;
  professor: mongoose.Types.ObjectId;
}

const scheduleSchema = new mongoose.Schema<ISchedule>({
  subjectCode: {
    type: String,
    trim: true,
    required: true,
  },
  subjectName: {
    type: String,
    trim: true,
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
  yearLevel: {
    type: String,
    required: true,
  },
  yearAndSection: {
    type: String,
    required: true,
  },
  professor: {
    type: mongoose.Schema.Types.ObjectId,
    required: true,
    ref: "Professor",
  },
});

scheduleSchema.methods.toJSON = function () {
  const schedule = this;
  const scheduleObj = schedule.toObject();
  delete scheduleObj.__v;
  return scheduleObj;
};

const Schedule = mongoose.model<ISchedule>("Schedule", scheduleSchema);

export default Schedule;
