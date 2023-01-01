const express = require('express')
const tables = require('../models/tables')
const router = express.Router() 

//get all tables by a specific restaurant
router.get(":/:restaurantId", async (req, res) => {
try {
    let query = {restaurant_id : req.params.restaurantId}
    const restaurantTables = await tables.find(query)
    res.json(restaurantTables)
} catch(err) {
    res.status(500).json({error: err, message: "Could not get tables."})
}
    
})

module.exports = router