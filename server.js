require('dotenv').config()

const express = require('express')
const app = express()
const mongoose = require('mongoose')

mongoose.connect(process.env.DATABASE_URL, {useNewUrlParser: true})
const db = mongoose.connection
db.on('error', (error) => console.error(error))
db.once('open', () => console.log("connected to database"))

app.use(express.json())

const kundenRouter = require('./routes/kunden')
app.use('/kunden', kundenRouter)
const restaurantRouter = require('./routes/restaurants')
app.use('/restaurants', restaurantRouter)

app.listen(3000, () => console.log("server started"))