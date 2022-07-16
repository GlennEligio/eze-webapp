import mongoose from "mongoose";
import { IIndexable } from "../types/IIndexable";

export interface Equipment {
  equipment: mongoose.Types.ObjectId;
  amount: number;
}

export interface ITransaction extends IIndexable {
  equipments: mongoose.Types.DocumentArray<Equipment>;
  borrower: mongoose.Types.ObjectId;
  professor: mongoose.Types.ObjectId;
  borrowedAt: Date;
  returnedAt?: Date;
  status: string;
}

const transactionSchema = new mongoose.Schema<ITransaction>({
  equipments: [
    {
      equipment: {
        type: mongoose.Schema.Types.ObjectId,
        required: true,
        ref: "Equipment",
      },
      amount: {
        type: Number,
        required: true,
        min: 1,
      },
    },
  ],
  borrower: {
    type: mongoose.Schema.Types.ObjectId,
    required: true,
    ref: "Student",
  },
  professor: {
    type: mongoose.Schema.Types.ObjectId,
    required: true,
    ref: "Professor",
  },
  borrowedAt: {
    required: true,
    type: Date,
  },
  returnedAt: {
    type: Date,
  },
  status: {
    type: String,
    trim: true,
    required: true,
  },
});

transactionSchema.methods.toJSON = function () {
  const transaction = this;
  const transactionObj = transaction.toObject();
  delete transactionObj.__v;
  return transactionObj;
};

const Transaction = mongoose.model<ITransaction>(
  "Transaction",
  transactionSchema
);

export default Transaction;
