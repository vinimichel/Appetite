const express = require('express')
const router = express.Router()
const restaurants = require('../models/restaurants')
const uploadMiddleware = require("../helpers/file_upload")

router.post("/images",uploadMiddleware, async(req, res) => {
    //console.log("id : " + req.body.id)
    let myquery = { "_id" : req.body.id };
    let newvalues = { $set: { "logo" : req.file.filename } };
    let update = restaurants.updateOne(myquery, newvalues).then((obj) => {
        res.json({message : "Logo uploaded successfully"})
    })
    .catch((err) => {
        console.log('Error: ' + err);
    })

})

module.exports = router