import { createSlice } from "@reduxjs/toolkit";
import { Account } from "../api/AccountService";

export interface AccountState {
  accounts: Account[];
  selectedAccount: Account | null;
}

const INITIAL_STATE: AccountState = {
  accounts: [],
  selectedAccount: null,
};

const accountSlice = createSlice({
  name: "account",
  initialState: INITIAL_STATE,
  reducers: {
    addAccounts(state, action) {
      state.accounts = action.payload.accounts;
    },
    updateSelectedAccount(state, action) {
      state.selectedAccount = action.payload.selectedAccount;
    },
    addAccount(state, action) {
      state.accounts = [...state.accounts, action.payload.newAccount];
    },
    updateAccount(state, action) {
      state.accounts = state.accounts.map((a) => {
        if (a.username === action.payload.account.username) {
          return action.payload.account;
        }
        return a;
      });
    },
    removeAccount(state, action) {
      state.accounts = state.accounts.filter(
        (a) => a.username !== action.payload.username
      );
    },
  },
});

export default accountSlice.reducer;
export const accountActions = accountSlice.actions;
