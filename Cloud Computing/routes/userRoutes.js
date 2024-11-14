const express = require('express');
const router = express.Router();
const asycnHandler = require('express-async-handler');
const { addUser, getUserById, updateUserById, deleteUserById } = require('../controllers/userController');

router.post('/', asycnHandler(addUser))
router.get('/:userId', asycnHandler(getUserById))
router.put('/:userId', asycnHandler(updateUserById))
router.delete('/:userId', asycnHandler(deleteUserById))

module.exports = router