import express from "express";
import XLSX from "xlsx";
import Equipment, { IEquipment } from "../model/equipment";
import ApiError from "../error/ApiError";
import { uploadExcel } from "../middleware/file";

const router = express.Router();

/**
 * @openapi
 * '/api/equipments':
 *  get:
 *     tags:
 *     - Equipment
 *     summary: Get all equipments
 *     responses:
 *       200:
 *         description: Successfully fetch all equipments
 *         content:
 *           application/json:
 *             schema:
 *               type: array
 *               items:
 *                 $ref: '#/components/schemas/Equipment'
 */
router.get("/", async (_req, res, next) => {
  try {
    const equipments = await Equipment.find({});
    res.send(equipments);
  } catch (e) {
    next(e);
  }
});

/**
 * @openapi
 * '/api/equipments/download':
 *  get:
 *    tags:
 *    - Equipment
 *    summary: Download equipments in excel file
 *    responses:
 *      200:
 *        description: Succesfully downloaded the equipment.xlsx file
 *        content:
 *          application/octet-stream:
 *            schema:
 *              type: string
 *              format: binary
 */
router.get("/download", async (_req, res, next) => {
  try {
    const equipments = await Equipment.find({});
    const equipmentsJSON = JSON.stringify(equipments);
    const equipmentsObj = JSON.parse(equipmentsJSON);

    const worksheet = XLSX.utils.json_to_sheet(equipmentsObj);
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, "Equipments");

    const buffer = XLSX.write(workbook, { type: "buffer", bookType: "xlsx" });
    res.set("Content-Disposition", "attachment; filename=equipments.xlsx");
    res.set("Content-Type", "application/octet-stream");
    res.send(buffer);
  } catch (e) {
    next(e);
  }
});

/**
 * @openapi
 * '/api/equipments/upload':
 *  post:
 *    tags:
 *    - Equipment
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
 *        description: Succesfully uploaded the equipment.xlsx file
 *      400:
 *        description: Bad request
 */
router.post("/upload", uploadExcel.single("excel"), async (req, res, next) => {
  try {
    const excelBuffer = req.file!.buffer;
    const workbook = XLSX.read(excelBuffer, { type: "buffer" });
    const equipmentsJson = XLSX.utils.sheet_to_json(workbook.Sheets.Equipments);
    for (const equipmentJson of equipmentsJson) {
      const equipment = new Equipment(equipmentJson);
      await equipment.save();
    }
    res.send();
  } catch (e) {
    next(e);
  }
});

/**
 * @openapi
 * '/api/equipments/{id}':
 *   get:
 *     tags:
 *       - Equipment
 *     summary: Fetch a single Equipment using an id
 *     parameters:
 *       - name: id
 *         in: path
 *         required: true
 *         description: Id of the equipment
 *         schema:
 *           type: string
 *     responses:
 *       200:
 *         description: An Equipment object
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/Equipment'
 *       404:
 *         description: No Equipment found
 */
router.get("/:id", async (req, res, next) => {
  try {
    const equipment = await Equipment.findById(req.params.id);
    if (!equipment) {
      throw new ApiError(404, "Not equipment found with specified id");
    }
    res.send(equipment);
  } catch (e) {
    next(e);
  }
});

/**
 * @openapi
 * '/api/equipments':
 *   post:
 *     tags:
 *       - Equipment
 *     summary: Create an Equipment
 *     requestBody:
 *       description: Equipment to create
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/CreateEquipmentInput'
 *     responses:
 *       201:
 *         description: Successfully created an Equipment
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/Equipment'
 *       400:
 *         description: Bad request
 */
router.post("/", async (req, res, next) => {
  try {
    const equipment = new Equipment(req.body);
    equipment.defectiveSince = new Date();
    const newEquip = await equipment.save();
    res.send(newEquip);
  } catch (e) {
    next(e);
  }
});

/**
 * @openapi
 * '/api/equipments/{id}':
 *   patch:
 *     tags:
 *       - Equipment
 *     summary: Update an equipment information
 *     parameters:
 *       - name: id
 *         in: path
 *         required: true
 *         description: Id of the equipment
 *         schema:
 *           type: string
 *     requestBody:
 *       description: Object for updating equipment
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/UpdateEquipmentInput'
 *     responses:
 *       200:
 *         description: Successfully updated an Equipment
 *       400:
 *         description: Bad request
 *       404:
 *         description: Equipment not found
 */
router.patch("/:id", async (req, res, next) => {
  try {
    const updatesObject = req.body as Partial<IEquipment>;
    const updates = Object.keys(req.body);
    const allowedUpdates = ["name", "barcode", "status", "defectiveSince"];
    const isValidUpdate = updates.every((update) =>
      allowedUpdates.includes(update)
    );

    if (!isValidUpdate) {
      throw new ApiError(
        400,
        "Update object includes properties that is not allowed"
      );
    }

    const equipment = await Equipment.findById(req.params.id);
    if (!equipment) {
      throw new ApiError(404, "No equipment of same id was found");
    }
    updates.forEach((update) => (equipment[update] = req.body[update]));
    await equipment.save();
    res.send();
  } catch (e) {
    next(e);
  }
});

/**
 * @openapi
 * '/api/equipments/{id}':
 *   delete:
 *     tags:
 *       - Equipment
 *     summary: Delete an Equipment using id
 *     parameters:
 *       - name: id
 *         in: path
 *         required: true
 *         description: Id of the equipment
 *         schema:
 *           type: string
 *     responses:
 *       200:
 *         description: Successfully deleted an equipment
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/Equipment'
 *       404:
 *         description: Equipment not found
 */
router.delete("/:id", async (req, res, next) => {
  try {
    const equipment = await Equipment.findByIdAndDelete(req.params.id);
    if (!equipment) {
      throw new ApiError(404, "No equipment with same id was found");
    }
    res.send(equipment);
  } catch (e) {
    next(e);
  }
});

export default router;
