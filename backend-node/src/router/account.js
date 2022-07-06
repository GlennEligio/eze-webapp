const express = require("express");
const Account = require("../model/account");
const ApiError = require("../error/ApiError");

const router = express.Router();

router.get("/accounts", async (req, res, next) => {
  try {
    const users = await Account.find({});
    res.send(users);
  } catch (e) {
    next(e);
  }
});

router.get("/accounts/:id", async (req, res, next) => {
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

router.post("/accounts", async (req, res, next) => {
  try {
    const user = new Account(req.body);
    await user.save();
    return res.status(201).send(user);
  } catch (e) {
    next(e);
  }
});

// TODO: template for centralized error handling
router.patch("/accounts/:id", async (req, res, next) => {
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

router.delete("/accounts/:id", async (req, res, next) => {
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

module.exports = router;
