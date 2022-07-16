import express from "express";
import "./db/mongoose";
import accountRouter from "./router/account";
import equipmentRouter from "./router/equipment";
import studentRouter from "./router/student";
import professorRouter from "./router/professor";
import transactionRouter from "./router/transaction";
import scheduleRouter from "./router/schedule";
import errorHandler from "./middleware/errorHandler";
import authMiddleware from "./middleware/jwtAuth";
import typeMiddleware from "./middleware/typeAuth";

const app = express();

app.use(express.json());
app.use(authMiddleware);
app.use(typeMiddleware);
app.use("/api/accounts", accountRouter);
app.use("/api/equipments", equipmentRouter);
app.use("/api/students", studentRouter);
app.use("/api/professors", professorRouter);
app.use("/api/transactions", transactionRouter);
app.use("/api/schedules", scheduleRouter);
app.use(errorHandler);

export default app;
