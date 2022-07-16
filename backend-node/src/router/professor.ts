import express from "express";
import Professor from "../model/professor";
import ApiError from "../error/ApiError";
import XLSX from "xlsx";
import { uploadExcel } from "../middleware/file";
import { CustomRequest } from "../types/CustomRequest";

const router = express.Router();

router.get("/", async (_req, res, next) => {
  try {
    const professors = await Professor.find({});
    res.send(professors);
  } catch (e) {
    next(e);
  }
});

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

router.post("/", async (req, res, next) => {
  try {
    const professor = new Professor(req.body);
    const newProf = await professor.save();
    res.status(201).send(newProf);
  } catch (e) {
    next(e);
  }
});

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
