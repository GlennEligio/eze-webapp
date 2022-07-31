import express from "express";
import XLSX from "xlsx";
import sharp from "sharp";
import Account, { IAccount } from "../model/account";
import ApiError from "../error/ApiError";
import { uploadExcel, uploadImage } from "../middleware/file";
import { CustomRequest } from "../types/CustomRequest";

const router = express.Router();

/**
 * @openapi
 * '/api/accounts/download':
 *  get:
 *    tags:
 *    - Account
 *    summary: Download Accounts in excel file
 *    responses:
 *      200:
 *        description: Succesfully downloaded the account.xlsx file
 *        content:
 *          application/octet-stream:
 *            schema:
 *              type: string
 *              format: binary
 */
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

/**
 * @openapi
 * '/api/accounts/upload':
 *  post:
 *    tags:
 *    - Account
 *    summary: Upload excel file
 *    requestBody:
 *      content:
 *        multipart/form-data:
 *          schema:
 *            type: object
 *            properties:
 *              excel:
 *                type: string
 *                format: base64
 *          encoding:
 *            excel:
 *              contentType: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
 *    responses:
 *      200:
 *        description: Succesfully uploaded the account.xlsx file
 *      400:
 *        description: Bad request
 */
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

/**
 * @openapi
 * '/api/accounts/me/avatar':
 *  post:
 *    tags:
 *    - Account
 *    summary: Upload own avatar file
 *    requestBody:
 *      content:
 *        multipart/form-data:
 *          schema:
 *            type: object
 *            properties:
 *              avatar:
 *                type: string
 *                format: base64
 *          encoding:
 *            avatar:
 *              contentType: image/png, image/jpg, image/jpeg
 *    responses:
 *      200:
 *        description: Succesfully uploaded the student avatar image file
 *      400:
 *        description: Bad request
 */
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

/**
 * @openapi
 * '/api/accounts/{id}/avatar':
 *  post:
 *    tags:
 *    - Account
 *    summary: Upload account avatar file
 *    parameters:
 *      - name: id
 *        in: path
 *        required: true
 *        description: Id of account
 *        schema:
 *          type: string
 *    requestBody:
 *      content:
 *        multipart/form-data:
 *          schema:
 *            type: object
 *            properties:
 *              avatar:
 *                type: string
 *                format: base64
 *          encoding:
 *            avatar:
 *              contentType: image/png, image/jpg, image/jpeg
 *    responses:
 *      200:
 *        description: Succesfully uploaded the account avatar image file
 *      400:
 *        description: Bad request
 */
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

/**
 * @openapi
 * '/api/accounts/me/avatar':
 *  get:
 *    tags:
 *    - Account
 *    summary: Download own Accounts avatar image file
 *    responses:
 *      200:
 *        description: Succesfully downloaded own Account avatar file
 *        content:
 *          image/png:
 *            schema:
 *              type: string
 *              format: base64
 *      400:
 *        description: Bad request
 */
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

/**
 * @openapi
 * '/api/accounts/{id}/avatar':
 *  get:
 *    tags:
 *    - Account
 *    summary: Download own Accounts avatar image file
 *    parameters:
 *      - name: id
 *        description: Account id
 *        required: true
 *        in: path
 *        schema:
 *          type: string
 *    responses:
 *      200:
 *        description: Succesfully downloaded own Account avatar file
 *        content:
 *          image/png:
 *            schema:
 *              type: string
 *              format: base64
 *      400:
 *        description: Bad request
 */
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

/**
 * @openapi
 * '/api/accounts':
 *  get:
 *     tags:
 *     - Account
 *     summary: Get all Accounts
 *     responses:
 *       200:
 *         description: Successfully fetch all Accounts
 *         content:
 *           application/json:
 *             schema:
 *               type: array
 *               items:
 *                 $ref: '#/components/schemas/Account'
 */
router.get("/", async (req, res, next) => {
  try {
    const users = await Account.find({});
    res.send(users);
  } catch (e) {
    next(e);
  }
});

