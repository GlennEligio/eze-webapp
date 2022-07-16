import ApiError from "../error/ApiError";
import express from "express";

const errorHandler: express.ErrorRequestHandler = (error, req, res, _next) => {
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

export default errorHandler;
