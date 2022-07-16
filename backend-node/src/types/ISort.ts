import { IIndexable } from "./IIndexable";

export interface ISort extends IIndexable {
  limit?: number;
  skip?: number;
  sortBy?: string;
}
