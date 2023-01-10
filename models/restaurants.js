const mongoose = require("mongoose")
const bcrypt = require('bcrypt');

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
        password: {
            type: String,
            required: true
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
      Date: {
        type: Date,
        required: false,
        default:Date.now
    }
})

restaurantSchema.pre("save", async function(next) {
    try {
        const salt = await bcrypt.genSalt(12)
        const hashedPassword = await bcrypt.hash(this.password, salt)
        this.password = hashedPassword
        next()
    } catch(err) {
        next(err)
    }
})

restaurantSchema.methods.isValidPassword = async function (password) {
    try{
        return await bcrypt.compare(password, this.password)

    } catch(err) {
        throw err
    }
}


module.exports = mongoose.model('restaurants', restaurantSchema)

//add min and max to the schema later!!!