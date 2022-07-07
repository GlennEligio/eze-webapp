const express = require("express");
const Student = require("../model/student");
const ApiError = require("../error/ApiError");

const router = express.Router();

router.get("/", async (req, res, next) => {
  try {
    const students = await Student.find({});
    res.send(students);
  } catch (e) {
    next(e);
  }
});

router.get("/:id", async (req, res, next) => {
  try {
    const student = await Student.findById(req.params.id);
    if (!student) {
      throw new ApiError(404, "No student with same id was found");
    }
    res.send(student);
  } catch (e) {
    next(e);
  }
});

router.post("/", async (req, res, next) => {
  try {
    const student = new Student(req.body);
    const newStudent = await student.save();
    res.send(newStudent);
  } catch (e) {
    next(e);
  }
});

router.patch("/:id", async (req, res, next) => {
  try {
    const updates = Object.keys(req.body);
    const allowedUpdates = [
      "studentNumber",
      "fullname",
      "yearAndSection",
      "contactNumber",
      "birthday",
      "address",
      "email",
      "guardian",
      "guardianNumber",
      "yearLevel",
    ];
    const isValidUpdate = updates.every((update) =>
      allowedUpdates.includes(update)
    );

    if (!isValidUpdate) {
      throw new ApiError(
        400,
        "Update object includes properties that is not allowed"
      );
    }
    const student = await Student.findById(req.params.id);
    if (!student) {
      throw new ApiError(404, "Student with same id was not found");
    }
    updates.forEach((update) => (student[update] = req.body[update]));
    await student.save();
    res.send(student);
  } catch (e) {
    next(e);
  }
});

router.delete("/:id", async (req, res, next) => {
  try {
    const student = await Student.findByIdAndDelete(req.params.id);
    if (!student) {
      throw new ApiError(404, "Student with same id was not found");
    }
    res.send(student);
  } catch (e) {
    next(e);
  }
});

module.exports = router;
