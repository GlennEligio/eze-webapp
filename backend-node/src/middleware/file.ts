import multer from "multer";
import ApiError from "../error/ApiError";

export const uploadExcel = multer({
  limits: {
    fileSize: 15000000,
  },
  fileFilter(_req, file, cb) {
    if (!file.originalname.match(/\.(xlsx)$/)) {
      cb(new ApiError(400, "Can only use .xlsx file for excel uploads"));
      return;
    }

    cb(null, true);
  },
});

export const uploadImage = multer({
  limits: {
    fileSize: 1000000,
  },
  fileFilter(req, file, cb) {
    if (!file.originalname.match(/\.(png|jpg|jpeg)$/)) {
      cb(
        new ApiError(
          400,
          "Can upload images with extension .jpg, .jpeg, and .png"
        )
      );
      return;
    }
    cb(null, true);
  },
});
