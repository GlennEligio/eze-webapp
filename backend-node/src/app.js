const express = require("express");
require("./db/mongoose");
const accountRouter = require("./router/account");
const equipmentRouter = require("./router/equipment");
const studentRouter = require("./router/student");
const professorRouter = require("./router/professor");

const app = express();

app.use(express.json());
app.use(accountRouter);
app.use(equipmentRouter);
app.use(studentRouter);
app.use(professorRouter);

module.exports = app;
