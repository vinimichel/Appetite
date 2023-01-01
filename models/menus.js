const mongoose = require("mongoose")


const menuSchema = new mongoose.Schema({
    restaurant_id : {
        type: String,
        required: true
    },
    dish : {
        type: String,
        required: true
    },
    course : {
        type: String,
        required: true
        //z.B. Vorspiese, Hauptgericht, Nachspeise, Getränke
    },
    price : {
        type: String
    }
})

module.exports = mongoose.model('menus', menuSchema)

