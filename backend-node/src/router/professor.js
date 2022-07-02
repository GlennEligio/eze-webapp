const express = require("express");
const Professor = require("../model/professor");

const router = express.Router();

router.get("/professors", async (req, res) => {
  try {
    const professors = await Professor.find({});
    res.send(professors);
  } catch (error) {
    res.status(500).send(error);
  }
});

router.get("/professors/:id", async (req, res) => {
  try {
    const professor = await Professor.findById(req.params.id);
    if (!professor) {
      res.status(404).send();
      return;
    }
    res.send(professor);
  } catch (error) {
    res.status(500).send();
  }
});

router.post("/professor", async (req, res) => {
  try {
    const professor = new Professor(req.body);
    const newProf = await professor.save();
    res.status(201).send(newProf);
  } catch (error) {
    res.status(400).send(error);
  }
});

router.patch("/professor/:id", async (req, res) => {
  try {
    const updates = Object.keys(req.body);
    const allowedUpdates = ["name", "contactNumber"];
    const isValidUpdate = updates.every((update) =>
      allowedUpdates.includes(update)
    );

    if (!isValidUpdate) {
      res.status(400).send();
      return;
    }

    const professor = await Professor.findById(req.params.id);
    if (!professor) {
      res.status(404).send();
      return;
    }
    updates.forEach((update) => (professor[update] = req.body[update]));
    await professor.save();
    res.send(professor);
  } catch (error) {
    res.status(500).send(error);
  }
});

router.delete("/professor/:id", async (req, res) => {
  try {
    const professor = await Professor.findByIdAndDelete(req.params.id);
    if (!professor) {
      res.status(404).send();
      return;
    }
    res.send(professor);
  } catch (error) {
    res.status(500).send(error);
  }
});

module.exports = router;
