const jwt = require("jsonwebtoken");
const Account = require("../model/account");
const ApiError = require("../error/ApiError");

const jwtAuthMiddleware = async (req, res, next) => {
  try {
    const path = req.originalUrl;
    if (path === "/accounts/register" || path === "/accounts/login") {
      req.account = {
        type: "*",
      };
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

    const payload = jwt.verify(parts[1], process.env.JWT_SECRET_KEY);
    const account = await Account.findById(payload._id);
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

module.exports = jwtAuthMiddleware;
