const express = require("express")
const router = express.Router()
const createError = require("http-errors")
const bcrypt = require('bcrypt');
const users = require('../models/users')

router.post("/register", async (req, res, next) => {

    try{
        //checks if the required fields have been filled by user
        const {email, password, firstname, lastname} = req.body
        if(!email || !password || !firstname || !lastname) throw createError.BadRequest()
       //checks if user already exists
       const doesExist = await users.findOne({email: email})
       if(doesExist) throw createError.Conflict(`${email} is already registered`)
       //salt and hash password
       const salt = await bcrypt.genSalt(12)
        const hashedPassword = await bcrypt.hash(req.body.password, salt)
        const newPassword = hashedPassword
    const user = new users({
        firstname: req.body.firstname,
        lastname: req.body.lastname,
        email: req.body.email,
        password: newPassword,
        address: req.body.address,
        PLZ: req.body.PLZ,
    })
        const newUser = await user.save()
        res.status(201).json(newUser)

    } catch(err) {
        next(err)
    }
})

router.post("/login", async (req, res, next) => {

})

router.post("/refresh-token", async (req, res, next) => {

})

router.delete("/logout", async (req, res, next) => {

})

module.exports = router