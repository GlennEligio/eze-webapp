import { IIndexable } from "./IIndexable";

export interface ITransformedTransaction extends IIndexable {
  borrower?: string;
  professor?: string;
  borrowedAt?: Date;
  returnedAt?: Date;
  status?: string;
}
