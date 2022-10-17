import { FC } from "react";
import { Account } from "../../api/AccountService";

interface AccountItemProps {
  account: Account;
  focused: boolean;
  key: React.Key;
  onRowClick: (account: Account) => void;
}

const AccountItem: FC<AccountItemProps> = (props) => {
  return (
    <tr
      onClick={() => props.onRowClick(props.account)}
      className={`${props.focused && "table-active"}`}
    >
      <td>{props.account.id}</td>
      <td>{props.account.fullName}</td>
      <td>{props.account.username}</td>
      <td>{props.account.email}</td>
      <td>{props.account.type}</td>
      <td>{props.account.createdAt}</td>
      <td>{props.account.active ? "YES" : "NOD"}</td>
    </tr>
  );
};

export default AccountItem;
