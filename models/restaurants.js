const mongoose = require("mongoose")

const restaurantSchema = new mongoose.Schema({
        name: {
            type: String,
            required: true
        },
        address: {
            type: String,
            required: true
        },
        PLZ: {
            type:Number,
            required: true
        },
        location: {
            type: {type: String},
            coordinates: []
      }
})

module.exports = mongoose.model('restaurants', restaurantSchema)
