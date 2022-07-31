import express from "express";
import XLSX from "xlsx";
import mongoose from "mongoose";
import validator from "validator";
import Transaction, { ITransaction } from "../model/transaction";
import ApiError from "../error/ApiError";
import { QueryOptions } from "../types/QueryOptions";
import { uploadExcel } from "../middleware/file";
import { ITransformedTransaction } from "../types/ITransformedTransaction";
import { CustomRequest } from "../types/CustomRequest";

const router = express.Router();

/**
 * @openapi
 * '/api/transactions':
 *   get:
 *     tags:
 *       - Transaction
 *     summary: Get All Transactions
 *     responses:
 *       200:
 *         description: Successfully fetch all Transactions
 *         content:
 *           application/json:
 *             schema:
 *               type: array
 *               items:
 *                 $ref: '#/components/schemas/Transaction'
 */
router.get("/", async (req, res, next) => {
  try {
    const match = { ...req.query };
    delete match.limit;
    delete match.skip;
    delete match.sortBy;
    delete match.order;
    const query: QueryOptions = {};

    if (req.query.limit) {
      if (validator.isInt(req.query.limit as string)) {
        query.limit = parseInt(req.query.limit as string);
      }
    }

    if (req.query.skip) {
      if (validator.isInt(req.query.skip as string)) {
        query.skip = parseInt(req.query.skip as string);
      }
    }

    if (req.query.sortBy || req.query.order) {
      query.sort = {};
      query.sort[req.query.sortBy as string] =
        req.query.order?.toString().toLowerCase() === "desc" ? -1 : 1;
    }

    console.log(query);

    const transactions = await Transaction.find(match)
      .setOptions(query)
      .populate({ path: "equipments.equipment", select: "name" })
      .populate({ path: "professor", select: "name" })
      .populate({ path: "borrower", select: "fullname" });
    res.send(transactions);
  } catch (e) {
    next(e);
  }
});

/**
 * @openapi
 * '/api/transactions/download':
 *  get:
 *    tags:
 *    - Transaction
 *    summary: Download Transactions in excel file
 *    responses:
 *      200:
 *        description: Succesfully downloaded the transaction.xlsx file
 *        content:
 *          application/octet-stream:
 *            schema:
 *              type: string
 *              format: binary
 */
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

/**
 * @openapi
 * '/api/transactions/upload':
 *  post:
 *    tags:
 *    - Transaction
 *    summary: Upload excel file
 *    requestBody:
 *      content:
 *        multipart/form-data:
 *          schema:
 *            type: object
 *            properties:
 *              excel:
 *                type: string
 *                format: base64
 *          encoding:
 *            excel:
 *              contentType: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
 *    responses:
 *      200:
 *        description: Succesfully uploaded the transaction.xlsx file
 *      400:
 *        description: Bad request
 */
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

/**
 * @openapi
 * '/api/transactions/{id}':
 *   get:
 *     tags:
 *       - Transaction
 *     summary: Fetch a single Transaction using an id
 *     parameters:
 *       - name: id
 *         in: path
 *         required: true
 *         description: Id of the Transaction
 *         schema:
 *           type: string
 *     responses:
 *       200:
 *         description: An Transaction object
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/Transaction'
 *       404:
 *         description: Transaction not found
 */
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

/**
 * @openapi
 * '/api/transactions':
 *   post:
 *     tags:
 *       - Transaction
 *     summary: Create an Transaction object
 *     requestBody:
 *       description: Transaction to create
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/CreateTransactionInput'
 *     responses:
 *       201:
 *         description: Successfully created an Transaction
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/Transaction'
 *       400:
 *         description: Bad request
 */
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

/**
 * @openapi
 * '/api/transactions/{id}':
 *   patch:
 *     tags:
 *       - Transaction
 *     summary: Update an Transaction information
 *     parameters:
 *       - name: id
 *         in: path
 *         required: true
 *         description: Id of the Transaction
 *         schema:
 *           type: string
 *     requestBody:
 *       description: Object for updating Transaction
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/UpdateTransactionInput'
 *     responses:
 *       200:
 *         description: Successfully updated an Transaction
 *       400:
 *         description: Bad request
 *       404:
 *         description: Transaction not found
 */
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

/**
 * @openapi
 * '/api/transcations/{id}':
 *   delete:
 *     tags:
 *       - Transaction
 *     summary: Delete an Transaction using id
 *     parameters:
 *       - name: id
 *         in: path
 *         required: true
 *         description: Id of the Transaction
 *         schema:
 *           type: string
 *     responses:
 *       200:
 *         description: Successfully deleted an Transaction
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/Transaction'
 *       404:
 *         description: Transaction not found
 */
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
