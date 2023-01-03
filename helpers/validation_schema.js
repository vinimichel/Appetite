const Joi = require('@hapi/joi')
const authSchema = Joi.object({
    email: Joi.string().email().lowercase().required(),
    password: Joi.string().min(8).required(),
    firstname: Joi.string().required(),
    lastname: Joi.string().required(),
    address: Joi.string(),
    PLZ: Joi.number().integer(),
})

const loginSchema = Joi.object({
    email: Joi.string().email().lowercase().required(),
    password: Joi.string().min(8).required(),
})

module.exports = {
    authSchema,
    loginSchema
}