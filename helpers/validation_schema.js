const Joi = require('@hapi/joi')
const authSchema = Joi.object({
    email: Joi.string().email().lowercase().required(),
    password: Joi.string().min(8).required(),
    firstname: Joi.string().required(),
    lastname: Joi.string().required(),
    address: Joi.string(),
    PLZ: Joi.number().integer(),
})

module.exports = {
    authSchema
}