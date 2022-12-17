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
      if (state.transactions.length > 0) {
        let tempTxList = [...state.transactions];
        tempTxList = tempTxList.map((t) => {
          if (t.txCode === action.payload.transaction.txCode) {
            return action.payload.transaction;
          }
          return t;
        });
        state.transactions = [...tempTxList];
      }
    },
    removeTransaction(state, action) {
      state.transactions = state.transactions.filter(
        (t) => t.txCode !== action.payload.txCode
      );
    },
    resetState(state) {
      state.transactions = [];
      state.selectedTransaction = null;
    },
  },
});

export default transactionSlice.reducer;
export const transactionAction = transactionSlice.actions;
