import express, { Request, Response, NextFunction } from "express";

const router = express.Router();

/**
 * @openapi
 * /health:
 *  get:
 *     tags:
 *     - Checks
 *     description: Responds if the app is up and running
 *     responses:
 *       200:
 *         description: App is up and running
 */
router.get(
  "/health",
  async (_req: Request, res: Response, next: NextFunction) => {
    try {
      res.status(200).send();
    } catch (e) {
      next(e);
    }
  }
);

export default router;
