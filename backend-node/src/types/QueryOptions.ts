import { IIndexable } from "./IIndexable";

export interface QueryOptions extends IIndexable {
  limit?: number;
  skip?: number;
  sort?: IIndexable;
}
