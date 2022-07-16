import mongoose, { HydratedDocument } from "mongoose";
import { IIndexable } from "../types/IIndexable";

export interface IEquipment extends IIndexable {
  name: string;
  barcode: string;
  status?: string;
  defectiveSince?: Date;
}

const equipmentSchema = new mongoose.Schema<IEquipment>({
  name: {
    type: String,
    require: true,
    trim: true,
  },
  barcode: {
    type: String,
    trim: true,
    unique: true,
  },
  status: {
    type: String,
    trim: true,
  },
  defectiveSince: {
    type: Date,
  },
});

equipmentSchema.methods.toJSON = function () {
  const equipment = this;
  const equipmentObj = equipment.toObject();
  delete equipmentObj.__v;
  return equipmentObj;
};

const Equipment = mongoose.model<IEquipment>("Equipment", equipmentSchema);

export default Equipment;
