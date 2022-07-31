import express from "express";
import XLSX from "xlsx";
import sharp from "sharp";
import Student, { IStudent } from "../model/student";
import ApiError from "../error/ApiError";
import { uploadExcel, uploadImage } from "../middleware/file";
import { CustomRequest } from "../types/CustomRequest";

const router = express.Router();

/**
 * @openapi
 * '/api/students':
 *   get:
 *     tags:
 *       - Student
 *     summary: Get All Students
 *     responses:
 *       200:
 *         description: Successfully fetch all Students
 *         content:
 *           application/json:
 *             schema:
 *               type: array
 *               items:
 *                 $ref: '#/components/schemas/Student'
 */
router.get("/", async (_req, res, next) => {
  try {
    const students = await Student.find({});
    res.send(students);
  } catch (e) {
    next(e);
  }
});

/**
 * @openapi
 * '/api/students/download':
 *  get:
 *    tags:
 *    - Student
 *    summary: Download Students in excel file
 *    responses:
 *      200:
 *        description: Succesfully downloaded the student.xlsx file
 *        content:
 *          application/octet-stream:
 *            schema:
 *              type: string
 *              format: binary
 */
router.get("/download", async (_req, res, next) => {
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

/**
 * @openapi
 * '/api/students/upload':
 *  post:
 *    tags:
 *    - Student
 *    summary: Upload excel file
 *    requestBody:
 *      content:
 *        multipart/form-data:
 *          schema:
 *            type: object
 *            properties:
 *              excel:
 *                type: string
 *                format: base64
 *          encoding:
 *            excel:
 *              contentType: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
 *    responses:
 *      200:
 *        description: Succesfully uploaded the student.xlsx file
 *      400:
 *        description: Bad request
 */
router.post(
  "/upload",
  uploadExcel.single("excel"),
  async (req: CustomRequest, res, next) => {
    try {
      const excelBuffer = req.file!.buffer;
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
  }
);

/**
 * @openapi
 * '/api/students/{id}':
 *   get:
 *     tags:
 *       - Student
 *     summary: Fetch a single Student using an id
 *     parameters:
 *       - name: id
 *         in: path
 *         required: true
 *         description: Id of the Student
 *         schema:
 *           type: string
 *     responses:
 *       200:
 *         description: An Student object
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/Student'
 *       404:
 *         description: Student not found
 */
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

/**
 * @openapi
 * '/api/students':
 *   post:
 *     tags:
 *       - Student
 *     summary: Create an Student object
 *     requestBody:
 *       description: Student to create
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/CreateStudentInput'
 *     responses:
 *       201:
 *         description: Successfully created an Student
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/Student'
 *       400:
 *         description: Bad request
 */
router.post("/", async (req, res, next) => {
  try {
    const student = new Student(req.body);
    const newStudent = await student.save();
    res.send(newStudent);
  } catch (e) {
    next(e);
  }
});

/**
 * @openapi
 * '/api/students/{id}':
 *   patch:
 *     tags:
 *       - Student
 *     summary: Update an Student information
 *     parameters:
 *       - name: id
 *         in: path
 *         required: true
 *         description: Id of the Student
 *         schema:
 *           type: string
 *     requestBody:
 *       description: Object for updating Student
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/UpdateStudentInput'
 *     responses:
 *       200:
 *         description: Successfully updated an Student
 *       400:
 *         description: Bad request
 *       404:
 *         description: Student not found
 */
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

/**
 * @openapi
 * '/api/students/{id}':
 *   delete:
 *     tags:
 *       - Student
 *     summary: Delete an Student using id
 *     parameters:
 *       - name: id
 *         in: path
 *         required: true
 *         description: Id of the Student
 *         schema:
 *           type: string
 *     responses:
 *       200:
 *         description: Successfully deleted an Student
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/Student'
 *       404:
 *         description: Student not found
 */
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

/**
 * @openapi
 * '/api/students/{id}/avatar':
 *  post:
 *    tags:
 *    - Student
 *    summary: Upload avatar file
 *    parameters:
 *      - name: id
 *        in: path
 *        required: true
 *        description: Id of student
 *        schema:
 *          type: string
 *    requestBody:
 *      content:
 *        multipart/form-data:
 *          schema:
 *            type: object
 *            properties:
 *              avatar:
 *                type: string
 *                format: base64
 *          encoding:
 *            avatar:
 *              contentType: image/png, image/jpg, image/jpeg
 *    responses:
 *      200:
 *        description: Succesfully uploaded the student avatar image file
 *      400:
 *        description: Bad request
 */
router.post(
  "/:id/avatar",
  uploadImage.single("avatar"),
  async (req: CustomRequest, res, next) => {
    try {
      const imageBuffer = await sharp(req.file!.buffer).png().toBuffer();
      const student = await Student.findById(req.params.id);
      if (!student) {
        throw new ApiError(404, "No student with same id was found");
      }
      student.image = imageBuffer;
      await student.save();
      res.send();
    } catch (e) {
      next(e);
    }
  }
);

/**
 * @openapi
 * '/api/students/{id}/avatar':
 *  get:
 *    tags:
 *    - Student
 *    summary: Download Students avatar image file
 *    parameters:
 *      - name: id
 *        description: Student id
 *        required: true
 *        in: path
 *        schema:
 *          type: string
 *    responses:
 *      200:
 *        description: Succesfully downloaded the student avatar file
 *        content:
 *          image/png:
 *            schema:
 *              type: string
 *              format: base64
 *      400:
 *        description: Bad request
 */
router.get("/:id/avatar", async (req, res, next) => {
  try {
    const student = await Student.findById(req.params.id);
    if (!student) {
      throw new ApiError(404, "No student with same id was found");
    }
    res.set("Content-Type", "image/png");
    res.send(student.image);
  } catch (e) {
    next(e);
  }
});

export default router;
