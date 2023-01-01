const mongoose = require("mongoose")

const reservationsSchema = new mongoose.Schema({
    restaurant_id : {
        type: String,
        require: true
    },
    user_id : {
        type: String,
        require: true
    },
    table_id : {
        type: String,
        required: true
    },
    reservation_date : {
        type: Date,
        require: true
    },
    reservation_time : {
        type: String,
        required: true
    }
})

module.exports = mongoose.model('reservations', reservationsSchema)