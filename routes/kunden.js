const express = require('express')
const kunden = require('../models/kunden')
const router = express.Router()

//get all users
router.get('/', async (req, res)  => {
    try{
        const alleKunden = await kunden.find()
        res.json(alleKunden)
    } catch(err) {
        res.status(500).json({ message: err.message})
    }
})
//get one user
router.get('/:id', getKunde, (req, res)  => {
    res.json(res.kunde)
})
//create one user
router.post('/', async (req, res)  => {
    const Kunde = new kunden({
        vorname: req.body.vorname,
        name: req.body.name,
        address: req.body.address,
        PLZ: req.body.PLZ,
    })

    try{
        const neuKunde = await Kunde.save()
        res.status(201).json(neuKunde)

    } catch(err) {
        res.status(400).json({message: err.message})
    }
    
})
//update one user
router.patch('/:id',getKunde, async (req, res)  => {

    if(req.body.vorname != null) {
        res.kunde.vorname = req.body.vorname
    }
    if(req.body.name != null) {
        res.kunde.name = req.body.name
     }
    if(req.body.PLZ != null) {
        res.kunde.PLZ = req.body.PLZ
    }
    if(req.body.address != null) {
        res.kunde.address = req.body.address
    }
 
    try {
        const updateUser = await res.kunde.save()
        res.json(updateUser)
    } catch(err) {
        res.status(400).json({message: "Cannot update user informattion."})
    }

})
//delete one user
router.delete('/:id',getKunde, async (req, res)  => {
    try {
        await res.kunde.remove()
        res.json({message : "Deleted user"})
    } catch(err) {
        res.status(500).json({error: err, message: "An Error occured. Could not delete user account."})
    }
})


//middleware
async function getKunde(req, res, next) {
    let kundeInfo
try {
    kundeInfo = await kunden.findById(req.params.id)
    if(kundeInfo == null) {
        return res.status(404).json({message : 'Cannot find user'})
    }
} catch(err) {
    return res.status(500).json({message : err.message})
}

res.kunde = kundeInfo
next()
}

module.exports = router

/**
 * z.B. kunden.find({PLZ: "36039"})
 * By applying a $near filter on the location field,
 *  we can find all points within 100km (100000 meters) of
 *  [-79.3832, 43.6532].
 */