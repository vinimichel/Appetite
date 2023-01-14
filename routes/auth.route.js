const express = require("express")
const router = express.Router()
const createError = require("http-errors")
const users = require('../models/users')
const {authSchema, loginSchema} = require('../helpers/validation_schema')
const {signAccessToken, signRefreshToken, verifyRefreshToken} = require("../helpers/jwt_helper")

router.post("/user/register", async (req, res, next) => {
    try{
        //use Joi to perform validations
        const result = await authSchema.validateAsync(req.body)
        //checks if user already exists
       const doesExist = await users.findOne({email: result.email})
       if(doesExist) throw createError.Conflict(`${result.email} is already registered`)
       
    const user = new users(result)
        const newUser = await user.save()
        const accessToken = await signAccessToken(newUser.id)
        const refreshToken = await signRefreshToken(newUser.id)
        res.json({accessToken, refreshToken})

    } catch(err) {
        if(err.isJoi === true) err.status = 422
        next(err)
    }
})

router.post("/user/login", async (req, res, next) => {
    try{
        const result = await loginSchema.validateAsync(req.body)
        const user = await users.findOne({email: result.email})
        if(!user) throw createError.NotFound("User not found")

        const isMatch = await user.isValidPassword(result.password)
        if(!isMatch) throw createError.Unauthorized("Invalid Email/Password")
        //res.send({userId : user.id})
        const accessToken = await signAccessToken(user.id)
        const refreshToken = await signRefreshToken(user.id)
        res.json({accessToken, refreshToken})
    } catch(err) {
        if(err.isJoi === true) return next(createError.BadRequest("Invalid Email/Password"))
        next(err)
    }

})

router.post("/refresh-token", async (req, res, next) => {
try {
    const {refreshToken} = req.body
    if(!refreshToken) throw createError.BadRequest()
    const userId = await verifyRefreshToken(refreshToken)

    const accessToken = await signAccessToken(userId)
    const newRefreshToken = await signRefreshToken(userId)

    res.send({accessToken, newRefreshToken})

} catch(err) {
    next(err)
}
})

router.delete("/user/logout", async (req, res, next) => {

})



module.exports = router 