const express = require('express')
const users = require('../models/users')
const router = express.Router()
const bcrypt = require('bcrypt');

//get all users
router.get('/', async (req, res)  => {
    try{
        const allUsers = await users.find()
        res.json(allUsers)
    } catch(err) {
        res.status(500).json({ message: err.message})
    }
})
//get one user
router.get('/:id', getUser, (req, res)  => {
    res.json(res.user)
})

//create one user
router.post('/auth/register', async (req, res)  => {
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

    try{
        const newUser = await user.save()
        res.status(201).json(newUser)

    } catch(err) {
        res.status(400).json({message: err.message})
    }
    
})

//user login 
router.post("/auth/login", async(req, res) => {


})

//update one user
router.patch('/:id',getUser, async (req, res)  => {

    if(req.body.firstname != null) {
        res.user.firstname = req.body.firstname
    }
    if(req.body.lastname != null) {
        res.user.lastname = req.body.lastname
     }
    if(req.body.PLZ != null) {
        res.user.PLZ = req.body.PLZ
    }
    if(req.body.address != null) {
        res.user.address = req.body.address
    }
    if(req.body.email != null) {
        res.user.email = req.body.email
    }
 
    try {
        const updateUser = await res.user.save()
        res.json(updateUser)
    } catch(err) {
        res.status(400).json({message: "Cannot update user informattion."})
    }

})
//delete one user
router.delete('/:id',getUser, async (req, res)  => {
    try {
        await res.user.remove()
        res.json({message : "Deleted user"})
    } catch(err) {
        res.status(500).json({error: err, message: "An Error occured. Could not delete user account."})
    }
})


//middleware
async function getUser(req, res, next) {
    let userInfo
try {
    userInfo = await users.findById(req.params.id)
    if(userInfo == null) {
        return res.status(404).json({message : 'Cannot find user'})
    }
} catch(err) {
    return res.status(500).json({message : err.message})
}

res.user = userInfo
next()
}

module.exports = router

/**
 * z.B. kunden.find({PLZ: "36039"})
 * By applying a $near filter on the location field,
 *  we can find all points within 100km (100000 meters) of
 *  [-79.3832, 43.6532].
 */