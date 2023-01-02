require('dotenv').config()

const express = require('express')
const app = express()
const mongoose = require('mongoose')

mongoose.connect(process.env.DATABASE_URL, {useNewUrlParser: true})
const db = mongoose.connection
db.on('error', (error) => console.error(error))
db.once('open', () => console.log("connected to database"))

app.use(express.json())

const usersRouter = require('./routes/users')
app.use('/users', usersRouter)
const restaurantsRouter = require('./routes/restaurants')
app.use('/restaurants', restaurantsRouter)
const reservationsRouter = require('./routes/reservations')
app.use('/reservations', reservationsRouter)
const menusRouter = require('./routes/menus')
app.use('/menus', menusRouter)
const tablesRouter = require('./routes/tables')
app.use('/tables', tablesRouter)


app.listen(3000, () => console.log("server started"))