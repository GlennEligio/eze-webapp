const jwt = require("jsonwebtoken");
const Account = require("../model/account");
const ApiError = require("../error/ApiError");

const authMiddleware = async (req, res, next) => {
  try {
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
    next();
  } catch (e) {
    next(e);
  }
};

module.exports = authMiddleware;
