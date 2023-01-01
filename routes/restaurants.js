const express = require('express')
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
        email: req.body.email,
        PLZ: req.body.PLZ,
        longitude: req.body.longitude,
        latitude: req.body.latitude

    })

    try{
        const newRestaurant = await Restaurant.save()
        res.status(201).json(newRestaurant)

    } catch(err) {
        res.status(400).json({message: err.message})
    }

})

router.get("/:id", getRestaurant, async (req, res) => {
    res.json(res.restaurant)
})

//update resaurant information
router.patch('/:id', getRestaurant, async (req, res)  => {

    if(req.body.name != null) {
        res.restaurant.name = req.body.name
    }
    if(req.body.address != null) {
        res.restaurant.address = req.body.address
     }
    if(req.body.email != null) {
        res.restaurant.email = req.body.email
    }
    if(req.body.PLZ != null) {
        res.restaurant.PLZ = req.body.PLZ
    }
    if(req.body.longitude != null) {
        res.restaurant.longitude = req.body.longitude
    }
    if(req.body.latitude != null) {
        res.restaurant.latitude = req.body.latitude
    }
 
    try {
        const updateRestaurant = await res.restaurant.save()
        res.json(updateRestaurant)
    } catch(err) {
        res.status(400).json({message: "Cannot update restaurant information."})
    }

})

//delete restaurant 
router.delete('/:id',getRestaurant, async (req, res)  => {
    try {
        await res.restaurant.remove()
        res.json({message : "Restaurant deleted"})
    } catch(err) {
        res.status(500).json({error: err, message: "An Error occured. Could not delete restaurant."})
    }
})

//middleware
async function getRestaurant(req, res, next) {
    let restaurantInfo
    try {
        restaurantInfo = await restaurants.findById(req.params.id)
        if(restaurantInfo == null) {
            return res.status(404).json({message : 'Cannot find restaurant'})
        }

    } catch(err) {
        res.status(500).json({message: "Error. Please contact the admin"})
    }

    
    res.restaurant = restaurantInfo
    next()
}

module.exports = router