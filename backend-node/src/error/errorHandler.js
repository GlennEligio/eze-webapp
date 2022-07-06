const ApiError = require("./ApiError");

const errorHandler = (error, req, res, next) => {
  if (error instanceof ApiError) {
    res.status(error.httpCode).send({
      code: error.httpCode,
      message: error.message,
      path: req.originalUrl,
    });
    return;
  }
  res.status(500).send({
    code: 500,
    message: error.message,
    path: req.originalUrl,
  });
};

module.exports = errorHandler;
