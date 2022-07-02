const mongoose = require("mongoose");

const transactionSchema = new mongoose.Schema({
  equipments: [
    {
      equipment: {
        type: mongoose.Schema.Types.ObjectId,
        required: true,
        ref: "Equipment",
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
    required: true,
  },
  returnedAt: {
    type: Date,
  },
});

const Transaction = mongoose.model("Transaction", transactionSchema);

module.exports = Transaction;
