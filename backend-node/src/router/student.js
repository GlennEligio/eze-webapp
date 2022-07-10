const express = require("express");
const XLSX = require("xlsx");
const Student = require("../model/student");
const ApiError = require("../error/ApiError");
const { uploadExcel } = require("../middleware/file");

const router = express.Router();

router.get("/", async (req, res, next) => {
  try {
    const students = await Student.find({});
    res.send(students);
  } catch (e) {
    next(e);
  }
});

router.get("/download", async (req, res, next) => {
  try {
    const students = await Student.find({});
    const studentsJSON = JSON.stringify(students);
    const studentsObj = JSON.parse(studentsJSON);

    const worksheet = XLSX.utils.json_to_sheet(studentsObj);
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, "Students");

    const buffer = XLSX.write(workbook, { type: "buffer", bookType: "xlsx" });
    res.set("Content-Disposition", "attachment; filename=students.xlsx");
    res.set("Content-Type", "application/octet-stream");
    res.send(buffer);
  } catch (e) {
    next(e);
  }
});

router.post("/upload", uploadExcel.single("excel"), async (req, res, next) => {
  try {
    const excelBuffer = req.file.buffer;
    const workbook = XLSX.read(excelBuffer, { type: "buffer" });
    const studentsJson = XLSX.utils.sheet_to_json(workbook.Sheets.Students);
    for (const studentJson of studentsJson) {
      const student = new Student(studentJson);
      await student.save();
    }
    res.send();
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
