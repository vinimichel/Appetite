const mongoose = require('mongoose')

const kundenSchema = new mongoose.Schema({
    vorname: {
        type: String,
        required: true
    },
    name: {
        type:String,
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
    Datum: {
        type: Date,
        required: true,
        default:Date.now
    }

})

module.exports = mongoose.model('kunden', kundenSchema)