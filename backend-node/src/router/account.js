const express = require("express");
const Account = require("../model/account");

const router = express.Router();

router.get("/accounts", async (req, res) => {
  try {
    const users = await Account.find({});

    res.send(users);
  } catch (e) {
    res.status(500).send(e);
  }
});

router.get("/accounts/:id", async (req, res) => {
  try {
    const user = await Account.findById(req.params.id);
    if (!user) {
      res.status(404).send();
      return;
    }

    res.send(user);
  } catch (e) {
    res.status(500).send(e);
  }
});

router.post("/accounts", async (req, res) => {
  const user = new Account(req.body);
  try {
    await user.save();
    return res.status(201).send(user);
  } catch (error) {
    res.status(400).send();
  }
});

router.patch("/accounts/:id", async (req, res) => {
  const updates = Object.keys(req.body);
  const allowedUpdates = ["fullname", "username", "email", "password", "type"];
  let isValidUpdate = updates.every((update) =>
    allowedUpdates.includes(update)
  );

  if (!isValidUpdate) {
    res.status(400).send({
      error: "Update object includes properties that is not allowed",
    });
    return;
  }

  try {
    const accountToUpdate = await Account.findById(req.params.id);
    if (!accountToUpdate) {
      res.status(404).send();
      return;
    }

    updates.forEach((update) => (accountToUpdate[update] = req.body[update]));
    await accountToUpdate.save();
    res.send();
  } catch (error) {
    res.status(400).send();
  }
});

router.delete("/accounts/:id", async (req, res) => {
  try {
    const account = await Account.findByIdAndDelete(req.params.id);
    if (!account) {
      res.send(404).send();
      return;
    }
    res.send(account);
  } catch (error) {
    res.status(500).send(error);
  }
});

module.exports = router;
