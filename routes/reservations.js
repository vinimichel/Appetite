const express = require('express')
const reservations = require('../models/reservations')
const router = express.Router()

//get all reservations
router.get('/', async(req, res) => {
    try{
        const allReservations = await reservations.find()
        res.json(allReservations)
    } catch(err) {
        res.status(500).json({ message: err.message})
    }
})

//make a reservation
router.post('/', async (req, res)  => {
    const reservation = new reservations({
        restaurant_id: req.body.restaurant_id,
        user_id: req.body.user_id,
        table_id: req.body.table_id,
        reservation_date: req.body.reservation_date,
        reservation_time: req.body.reservation_time,
    })

    try{
        const newReservation = await reservation.save()
        res.status(201).json(newReservation)

    } catch(err) {
        res.status(400).json({message: err.message})
    }
    
})


//get a reservation
router.get('/:id', getReservation, (req, res)  => {
    res.json(res.reservation)
})


//delete a reservation
router.delete('/:id',getReservation, async (req, res)  => {
    try {
        await res.reservation.remove()
        res.json({message : "Reservation deleted"})
    } catch(err) {
        res.status(500).json({error: err, message: "An Error occured. Could not delete reservation."})
    }
})

//search reservations from a particular user
router.get("/user/:id", async(req,res) => {
    try{
        let userReservation
        let query = {user_id : req.params.id}
        userReservation = await reservations.find(query)
        res.json(userReservation)
    } catch(err) {
        res.status(500).json({error: err, message: "Could not get reservation."})
    }
})


//search reservations made to a particular restaurant
router.get("/restaurant/:id", async(req,res) => {
    try{
        let userReservation
        let query = {restaurant_id : req.params.id}
        userReservation = await reservations.find(query)
        res.json(userReservation)
    } catch(err) {
        res.status(500).json({error: err, message: "Could not get reservation."})
    }
})

//middleware
async function getReservation(req, res, next) {
    let reservationDetails
try {
    reservationDetails = await reservations.findById(req.params.id)
    if(reservationDetails == null) {
        return res.status(404).json({message : 'Cannot find reservation'})
    }
} catch(err) {
    return res.status(500).json({message : err.message})
}

res.reservation = reservationDetails
next()
}


module.exports = router