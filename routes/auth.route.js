const express = require("express")
const router = express.Router()
const createError = require("http-errors")
const bcrypt = require('bcrypt');
const users = require('../models/users')
const {authSchema} = require('../helpers/validation_schema')

router.post("/register", async (req, res, next) => {

    try{
        //use Joi to perform validations
        const result = await authSchema.validateAsync(req.body)
       console.log(result)
        //checks if user already exists
       const doesExist = await users.findOne({email: result.email})
       if(doesExist) throw createError.Conflict(`${result.email} is already registered`)
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
        if(err.isJoi === true) err.status = 422
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