import express from "express";
import XLSX from "xlsx";
import sharp from "sharp";
import Account, { IAccount } from "../model/account";
import ApiError from "../error/ApiError";
import { uploadExcel, uploadImage } from "../middleware/file";
import { CustomRequest } from "../types/CustomRequest";

const router = express.Router();

router.get("/download", async (_req, res, next) => {
  try {
    const accounts = await Account.find({});
    const sanitizedAccountsJSON = JSON.stringify(accounts);
    const sanitizedAccounts = JSON.parse(sanitizedAccountsJSON);

    const worksheet = XLSX.utils.json_to_sheet(sanitizedAccounts);
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, "Accounts");

    const buffer = XLSX.write(workbook, { type: "buffer", bookType: "xlsx" });
    res.set("Content-Disposition", "attachment; filename=accounts.xlsx");
    res.set("Content-Type", "application/octet-stream");
    res.send(buffer);
  } catch (e) {
    next(e);
  }
});

// NOTE: Account excel must have fullname, username, email, password, and type column
router.post(
  "/upload/excel",
  uploadExcel.single("excel"),
  async (req: CustomRequest, res, next) => {
    try {
      const excelBuffer = req.file!.buffer;
      const workbook = XLSX.read(excelBuffer, { type: "buffer" });
      const accountsJson = XLSX.utils.sheet_to_json(workbook.Sheets.Accounts);
      for (const accountJson of accountsJson) {
        const account = new Account(accountJson);
        await account.save();
      }
      res.send();
    } catch (e) {
      next(e);
    }
  }
);

router.post(
  "/me/avatar",
  uploadImage.single("avatar"),
  async (req: CustomRequest, res, next) => {
    try {
      const avatarBuffer = await sharp(req.file!.buffer).png().toBuffer();
      const account = req.account;
      if (!account) {
        throw new ApiError(404, "No account with same id was found");
      }
      account.profile = avatarBuffer;
      await account.save();
      res.send();
    } catch (e) {
      next(e);
    }
  }
);

router.post(
  "/:id/avatar",
  uploadImage.single("avatar"),
  async (req: CustomRequest, res, next) => {
    try {
      const avatarBuffer = await sharp(req.file!.buffer).png().toBuffer();
      const account = await Account.findById(req.params.id);
      if (!account) {
        throw new ApiError(404, "No account with same id was found");
      }
      account.profile = avatarBuffer;
      await account.save();
      res.send();
    } catch (e) {
      next(e);
    }
  }
);

router.get("/me/avatar", async (req: CustomRequest, res, next) => {
  try {
    const account = req.account;
    if (!account) {
      throw new ApiError(404, "No account with same id was found");
    }
    res.set("Content-Type", "image/png");
    res.send(account.profile);
  } catch (e) {
    next(e);
  }
});

router.get("/:id/avatar", async (req, res, next) => {
  try {
    const account = await Account.findById(req.params.id);
    if (!account) {
      throw new ApiError(404, "No account with same id was found");
    }
    res.set("Content-Type", "image/png");
    res.send(account.profile);
  } catch (e) {
    next(e);
  }
});

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

router.patch("/me", async (req: CustomRequest, res, next) => {
  try {
    const updatedAccount = req.body as { [prop in keyof IAccount]: any };
    const updates = Object.keys(updatedAccount);
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

    const accountToUpdate = req.account!;
    updates.forEach(
      (update) => (accountToUpdate[update] = updatedAccount[update])
    );
    await accountToUpdate.save();
    res.send();
  } catch (e) {
    next(e);
  }
});

router.patch("/:id", async (req, res, next) => {
  try {
    const updatedAccount = req.body as { [prop in keyof IAccount]: any };
    const updates = Object.keys(updatedAccount);
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

    updates.forEach((update) => {
      accountToUpdate[update] = updatedAccount[update];
    });
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

router.post("/logout", async (req: CustomRequest, res, next) => {
  try {
    const account = req.account!;
    const accountToken = req.token!;
    account.tokens = account.tokens!.filter(
      (token) => token.token !== accountToken
    );
    await account.save();
    res.send();
  } catch (e) {
    next(e);
  }
});

router.post("/logoutAll", async (req: CustomRequest, res, next) => {
  try {
    const account = req.account!;
    account.tokens = [];
    await account.save();
    res.send();
  } catch (e) {
    next(e);
  }
});

export default router;
