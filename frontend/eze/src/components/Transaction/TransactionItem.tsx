import React from "react";
import { Transaction } from "../../api/TransactionService";

interface TransactionItemProps {
  transaction: Transaction;
  key: React.Key;
  onTransactionItemClick: (transaction: Transaction) => void;
  focused: boolean;
  "data-bs-toggle": string;
  "data-bs-target": string;
}

const TransactionItem: React.FC<TransactionItemProps> = (props) => {
  return (
    <tr
      onClick={() => props.onTransactionItemClick(props.transaction)}
      className={props.focused ? `table-active` : ""}
      data-bs-toggle={props["data-bs-toggle"]}
      data-bs-target={props["data-bs-target"]}
    >
      <td>{props.transaction.txCode}</td>
      <td>{props.transaction.borrower}</td>
      <td>{props.transaction.yearAndSection}</td>
      <td>{props.transaction.equipmentsCount}</td>
      <td>{props.transaction.professor}</td>
      <td>{props.transaction.borrowedAt}</td>
      <td>{props.transaction.returnedAt}</td>
      <td>{props.transaction.status}</td>
    </tr>
  );
};

export default TransactionItem;
