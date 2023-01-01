const mongoose = require('mongoose');
const bcrypt = require('bcrypt');

const usersSchema = new mongoose.Schema({
    firstname: {
        type: String,
        required: true
    },
    lastname: {
        type:String,
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
        required: false
    },
    PLZ: {
        type:Number,
        required: true
    },
    Date: {
        type: Date,
        required: true,
        default:Date.now
    }

})

/**
usersSchema.pre("save", async function(next) {
    try {
        const salt = await bcrypt.genSalt(12)
        const hashedPassword = await bcrypt.hash(this.password, salt)
        this.password = hashedPassword
        next()
    } catch(err) {
        next(err)
    }
})

*/

module.exports = mongoose.model('users', usersSchema)