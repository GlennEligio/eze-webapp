const express = require("express");
const Equipment = require("../model/equipment");
const ApiError = require("../error/ApiError");

const router = express.Router();

router.get("/equipments", async (req, res, next) => {
  try {
    const equipments = await Equipment.find({});
    res.send(equipments);
  } catch (e) {
    next(e);
  }
});

router.get("/equipments/:id", async (req, res, next) => {
  try {
    const equipment = await Equipment.findById(req.params.id);
    if (!equipment) {
      throw new ApiError(404, "Not equipment found with specified id");
    }
    res.send(equipment);
  } catch (error) {
    next(e);
  }
});

router.post("/equipments", async (req, res, next) => {
  try {
    const equipment = new Equipment(req.body);
    equipment.defectiveSince = new Date();
    const newEquip = await equipment.save();
    res.send(newEquip);
  } catch (e) {
    next(e);
  }
});

router.patch("/equipments/:id", async (req, res, next) => {
  try {
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

router.delete("/equipments/:id", async (req, res, next) => {
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

module.exports = router;
