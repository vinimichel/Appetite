const express = require('express')
const menus = require('../models/menus')
const router = express.Router() 


//get all reservations
router.get('/', async(req, res) => {
    try{
        const allMenus = await menus.find()
        res.json(allMenus)
    } catch(err) {
        res.status(500).json({ message: err.message})
    }
})

//get menu list per restaurant and course (i.e. Vorspeise, Hauptgericht und Dessert)
router.get("/:course/:id", getMenus, (req, res) => {
    res.json(res.menu)
})


//function to get menus
async function getMenus(req, res, next) {
    let menuInfo
    let query = {course : req.params.course, restaurant_id: req.params.id }
try {
    menuInfo = await menus.find(query)
    if(menuInfo == null) {
        return res.status(404).json({message : 'Cannot find menu list'})
    }
} catch(err) {
    return res.status(500).json({message : err.message})
}

res.menu = menuInfo
next()

}


module.exports = router