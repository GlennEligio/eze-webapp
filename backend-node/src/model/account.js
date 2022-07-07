const mongoose = require("mongoose");
const validator = require("validator");
const bcrypt = require("bcryptjs");
const jwt = require("jsonwebtoken");
const ApiError = require("../error/ApiError");

const accountSchema = new mongoose.Schema(
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
      validate(value) {
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
    process.env.JWT_SECRET_KEY,
    {
      expiresIn: "2 day",
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
  return accountObj;
};

const Account = mongoose.model("Account", accountSchema);

module.exports = Account;
