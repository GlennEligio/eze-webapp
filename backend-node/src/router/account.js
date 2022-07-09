const express = require("express");
const Account = require("../model/account");
const ApiError = require("../error/ApiError");

const router = express.Router();

router.get("/", async (req, res, next) => {
  try {
    const users = await Account.find({});
    res.send(users);
  } catch (e) {
    next(e);
  }
});

router.get("/:id", async (req, res, next) => {
  try {
    const user = await Account.findById(req.params.id);
    if (!user) {
      throw new ApiError(404, "No accounts found with specified id");
    }

    res.send(user);
  } catch (e) {
    next(e);
  }
});

router.post("/", async (req, res, next) => {
  try {
    const user = new Account(req.body);
    await user.save();
    return res.status(201).send(user);
  } catch (e) {
    next(e);
  }
});

router.patch("/:id", async (req, res, next) => {
  try {
    const updates = Object.keys(req.body);
    const allowedUpdates = [
      "fullname",
      "username",
      "email",
      "password",
      "type",
    ];
    let isValidUpdate = updates.every((update) =>
      allowedUpdates.includes(update)
    );

    if (!isValidUpdate) {
      throw new ApiError(
        400,
        "Update object includes properties that is not allowed"
      );
    }

    const accountToUpdate = await Account.findById(req.params.id);
    if (!accountToUpdate) {
      throw new ApiError(404, "No account found with specified id");
    }

    updates.forEach((update) => (accountToUpdate[update] = req.body[update]));
    await accountToUpdate.save();
    res.send();
  } catch (e) {
    next(e);
  }
});

router.delete("/:id", async (req, res, next) => {
  try {
    const account = await Account.findByIdAndDelete(req.params.id);
    if (!account) {
      throw new ApiError(404, "No account found with specified id");
    }
    res.send(account);
  } catch (e) {
    next(e);
  }
});

router.post("/register", async (req, res, next) => {
  try {
    const account = new Account(req.body);
    const newAccount = await account.save();
    res.status(201).send(newAccount);
  } catch (e) {
    next(e);
  }
});

router.post("/login", async (req, res, next) => {
  try {
    const JWT_SECRET = process.env.JWT_SECRET;
    const creds = req.body;
    if (!creds.username || !creds.password) {
      throw new ApiError(401, "Username or password is missing");
    }

    const account = await Account.findCredentials(
      creds.username,
      creds.password
    );
    const token = await account.generateToken();
    res.send({
      account,
      token,
    });
  } catch (e) {
    next(e);
  }
});

router.post("/logout", async (req, res, next) => {
  try {
    const account = req.account;
    const accountToken = req.token;
    account.tokens = account.tokens.filter(
      (token) => token.token !== accountToken
    );
    await account.save();
    res.send();
  } catch (e) {
    next(e);
  }
});

router.post("/logoutAll", async (req, res, next) => {
  try {
    const account = req.account;
    account.tokens = [];
    await account.save();
    res.send();
  } catch (e) {
    next(e);
  }
});

module.exports = router;
