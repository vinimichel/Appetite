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
        email: {
            type: String,
            required: true,
            lowercase: true,
            unique: true
        },
        PLZ: {
            type:Number,
            required: true
        },
        tables: {
            type: Number,
            required: true,
            default: 0
        },
        logo: {
            type: String,
            required: false
        },
        /** 
        location: {
            type: String,
            coordinates: []
      }
      */
     longitude: {
        type: String,
        required: true
     },
     latitude: {
        type: String,
        requried: true
     }
})


module.exports = mongoose.model('restaurants', restaurantSchema)

//add min and max to the schema later!!!