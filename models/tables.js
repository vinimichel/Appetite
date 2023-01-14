const mongoose = require("mongoose")

const tablesSchema = mongoose.Schema({
    restaurant_id : {
        type: String
    },
    table_id : {
        type: String,
        unique: true,
    },
    no_of_sits : {
        type: Number,
        required: true
    },
    status : {
        type: String,
        default: "frei"
    }


})


module.exports = mongoose.model("tables", tablesSchema)