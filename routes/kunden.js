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
router.get('/:id', (req, res)  => {
    res.send(req.params.id)
})
//create one user
router.post('/', async (req, res)  => {
    const Kunde = new Kunde({
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
router.patch('/:id', (req, res)  => {
    
})
//delete one user
router.delete('/:id', (req, res)  => {
    
})

module.exports = router