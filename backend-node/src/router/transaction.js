const express = require("express");
const Transaction = require("../model/transaction");

const router = express.Router();

router.get("/transactions", async (req, res) => {
  try {
    const transactions = await Transaction.find({})
      .populate({ path: "equipments.equipment", select: "name" })
      .populate({ path: "professor", select: "name" })
      .populate({ path: "borrower", select: "fullname" });
    res.send(transactions);
  } catch (error) {
    console.log(error);
    res.status(500).send(error);
  }
});

router.get("/transactions/:id", async (req, res) => {
  try {
    const transaction = await Transaction.findById(req.params.id)
      .populate({ path: "equipments.equipment", select: "name" })
      .populate({ path: "professor", select: "name" })
      .populate({ path: "borrower", select: "fullname" });
    if (!transaction) {
      res.status(404).send();
      return;
    }
    res.send(transaction);
  } catch (error) {
    res.status(500).send();
  }
});

router.post("/transactions", async (req, res) => {
  try {
    const transaction = new Transaction(req.body);

    await transaction.save();
    res.send(transaction);
  } catch (error) {
    res.status(500).send(error);
  }
});

router.patch("/transactions/:id", async (req, res) => {
  try {
    const updates = Object.keys(req.body);
    console.log(updates);
    const allowedUpdates = [
      "equipments",
      "borrower",
      "professor",
      "borrowedAt",
      "returnedAt",
    ];
    const isValidUpdate = updates.every((update) =>
      allowedUpdates.includes(update)
    );

    if (!isValidUpdate) {
      res
        .status(400)
        .send("Update object contains properties that is not allowed");
      return;
    }
    const transaction = await Transaction.findById(req.params.id);

    updates.forEach((update) => (transaction[update] = req.body[update]));

    await transaction.save();
    res.send(transaction);
  } catch (error) {
    res.status(500).send(error);
  }
});

router.delete("/transactions/:id", async (req, res) => {
  try {
    const transaction = await Transaction.findByIdAndDelete(req.params.id);
    if (!transaction) {
      res.status(404).send();
      return;
    }
    res.send(transaction);
  } catch (error) {
    res.status(500).send(error);
  }
});

module.exports = router;
