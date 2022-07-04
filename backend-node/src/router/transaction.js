const express = require("express");
const { ObjectId } = require("mongodb");
const Transaction = require("../model/transaction");

const router = express.Router();

// GET /transactions?status=pending
// GET /transactions?limit=2&skip=3
// GET /transactions?sortBy=createdAt:desc
router.get("/transactions", async (req, res) => {
  const match = { ...req.query };
  const sort = {};
  delete match.limit;
  delete match.skip;
  delete match.sortBy;

  if (req.query.sortBy) {
    const sortOption = req.query.sortBy.split(":");
    sort[sortOption[0]] = sortOption[1] === "desc" ? -1 : 1;
  }

  try {
    const transactions = await Transaction.find(match)
      .setOptions({
        sort,
        limit: req.query.limit,
        skip: req.query.skip,
      })
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
    transaction.borrowedAt = new Date();

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
