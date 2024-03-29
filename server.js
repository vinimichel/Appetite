require('dotenv').config()

const express = require('express')
const app = express()
const mongoose = require('mongoose')
const morgan = require("morgan")
const createErrors = require("http-errors")
const {verifyAccessToken} = require("./helpers/jwt_helper")

mongoose.connect(process.env.DATABASE_URL, {useNewUrlParser: true})
const db = mongoose.connection
db.on('error', (error) => console.error(error))
db.once('open', () => console.log("connected to database"))
db.on('disconnected', () => {
    console.log('Connection is disconnected')
})

process.on("SIGINT", async() => {
    await mongoose.connection.close()
    process.exit(0)
})

app.use(express.json())
app.use(morgan('dev'))

app.get("/",verifyAccessToken, async (req, res, next) => {
    //console.log(req.headers.authorization)
    res.send("Welcome to the appetite app!!")
})
const authRoute = require("./routes/auth.route")
app.use('/auth', authRoute)
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
const imageUploadRouter = require('./routes/image_upload')
app.use('/upload', imageUploadRouter)

app.use(async(req, res, next) => {
    next(createErrors.NotFound("This page does not exist"))
})

app.use((err, req, res, next) => {
    res.status(err.status || 500)
    res.send({
        error : {
            status : err.status || 500,
            message : err.message,
        },
    })
})


app.listen(3000, () => console.log("server started"))