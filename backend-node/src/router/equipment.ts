import express from "express";
import XLSX from "xlsx";
import Equipment, { IEquipment } from "../model/equipment";
import ApiError from "../error/ApiError";
import { uploadExcel } from "../middleware/file";

const router = express.Router();

router.get("/", async (_req, res, next) => {
  try {
    const equipments = await Equipment.find({});
    res.send(equipments);
  } catch (e) {
    next(e);
  }
});

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
