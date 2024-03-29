import mongoose from "mongoose";
import validator from "validator";
import bcrypt from "bcryptjs";
import jwt from "jsonwebtoken";
import ApiError from "../error/ApiError";
import { IIndexable } from "../types/IIndexable";

interface Token {
  _id: mongoose.Types.ObjectId;
  token: string;
}

export interface IAccount extends IIndexable {
  fullname: string;
  username: string;
  email: string;
  password: string;
  type?: string;
  profile?: Buffer;
  tokens?: Token[];
  createdAt?: Date;
  updatedAt?: Date;
}

interface IAccountMethods {
  generateToken(): Promise<string>;
  toJSON(): string;
}

export interface AccountModel
  extends mongoose.Model<IAccount, {}, IAccountMethods> {
  findCredentials: (
    username: string,
    password: string
  ) => Promise<mongoose.Document<unknown, any, IAccount> & IAccount>;
}

const accountSchema = new mongoose.Schema<
  IAccount,
  AccountModel,
  IAccountMethods
>(
  {
    fullname: {
      type: String,
      required: true,
    },
    username: {
      type: String,
      required: true,
      trim: true,
      unique: true,
    },
    email: {
      type: String,
      required: true,
      trim: true,
      unique: true,
      validate(value: string) {
        if (!validator.isEmail(value)) {
          throw new Error("Email is invalid");
        }
        return true;
      },
    },
    password: {
      type: String,
      required: true,
      trim: true,
      minLength: 7,
    },
    type: {
      type: String,
      required: true,
      default: "USER",
    },
    profile: {
      type: Buffer,
    },
    tokens: [
      {
        token: {
          type: String,
          trim: true,
        },
      },
    ],
  },
  {
    timestamps: true,
  }
);

accountSchema.pre("save", async function (next) {
  const account = this;

  if (account.isModified("password")) {
    account.password = await bcrypt.hash(account.password, 8);
  }

  next();
});

accountSchema.statics.findCredentials = async (username, password) => {
  const account = await Account.findOne({ username: username });
  if (!account) {
    throw new ApiError(401, "No account was found");
  }

  const isMatch = await bcrypt.compare(password, account.password);
  if (!isMatch) {
    throw new ApiError(401, "Incorrect username/password");
  }

  return account;
};

accountSchema.methods.generateToken = async function () {
  const account = this;

  const token = jwt.sign(
    { _id: account._id.toString() },
    process.env.JWT_SECRET_KEY!,
    {
      expiresIn: "2 days",
    }
  );

  account.tokens = account.tokens.concat({ token });
  await account.save();

  return token;
};

accountSchema.methods.toJSON = function () {
  const account = this;
  const accountObj = account.toObject();

  delete accountObj.password;
  delete accountObj.tokens;
  delete accountObj.__v;
  delete accountObj.profile;
  return accountObj;
};

const Account = mongoose.model<IAccount, AccountModel>(
  "Account",
  accountSchema
);

export default Account;
