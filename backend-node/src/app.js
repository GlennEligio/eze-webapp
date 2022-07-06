const express = require("express");
require("./db/mongoose");
const accountRouter = require("./router/account");
const equipmentRouter = require("./router/equipment");
const studentRouter = require("./router/student");
const professorRouter = require("./router/professor");
const transactionRouter = require("./router/transaction");
const scheduleRouter = require("./router/schedule");
const errorHandler = require("./error/errorHandler");

const app = express();

app.use(express.json());
app.use(accountRouter);
app.use(equipmentRouter);
app.use(studentRouter);
app.use(professorRouter);
app.use(transactionRouter);
app.use(scheduleRouter);
app.use(errorHandler);

module.exports = app;
