const express = require("express");
const Schedule = require("../model/schedule");

const router = express.Router();

router.get("/schedules", async (req, res) => {
  try {
    const schedules = await Schedule.find({}).populate({
      path: "professor",
      select: "name",
    });
    res.send(schedules);
  } catch (error) {
    res.status(500).send(error);
  }
});

router.get("/schedules/:id", async (req, res) => {
  try {
    const schedule = await Schedule.findById(req.params.id).populate({
      path: "professor",
      select: "name",
    });
    res.send(schedule);
  } catch (error) {
    res.status(500).send(error);
  }
});

router.post("/schedules", async (req, res) => {
  const schedule = new Schedule(req.body);
  try {
    await schedule.save();
    res.send(schedule);
  } catch (error) {
    res.status(500).send(error);
  }
});

router.patch("/schedules/:id", async (req, res) => {
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
      res.status(400).send();
      return;
    }

    const schedule = await Schedule.findById(req.params.id);

    if (!schedule) {
      res.status(404).send();
    }

    updates.forEach((update) => (schedule[update] = req.body[update]));
    await schedule.save();
    res.send(schedule);
  } catch (error) {
    res.status(500).send(error);
  }
});

router.delete("/schedules/:id", async (req, res) => {
  try {
    const schedule = await Schedule.findByIdAndDelete(req.params.id);
    if (!schedule) {
      res.status(404).send();
      return;
    }
    res.send(schedule);
  } catch (error) {
    res.status(500).send();
  }
});

module.exports = router;
