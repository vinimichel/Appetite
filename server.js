require('dotenv').config()

const express = require('express')
const app = express()
const mongoose = require('mongoose')
const morgan = require('morgan')
const createError = require('http-errors')

mongoose.connect(process.env.DATABASE_URL, {useNewUrlParser: true})
const db = mongoose.connection
db.on('error', (error) => console.error(error))
db.once('open', () => console.log("connected to database"))

app.use(express.json())

const kundenRouter = require('./routes/kunden')
app.use('/kunden', kundenRouter)
const restaurantRouter = require('./routes/restaurants')
app.use('/restaurants', restaurantRouter)

app.use(async (req, res, next) => {
    const error = new Error("Not found")
    error.status = 404
    next(error)
})

app.use((err, req, res, next) => {
    res.sendStatus(err.status || 500)
    res.send({
        status: err.status || 500,
        message: err.message
    })
})

app.listen(3000, () => console.log("server started"))