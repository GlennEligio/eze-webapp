const express = require("express");
const Schedule = require("../model/schedule");
const ApiError = require("../error/ApiError");

const router = express.Router();

router.get("/", async (req, res, next) => {
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

router.post("/", async (req, res, next) => {
  try {
    const schedule = new Schedule(req.body);
    await schedule.save();
    res.send(schedule);
  } catch (e) {
    next(e);
  }
});

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

module.exports = router;
