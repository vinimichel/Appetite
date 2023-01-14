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

const reservationSchema = Joi.object({
    restaurant_id: Joi.string().required(),
    user_id: Joi.string().required(),
    no_of_people: Joi.number().integer().required(),
    reservation_date: Joi.string().required(),
    reservation_time: Joi.string().required(),
})

const restaurantAuthSchema = Joi.object({
    name: Joi.string().required(),
    email: Joi.string().email().lowercase().required(),
    password: Joi.string().min(8).required(),
    address: Joi.string(),
    PLZ: Joi.number().integer(),
})

const restaurantLoginSchema = Joi.object({
    email: Joi.string().email().lowercase().required(),
    password: Joi.string().min(8).required(),
})

module.exports = {
    authSchema,
    loginSchema,
    reservationSchema,
    restaurantAuthSchema,
    restaurantLoginSchema
}