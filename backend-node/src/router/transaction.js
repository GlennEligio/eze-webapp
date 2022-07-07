const express = require("express");
const Transaction = require("../model/transaction");
const ApiError = require("../error/ApiError");

const router = express.Router();

router.get("/", async (req, res, next) => {
  try {
    const match = { ...req.query };
    const sort = {};
    delete match.limit;
    delete match.skip;
    delete match.sortBy;

    if (req.query.sortBy) {
      const sortOption = req.query.sortBy.split(":");
      sort[sortOption[0]] = sortOption[1] === "desc" ? -1 : 1;
    }

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
  } catch (e) {
    next(e);
  }
});

router.get("/:id", async (req, res, next) => {
  try {
    const transaction = await Transaction.findById(req.params.id)
      .populate({ path: "equipments.equipment", select: "name" })
      .populate({ path: "professor", select: "name" })
      .populate({ path: "borrower", select: "fullname" });
    if (!transaction) {
      throw new ApiError(404, "No transaction with same id was found");
    }
    res.send(transaction);
  } catch (e) {
    next(e);
  }
});

router.post("/", async (req, res, next) => {
  try {
    const transaction = new Transaction(req.body);
    transaction.borrowedAt = new Date();

    await transaction.save();
    res.send(transaction);
  } catch (e) {
    next(e);
  }
});

router.patch("/:id", async (req, res, next) => {
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
      throw new ApiError(
        400,
        "Update object includes properties that is not allowed"
      );
    }
    const transaction = await Transaction.findById(req.params.id);

    if (!transaction) {
      throw new ApiError(404, "No transaction with same id was found");
    }

    updates.forEach((update) => (transaction[update] = req.body[update]));
    await transaction.save();
    res.send(transaction);
  } catch (e) {
    next(e);
  }
});

router.delete("/:id", async (req, res, next) => {
  try {
    const transaction = await Transaction.findByIdAndDelete(req.params.id);
    if (!transaction) {
      throw new ApiError(404, "No transaction with same id was found");
    }
    res.send(transaction);
  } catch (error) {
    next(e);
  }
});

module.exports = router;
