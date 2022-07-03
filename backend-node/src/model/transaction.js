const mongoose = require("mongoose");

const transactionSchema = new mongoose.Schema({
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
    type: Date,
  },
  returnedAt: {
    type: Date,
  },
});

const Transaction = mongoose.model("Transaction", transactionSchema);

module.exports = Transaction;
