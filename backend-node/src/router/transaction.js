const express = require("express");
const XLSX = require("xlsx");
const Transaction = require("../model/transaction");
const ApiError = require("../error/ApiError");
const { uploadExcel } = require("../middleware/file");

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

router.get("/download", async (req, res, next) => {
  try {
    const transactions = await Transaction.find({});
    const transactionsJSON = JSON.stringify(transactions);
    const transactionsObj = JSON.parse(transactionsJSON);
    const transformedTransactions = transactionsObj.map((transaction) => {
      for (const equipment of transaction.equipments) {
        transaction[equipment.equipment] = equipment.amount;
      }
      delete transaction.equipments;
      return transaction;
    });

    const worksheet = XLSX.utils.json_to_sheet(transformedTransactions);
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, "Transactions");

    const buffer = XLSX.write(workbook, { type: "buffer", bookType: "xlsx" });
    res.set("Content-Disposition", "attachment; filename=transactions.xlsx");
    res.set("Content-Type", "application/octet-stream");
    res.send(buffer);
  } catch (e) {
    next(e);
  }
});

router.post("/upload", uploadExcel.single("excel"), async (req, res, next) => {
  try {
    const excelBuffer = req.file.buffer;
    const workbook = XLSX.read(excelBuffer, { type: "buffer" });
    const transactionsJson = XLSX.utils.sheet_to_json(
      workbook.Sheets.Transactions
    );
    const transformedTransactions = transactionsJson.map((transaction) => {
      let newTransaction = {
        borrower: transaction.borrower,
        professor: transaction.professor,
        borrowedAt: transaction.borrowedAt,
        returnedAt: transaction.returnedAt,
        status: transaction.status,
        equipments: [],
      };
      delete transaction.borrower;
      delete transaction.professor;
      delete transaction.borrowedAt;
      delete transaction.returnedAt;
      delete transaction.status;
      delete transaction._id;

      const equipmentsId = Object.keys(transaction);
      for (const id of equipmentsId) {
        newTransaction.equipments.push({
          equipment: id,
          amount: transaction[id],
        });
      }
      return newTransaction;
    });
    for (const transaction of transformedTransactions) {
      const transactionToSave = new Transaction(transaction);
      await transactionToSave.save();
    }
    res.send();
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
