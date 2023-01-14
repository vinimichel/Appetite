const express = require('express')
const reservations = require('../models/reservations')
const restaurants = require("../models/restaurants")
const router = express.Router()
const createError = require("http-errors")
const {reservationSchema} = require('../helpers/validation_schema')

//get all reservations
router.get('/', async(req, res, next) => {
    try{
        const allReservations = await reservations.find()
        res.json(allReservations)
    } catch(err) {
        res.status(500).json({ message: err.message})
    }
})

//make a reservation
router.post('/add', async (req, res, next)  => {
    try{
        const result = await reservationSchema.validateAsync(req.body)

        const reservation = new reservations({
            restaurant_id: result.restaurant_id,
            user_id: result.user_id,
            no_of_people: result.no_of_people,
            reservation_date: result.reservation_date,
            reservation_time: result.reservation_time,
        })
        const newReservation = await reservation.save()
        const tables = await updateTable("decrease", result.restaurant_id)
        console.log(tables);
        res.json({message : "Reservation made"})

    } catch(err) {
        if(err.isJoi === true) err.status = 422
        next(err)
    }
    
})


//get a reservation
router.get('/:id', getReservation, (req, res, next)  => {
    res.json(res.reservation)
})


//delete a reservation
router.delete('/delete/:id',getReservation, async (req, res, next)  => {
    try {
        await res.reservation.remove()
        //increase number of tables at the restaurant by 1
        const tables = await updateTable("increase", res.reservation.restaurant_id)
        console.log(tables);
        res.reservation.restaurant_id
        res.json({message : "Reservation deleted"})
    } catch(err) {
        next(err)
    }
})

//search reservations from a particular user
router.get("/user/:id", async(req,res, next) => {
    try{
        let userReservation
        let query = {user_id : req.params.id}
        userReservation = await reservations.find(query)
        res.json(userReservation)
    } catch(err) {
        next(createError.BadRequest("An Error occured. Could not get reservations."))
    }
})


//search reservations made to a particular restaurant
router.get("/restaurant/:id", async(req,res, next) => {
    try{
        let userReservation
        let query = {restaurant_id : req.params.id}
        userReservation = await reservations.find(query)
        res.json(userReservation)
    } catch(err) {
        next(createError.BadRequest("An Error occured. Could not delete reservation."))
    }
})

//middleware
async function getReservation(req, res, next) {
    let reservationDetails
try {
    reservationDetails = await reservations.findById(req.params.id)
    if(reservationDetails == null) {
        next(createError.NotFound("Could not find reservation."))
    }
} catch(err) {
    next(createError.BadRequest("An Error occured."))
}

res.reservation = reservationDetails
next()
}

async function updateTable(action, restaurant_id) {
    console.log("restaurant is " + restaurant_id)
    let restaurant_info, total_tables, updateRestaurant;
    try {
        restaurant_info = await restaurants.findOne({id : restaurant_id})
        if(action == "increase") total_tables = restaurant_info.tables + 1;
        else if(action == "decrease") total_tables = restaurant_info.tables - 1;

        restaurant_info.tables = total_tables;
        updateRestaurant = await restaurant_info.save()

    } catch(err) {
        
    }

    return updateRestaurant

}


module.exports = router