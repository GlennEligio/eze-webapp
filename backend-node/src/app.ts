import express from "express";
import "./db/mongoose";
import accountRouter from "./router/account";
import equipmentRouter from "./router/equipment";
import studentRouter from "./router/student";
import professorRouter from "./router/professor";
import transactionRouter from "./router/transaction";
import scheduleRouter from "./router/schedule";
import checksRouter from "./router/checks";
import errorHandler from "./middleware/errorHandler";
import authMiddleware from "./middleware/jwtAuth";
import typeMiddleware from "./middleware/typeAuth";
import swaggerDocs from "./utils/swagger";
import cors from "cors";

const port = parseInt(process.env.PORT as string);
const app = express();

app.use(cors());
app.use(express.json());
app.use(checksRouter);
// serves Swagger UI page
swaggerDocs(app, port as number);
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
