import express from "express";
import { HydratedDocument } from "mongoose";
import { IAccount } from "../model/account";

export interface CustomRequest extends express.Request {
  file?: Express.Multer.File;
  account?: HydratedDocument<IAccount>;
  token?: string;
}
