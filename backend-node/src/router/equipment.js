const express = require("express");
const Equipment = require("../model/equipment");

const router = express.Router();

router.get("/equipments", async (req, res) => {
  try {
    const equipments = await Equipment.find({});
    res.send(equipments);
  } catch (error) {
    res.status(500).send();
  }
});

router.get("/equipments/:id", async (req, res) => {
  try {
    const equipment = await Equipment.findById(req.params.id);
    if (!equipment) {
      res.status(404).send();
      return;
    }
    res.send(equipment);
  } catch (error) {
    res.status(500).send(error);
  }
});

router.post("/equipments", async (req, res) => {
  try {
    const equipment = new Equipment(req.body);
    equipment.defectiveSince = new Date();
    const newEquip = await equipment.save();
    res.send(newEquip);
  } catch (error) {
    res.status(400).send(error);
  }
});

router.patch("/equipments/:id", async (req, res) => {
  try {
    const updates = Object.keys(req.body);
    const allowedUpdates = ["name", "barcode", "status", "defectiveSince"];
    const isValidUpdate = updates.every((update) =>
      allowedUpdates.includes(update)
    );

    if (!isValidUpdate) {
      res.status(400).send({
        error:
          "Update must only contain name, barcode, status, and defectiveSince",
      });
      return;
    }

    const equipment = await Equipment.findById(req.params.id);
    if (!equipment) {
      res.status(404).send({
        error: "No equipment with given id was found",
      });
      return;
    }
    updates.forEach((update) => (equipment[update] = req.body[update]));
    await equipment.save();
    res.send();
  } catch (e) {
    res.status(400).send(error);
  }
});

router.delete("/equipments/:id", async (req, res) => {
  try {
    const equipment = await Equipment.findByIdAndDelete(req.params.id);
    if (!equipment) {
      res.status(404).send();
      return;
    }
    res.send(equipment);
  } catch (error) {
    res.status(500).send(error);
  }
});

module.exports = router;
