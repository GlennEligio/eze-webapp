const express = require("express");
const Professor = require("../model/professor");
const ApiError = require("../error/ApiError");

const router = express.Router();

router.get("/", async (req, res, next) => {
  try {
    const professors = await Professor.find({});
    res.send(professors);
  } catch (e) {
    next(e);
  }
});

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

module.exports = router;
