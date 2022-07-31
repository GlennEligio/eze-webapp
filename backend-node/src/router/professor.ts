import express from "express";
import Professor from "../model/professor";
import ApiError from "../error/ApiError";
import XLSX from "xlsx";
import { uploadExcel } from "../middleware/file";
import { CustomRequest } from "../types/CustomRequest";

const router = express.Router();

/**
 * @openapi
 * '/api/professors':
 *   get:
 *     tags:
 *       - Professor
 *     summary: Get All Professor
 *     responses:
 *       200:
 *         description: Successfully fetch all Professors
 *         content:
 *           application/json:
 *             schema:
 *               type: array
 *               items:
 *                 $ref: '#/components/schemas/Professor'
 */
router.get("/", async (_req, res, next) => {
  try {
    const professors = await Professor.find({});
    res.send(professors);
  } catch (e) {
    next(e);
  }
});

/**
 * @openapi
 * '/api/professors/download':
 *  get:
 *    tags:
 *    - Professor
 *    summary: Download professors in excel file
 *    responses:
 *      200:
 *        description: Succesfully downloaded the professor.xlsx file
 *        content:
 *          application/octet-stream:
 *            schema:
 *              type: string
 *              format: binary
 */
router.get("/download", async (_req, res, next) => {
  try {
    const professors = await Professor.find({});
    const professorsJSON = JSON.stringify(professors);
    const professorsObj = JSON.parse(professorsJSON);

    const worksheet = XLSX.utils.json_to_sheet(professorsObj);
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, "Professors");

    const buffer = XLSX.write(workbook, { type: "buffer", bookType: "xlsx" });
    res.set("Content-Disposition", "attachment; filename=professors.xlsx");
    res.set("Content-Type", "application/octet-stream");
    res.send(buffer);
  } catch (e) {
    next(e);
  }
});

/**
 * @openapi
 * '/api/professors/upload':
 *  post:
 *    tags:
 *    - Professor
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
 *        description: Succesfully uploaded the professor.xlsx file
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
      const professorsJson = XLSX.utils.sheet_to_json(
        workbook.Sheets.Professors
      );
      for (const professorJson of professorsJson) {
        const professor = new Professor(professorJson);
        await professor.save();
      }
      res.send();
    } catch (e) {
      next(e);
    }
  }
);

/**
 * @openapi
 * '/api/professors/{id}':
 *   get:
 *     tags:
 *       - Professor
 *     summary: Fetch a single Professor using an id
 *     parameters:
 *       - name: id
 *         in: path
 *         required: true
 *         description: Id of the professor
 *         schema:
 *           type: string
 *     responses:
 *       200:
 *         description: An Professor object
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/Professor'
 *       404:
 *         description: Professor not found
 */
router.get("/:id", async (req, res, next) => {
  try {
    const professor = await Professor.findById(req.params.id);
    if (!professor) {
      throw new ApiError(404, "No professor with same id was found");
    }
    res.send(professor);
  } catch (e) {
    next(e);
  }
});

/**
 * @openapi
 * '/api/professors':
 *   post:
 *     tags:
 *       - Professor
 *     summary: Create an Professor object
 *     requestBody:
 *       description: Professor to create
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/CreateProfessorInput'
 *     responses:
 *       201:
 *         description: Successfully created an Professor
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/Professor'
 *       400:
 *         description: Bad request
 */
router.post("/", async (req, res, next) => {
  try {
    const professor = new Professor(req.body);
    const newProf = await professor.save();
    res.status(201).send(newProf);
  } catch (e) {
    next(e);
  }
});

/**
 * @openapi
 * '/api/professors/{id}':
 *   patch:
 *     tags:
 *       - Professor
 *     summary: Update an professor information
 *     parameters:
 *       - name: id
 *         in: path
 *         required: true
 *         description: Id of the professor
 *         schema:
 *           type: string
 *     requestBody:
 *       description: Object for updating professor
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/UpdateProfessorInput'
 *     responses:
 *       200:
 *         description: Successfully updated an Professor
 *       400:
 *         description: Bad request
 *       404:
 *         description: Professor not found
 */
router.patch("/:id", async (req, res, next) => {
  try {
    const updates = Object.keys(req.body);
    const allowedUpdates = ["name", "contactNumber"];
    const isValidUpdate = updates.every((update) =>
      allowedUpdates.includes(update)
    );

    if (!isValidUpdate) {
      throw new ApiError(
        400,
        "Update object includes properties that is not allowed"
      );
    }

    const professor = await Professor.findById(req.params.id);
    if (!professor) {
      throw new ApiError(404, "No professor of the same id was found");
    }
    updates.forEach((update) => (professor[update] = req.body[update]));
    await professor.save();
    res.send(professor);
  } catch (e) {
    next(e);
  }
});

/**
 * @openapi
 * '/api/professors/{id}':
 *   delete:
 *     tags:
 *       - Professor
 *     summary: Delete an Professor using id
 *     parameters:
 *       - name: id
 *         in: path
 *         required: true
 *         description: Id of the professor
 *         schema:
 *           type: string
 *     responses:
 *       200:
 *         description: Successfully deleted an professor
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/Professor'
 *       404:
 *         description: Professor not found
 */
router.delete("/:id", async (req, res, next) => {
  try {
    const professor = await Professor.findByIdAndDelete(req.params.id);
    if (!professor) {
      throw new ApiError(404, "No professor of the same id was found");
    }
    res.send(professor);
  } catch (e) {
    next(e);
  }
});

export default router;
