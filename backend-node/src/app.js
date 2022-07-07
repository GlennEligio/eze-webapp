const express = require("express");
require("./db/mongoose");
const accountRouter = require("./router/account");
const equipmentRouter = require("./router/equipment");
const studentRouter = require("./router/student");
const professorRouter = require("./router/professor");
const transactionRouter = require("./router/transaction");
const scheduleRouter = require("./router/schedule");
const errorHandler = require("./error/errorHandler");
const auth = require("./middleware/auth");

const app = express();

app.use(express.json());
app.use(auth);
app.use("/accounts", accountRouter);
app.use("/equipments", equipmentRouter);
app.use("/students", studentRouter);
app.use("/professors", professorRouter);
app.use("/transactions", transactionRouter);
app.use("/schedules", scheduleRouter);
app.use(errorHandler);

module.exports = app;
