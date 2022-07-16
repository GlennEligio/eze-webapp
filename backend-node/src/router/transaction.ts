import express from "express";
import XLSX from "xlsx";
import mongoose from "mongoose";
import Transaction, { ITransaction } from "../model/transaction";
import ApiError from "../error/ApiError";
import { ISort } from "../types/ISort";
import { uploadExcel } from "../middleware/file";
import { ITransformedTransaction } from "../types/ITransformedTransaction";
import { CustomRequest } from "../types/CustomRequest";

const router = express.Router();

router.get("/", async (req, res, next) => {
  try {
    const match = { ...req.query };
    delete match.limit;
    delete match.skip;
    delete match.sortBy;
    const sort: ISort = {
      sortBy: req.query.sortBy as string,
      limit: parseInt(req.query.limit as string),
      skip: parseInt(req.query.skip as string),
    };

    if (sort.sortBy) {
      const sortOption = sort.sortBy.split(":");
      sort[sortOption[0]] = sortOption[1] === "desc" ? -1 : 1;
    }

    const transactions = await Transaction.find(match)
      .setOptions(sort)
      .populate({ path: "equipments.equipment", select: "name" })
      .populate({ path: "professor", select: "name" })
      .populate({ path: "borrower", select: "fullname" });
    res.send(transactions);
  } catch (e) {
    next(e);
  }
});

router.get("/download", async (_req, res, next) => {
  try {
    const transactions = await Transaction.find({});
    const transactionsJSON = JSON.stringify(transactions);
    const transactionsObj: ITransaction[] = JSON.parse(transactionsJSON);
    const transformedTransactions: ITransformedTransaction[] =
      transactionsObj.map((transaction) => {
        let newTransaction: ITransformedTransaction = {};
        newTransaction.borrowedAt = transaction.borrowedAt;
        newTransaction.borrower = transaction.borrower.toHexString();
        newTransaction.professor = transaction.professor.toHexString();
        newTransaction.returnedAt = transaction.returnedAt;
        newTransaction.status = transaction.status;
        for (const equipment of transaction.equipments!) {
          newTransaction[equipment.equipment.toHexString()] = equipment.amount;
        }
        return newTransaction;
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

router.post(
  "/upload",
  uploadExcel.single("excel"),
  async (req: CustomRequest, res, next) => {
    try {
      const excelBuffer = req.file!.buffer;
      const workbook = XLSX.read(excelBuffer, { type: "buffer" });
      const transactionsJson: ITransformedTransaction[] =
        XLSX.utils.sheet_to_json(workbook.Sheets.Transactions);
      const transformedTransactions = transactionsJson.map((transaction) => {
        let newTransaction: Partial<ITransaction> = {
          borrower: new mongoose.Types.ObjectId(transaction.borrower),
          professor: new mongoose.Types.ObjectId(transaction.professor),
          borrowedAt: transaction.borrowedAt,
          returnedAt: transaction.returnedAt,
          status: transaction.status,
        };
        delete transaction.borrower;
        delete transaction.professor;
        delete transaction.borrowedAt;
        delete transaction.returnedAt;
        delete transaction.status;
        delete transaction._id;

        const equipmentsId = Object.keys(transaction);
        for (const id of equipmentsId) {
          newTransaction.equipments!.push({
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
  }
);

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
  } catch (e) {
    next(e);
  }
});

export default router;
