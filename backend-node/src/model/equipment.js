const mongoose = require("mongoose");

const equipmentSchema = new mongoose.Schema({
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

const Equipment = mongoose.model("Equipment", equipmentSchema);

module.exports = Equipment;
