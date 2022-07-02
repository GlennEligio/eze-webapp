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

const Equipment = mongoose.model("Equipment", equipmentSchema);

module.exports = Equipment;
