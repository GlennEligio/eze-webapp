import { createSlice } from "@reduxjs/toolkit";
import { Transaction } from "../api/TransactionService";

export interface TransactionState {
  transactions: Transaction[];
  selectedTransaction: Transaction | null;
}

const INITIAL_STATE: TransactionState = {
  transactions: [],
  selectedTransaction: null,
};

const transactionSlice = createSlice({
  name: "transaction",
  initialState: INITIAL_STATE,
  reducers: {
    addTransactions(state, action) {
      state.transactions = [...action.payload.transactions];
    },
    updateSelectedTransaction(state, action) {
      state.selectedTransaction = action.payload.selectedTransaction;
    },
    addTransaction(state, action) {
      state.transactions = [
        ...state.transactions,
        action.payload.newTransaction,
      ];
    },
    updateTransaction(state, action) {
      state.transactions = state.transactions.map((t) => {
        if (t.txCode === action.payload.transaction.txCode) {
          return action.payload.transaction;
        }
        return t;
      });
    },
    removeTransaction(state, action) {
      state.transactions = state.transactions.filter(
        (t) => t.txCode !== action.payload.txCode
      );
    },
  },
});

export default transactionSlice.reducer;
export const transactionAction = transactionSlice.actions;
