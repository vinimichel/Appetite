const express = require('express')
const restaurants = require('../models/restaurants')
const router = express.Router()
const {restaurantAuthSchema, 
        restaurantLoginSchema} = require('../helpers/validation_schema')
const {signAccessToken, signRefreshToken, verifyRefreshToken} = require("../helpers/jwt_helper")
const createError = require("http-errors")


router.get("/", async (req, res) => {
    try{
        const allRestaurants = await restaurants.find()
        res.json(allRestaurants)
    } catch(err) {
        res.status(500).json({message: "An error occured"})
    }
})


router.post("/register", async (req, res, next) => {
    try {
        const result = await restaurantAuthSchema.validateAsync(req.body)
        //checks if restaurant already exists
        const doesExist = await restaurants.findOne({email: result.email})
        if(doesExist) throw createError.Conflict(`${result.email} is already registered`)

        const restaurant = new restaurants(result)
        const newRestaurant = await restaurant.save()
        const accessToken = await signAccessToken(newRestaurant.id)
        const refreshToken = await signRefreshToken(newRestaurant.id)
        res.json({accessToken, refreshToken})
        
    } catch(err) {
        if(err.isJoi === true) err.status = 422
        next(err)
        
    }

})

router.post("/login", async (req, res, next) => {
    try{
        const result = await restaurantLoginSchema.validateAsync(req.body)
        const restaurant = await restaurants.findOne({email: result.email})
        if(!restaurant) throw createError.NotFound("Restaurant not found")

        const isMatch = await restaurant.isValidPassword(result.password)
        if(!isMatch) throw createError.Unauthorized("Invalid Email/Password")
        //res.send({userId : user.id})
        const accessToken = await signAccessToken(restaurant.id)
        const refreshToken = await signRefreshToken(restaurant.id)
        res.json({accessToken, refreshToken})
    } catch(err) {
        if(err.isJoi === true) return next(createError.BadRequest("Invalid Email/Password"))
        next(err)
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