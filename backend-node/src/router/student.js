const express = require("express");
const Student = require("../model/student");

const router = express.Router();

router.get("/students", async (req, res) => {
  try {
    const students = await Student.find({});
    res.send(students);
  } catch (error) {
    res.status(500).send(error);
  }
});

router.get("/students/:id", async (req, res) => {
  try {
    const student = await Student.findById(req.params.id);
    if (!student) {
      res.status(404).send();
      return;
    }
    res.send(student);
  } catch (error) {
    res.status(500).send(error);
  }
});

router.post("/students", async (req, res) => {
  const student = new Student(req.body);
  try {
    const newStudent = await student.save();
    res.send(newStudent);
  } catch (error) {
    res.status(400).send(error);
  }
});

router.patch("/students/:id", async (req, res) => {
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
    res
      .status(400)
      .send("Update object includes properties that is not allowed");
  }

  try {
    const student = await Student.findById(req.params.id);
    if (!student) {
      res.status(404).send();
      return;
    }
    updates.forEach((update) => (student[update] = req.body[update]));
    await student.save();
    res.send(student);
  } catch (error) {
    res.status(500).send(error);
  }
});

router.delete("/students/:id", async (req, res) => {
  try {
    const student = await Student.findByIdAndDelete(req.params.id);
    if (!student) {
      res.status(404).send();
      return;
    }
    res.send(student);
  } catch (error) {
    res.status(500).send();
  }
});

module.exports = router;
