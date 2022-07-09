const express = require("express");
require("./db/mongoose");
const accountRouter = require("./router/account");
const equipmentRouter = require("./router/equipment");
const studentRouter = require("./router/student");
const professorRouter = require("./router/professor");
const transactionRouter = require("./router/transaction");
const scheduleRouter = require("./router/schedule");
const errorHandler = require("./error/errorHandler");
const auth = require("./middleware/jwtAuth");
const type = require("./middleware/typeAuth");

const app = express();

app.use(express.json());
app.use(auth);
app.use(type);
app.use("/api/accounts", accountRouter);
app.use("/api/equipments", equipmentRouter);
app.use("/api/students", studentRouter);
app.use("/api/professors", professorRouter);
app.use("/api/transactions", transactionRouter);
app.use("/api/schedules", scheduleRouter);
app.use(errorHandler);

module.exports = app;
