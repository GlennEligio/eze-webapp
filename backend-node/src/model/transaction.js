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

const Transaction = mongoose.model("Transaction", transactionSchema);

module.exports = Transaction;
