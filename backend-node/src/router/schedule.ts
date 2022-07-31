import express from "express";
import XLSX from "xlsx";
import Schedule from "../model/schedule";
import ApiError from "../error/ApiError";
import { uploadExcel } from "../middleware/file";
import { CustomRequest } from "../types/CustomRequest";

const router = express.Router();

/**
 * @openapi
 * '/api/schedules':
 *   get:
 *     tags:
 *       - Schedule
 *     summary: Get All Schedule
 *     responses:
 *       200:
 *         description: Successfully fetch all Schedules
 *         content:
 *           application/json:
 *             schema:
 *               type: array
 *               items:
 *                 $ref: '#/components/schemas/Schedule'
 */
router.get("/", async (_req, res, next) => {
  try {
    const schedules = await Schedule.find({}).populate({
      path: "professor",
      select: "name",
    });
    res.send(schedules);
  } catch (e) {
    next(e);
  }
});

/**
 * @openapi
 * '/api/schedules/download':
 *  get:
 *    tags:
 *    - Schedule
 *    summary: Download Schedules in excel file
 *    responses:
 *      200:
 *        description: Succesfully downloaded the schedule.xlsx file
 *        content:
 *          application/octet-stream:
 *            schema:
 *              type: string
 *              format: binary
 */
router.get("/download", async (_req, res, next) => {
  try {
    const schedules = await Schedule.find({});
    const schedulesJSON = JSON.stringify(schedules);
    const schedulesObj = JSON.parse(schedulesJSON);

    const worksheet = XLSX.utils.json_to_sheet(schedulesObj);
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, "Schedules");

    const buffer = XLSX.write(workbook, { type: "buffer", bookType: "xlsx" });
    res.set("Content-Disposition", "attachment; filename=schedules.xlsx");
    res.set("Content-Type", "application/octet-stream");
    res.send(buffer);
  } catch (e) {
    next(e);
  }
});

/**
 * @openapi
 * '/api/schedules/upload':
 *  post:
 *    tags:
 *    - Schedule
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
 *        description: Succesfully uploaded the schedule.xlsx file
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
      const schedulesJson = XLSX.utils.sheet_to_json(workbook.Sheets.Schedules);
      for (const scheduleJson of schedulesJson) {
        const schedule = new Schedule(scheduleJson);
        await schedule.save();
      }
      res.send();
    } catch (e) {
      next(e);
    }
  }
);

/**
 * @openapi
 * '/api/schedules/{id}':
 *   get:
 *     tags:
 *       - Schedule
 *     summary: Fetch a single Schedule using an id
 *     parameters:
 *       - name: id
 *         in: path
 *         required: true
 *         description: Id of the Schedule
 *         schema:
 *           type: string
 *     responses:
 *       200:
 *         description: An Schedule object
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/Schedule'
 *       404:
 *         description: Schedule not found
 */
router.get("/:id", async (req, res, next) => {
  try {
    const schedule = await Schedule.findById(req.params.id).populate({
      path: "professor",
      select: "name",
    });
    if (!schedule) {
      throw new ApiError(404, "No schedule with same id was found");
    }
    res.send(schedule);
  } catch (e) {
    next(e);
  }
});

/**
 * @openapi
 * '/api/schedules':
 *   post:
 *     tags:
 *       - Schedule
 *     summary: Create an Schedule object
 *     requestBody:
 *       description: Schedule to create
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/CreateScheduleInput'
 *     responses:
 *       201:
 *         description: Successfully created an Schedule
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/Schedule'
 *       400:
 *         description: Bad request
 */
router.post("/", async (req, res, next) => {
  try {
    const schedule = new Schedule(req.body);
    await schedule.save();
    res.send(schedule);
  } catch (e) {
    next(e);
  }
});

/**
 * @openapi
 * '/api/schedules/{id}':
 *   patch:
 *     tags:
 *       - Schedule
 *     summary: Update an Schedule information
 *     parameters:
 *       - name: id
 *         in: path
 *         required: true
 *         description: Id of the Schedule
 *         schema:
 *           type: string
 *     requestBody:
 *       description: Object for updating Schedule
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/UpdateScheduleInput'
 *     responses:
 *       200:
 *         description: Successfully updated an Schedule
 *       400:
 *         description: Bad request
 *       404:
 *         description: Schedule not found
 */
router.patch("/:id", async (req, res, next) => {
  try {
    const updates = Object.keys(req.body);
    const allowedUpdates = [
      "subjectCode",
      "subjectName",
      "day",
      "time",
      "room",
      "professor",
      "yearLevel",
      "yearAndSection",
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

    const schedule = await Schedule.findById(req.params.id);

    if (!schedule) {
      throw new ApiError(404, "No schedule of the same id was found");
    }

    updates.forEach((update) => (schedule[update] = req.body[update]));
    await schedule.save();
    res.send(schedule);
  } catch (e) {
    next(e);
  }
});

/**
 * @openapi
 * '/api/schedules/{id}':
 *   delete:
 *     tags:
 *       - Schedule
 *     summary: Delete an Schedule using id
 *     parameters:
 *       - name: id
 *         in: path
 *         required: true
 *         description: Id of the Schedule
 *         schema:
 *           type: string
 *     responses:
 *       200:
 *         description: Successfully deleted an Schedule
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/Schedule'
 *       404:
 *         description: Schedule not found
 */
router.delete("/:id", async (req, res, next) => {
  try {
    const schedule = await Schedule.findByIdAndDelete(req.params.id);
    if (!schedule) {
      throw new ApiError(404, "No schedule of the same id was found");
    }
    res.send(schedule);
  } catch (e) {
    next(e);
  }
});

export default router;
