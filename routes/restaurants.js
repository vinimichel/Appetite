const express = require('express')
const kunden = require('../models/kunden')
const restaurants = require('../models/restaurants')
const router = express.Router()


router.get("/", async (req, res) => {
    try{
        const allRestaurants = await restaurants.find()
        res.json(allRestaurants)
    } catch(err) {
        res.status(500).json({message: "An error occured"})
    }
})


router.post("/", async (req, res) => {
    const Restaurant = new restaurants({
        name: req.body.name,
        address: req.body.address,
        PLZ: req.body.PLZ,
        location: req.body.location,
    })

    try{
        const neuRestaurant = await Restaurant.save()
        res.status(201).json(neuRestaurant)

    } catch(err) {
        res.status(400).json({message: err.message})
    }

})

router.get("/:id", getRestaurant, async (req, res) => {
    res.json(res.restaurant)
})

async function getRestaurant(req, res, next) {
    let restaurant
    try {
        restaurant = await restaurants.findById(req.params.id)
        if(restaurant == null) {
            return res.status(404).json({message : 'Cannot find restaurant'})
        }

    } catch(err) {
        res.status(500).json({message: "Error. Please contact the admin"})
    }

    
    res.restaurant = restaurant
    next()
}

module.exports = router