/**
 * @openapi
 * '/api/accounts/{id}':
 *   get:
 *     tags:
 *       - Account
 *     summary: Fetch a single Account using an id
 *     parameters:
 *       - name: id
 *         in: path
 *         required: true
 *         description: Id of the Account
 *         schema:
 *           type: string
 *     responses:
 *       200:
 *         description: An Account object
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/Account'
 *       404:
 *         description: Student not found
 */
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

/**
 * @openapi
 * '/api/accounts':
 *   post:
 *     tags:
 *       - Account
 *     summary: Create an Account object
 *     requestBody:
 *       description: Account to create
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/CreateAccountInput'
 *     responses:
 *       201:
 *         description: Successfully created an Account
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/Account'
 *       400:
 *         description: Bad request
 */
router.post("/", async (req, res, next) => {
  try {
    const user = new Account(req.body);
    await user.save();
    return res.status(201).send(user);
  } catch (e) {
    next(e);
  }
});

/**
 * @openapi
 * '/api/accounts/me':
 *   patch:
 *     tags:
 *       - Account
 *     summary: Update own Account information
 *     requestBody:
 *       description: Object for updating Account
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/UpdateAccountInput'
 *     responses:
 *       200:
 *         description: Successfully updated own Account
 *       400:
 *         description: Bad request
 *       404:
 *         description: Account not found
 */
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

/**
 * @openapi
 * '/api/accounts/{id}':
 *   patch:
 *     tags:
 *       - Account
 *     summary: Update an Account information
 *     parameters:
 *       - name: id
 *         in: path
 *         required: true
 *         description: Id of the Account
 *         schema:
 *           type: string
 *     requestBody:
 *       description: Object for updating Account
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/UpdateAccountInput'
 *     responses:
 *       200:
 *         description: Successfully updated an Account
 *       400:
 *         description: Bad request
 *       404:
 *         description: Account not found
 */
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

/**
 * @openapi
 * '/api/accounts/{id}':
 *   delete:
 *     tags:
 *       - Account
 *     summary: Delete an Account using id
 *     parameters:
 *       - name: id
 *         in: path
 *         required: true
 *         description: Id of the Account
 *         schema:
 *           type: string
 *     responses:
 *       200:
 *         description: Successfully deleted an Account
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/Account'
 *       404:
 *         description: Account not found
 */
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

/**
 * @openapi
 * '/api/accounts/register':
 *   post:
 *     tags:
 *       - Account
 *     summary: Register an Account object
 *     requestBody:
 *       description: Account to register
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/RegisterAccountInput'
 *     responses:
 *       201:
 *         description: Successfully registered an Account
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/Account'
 *       400:
 *         description: Bad request
 */
router.post("/register", async (req, res, next) => {
  try {
    const account = new Account(req.body);
    const newAccount = await account.save();
    res.status(201).send(newAccount);
  } catch (e) {
    next(e);
  }
});

/**
 * @openapi
 * '/api/accounts/login':
 *   post:
 *     tags:
 *       - Account
 *     summary: Login an Account object
 *     requestBody:
 *       description: Account to login
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/LoginAccountInput'
 *     responses:
 *       200:
 *         description: Successfully logged in an Account
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/Account'
 *       400:
 *         description: Bad request
 */
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

/**
 * @openapi
 * '/api/accounts/logout':
 *   post:
 *     tags:
 *       - Account
 *     summary: Logout an Account by removing a single token
 *     responses:
 *       200:
 *         description: Successfully logged out an Account
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/Account'
 *       400:
 *         description: Bad request
 */
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

/**
 * @openapi
 * '/api/accounts/logoutAll':
 *   post:
 *     tags:
 *       - Account
 *     summary: Logout an Account by removing ALL tokens
 *     responses:
 *       200:
 *         description: Successfully logged out all accounts
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/Account'
 *       400:
 *         description: Bad request
 */
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
