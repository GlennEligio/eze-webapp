import jwt from "jsonwebtoken";
import Account from "../model/account";
import ApiError from "../error/ApiError";
import express from "express";
import { CustomRequest } from "../types/CustomRequest";

const jwtAuthMiddleware: express.RequestHandler = async (
  req: CustomRequest,
  _res,
  next
) => {
  try {
    const path = req.originalUrl;
    if (path === "/api/accounts/register" || path === "/api/accounts/login") {
      req.account = new Account({
        fullname: "*",
        username: "*",
        password: "*",
        email: "anonymous@gmail.com",
        type: "*",
      });
      next();
      return;
    }

    const authHeader = req.get("Authorization");
    if (!authHeader) {
      throw new ApiError(401, "No Authentication Scheme present");
    }
    const parts = authHeader.split(" ");
    if (parts[0] !== "Bearer") {
      throw new ApiError(401, "Invalid Authentication scheme");
    }

    const payload = jwt.verify(parts[1], process.env.JWT_SECRET_KEY!) as {
      _id: string;
    };
    const account = await Account.findOne({
      _id: payload._id,
      "tokens.token": parts[1],
    });
    if (!account) {
      throw new ApiError(401, "Invalid authentication");
    }
    req.account = account;
    req.token = parts[1];
    next();
  } catch (e) {
    next(e);
  }
};

export default jwtAuthMiddleware;
