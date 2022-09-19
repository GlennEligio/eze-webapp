import { FC } from "react";
import { Account } from "../../models/Account";

interface AccountItemProps {
  account: Account;
}

const AccountItem: FC<AccountItemProps> = (props) => {
  return (
    <tr>
      <td>{props.account.fullname}</td>
      <td>{props.account.username}</td>
      <td>{props.account.type}</td>
      <td>{props.account.createdAt}</td>
      <td>{props.account.email}</td>
    </tr>
  );
};

export default AccountItem;
