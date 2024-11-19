const express = require('express');
const router = express.Router();
const asycnHandler = require('express-async-handler');
const protectRoute = require('../middleware/authMiddleware')
const {
    registerUser,
    loginUser, 
    getUserById, 
    updateUserById, 
    deleteUserById,
    getChatHistoryByUserId
} = require('../controllers/userController');

router.post('/reg', registerUser);
router.post('/login', loginUser);
router.get('/:userId', protectRoute, getUserById);
router.put('/:userId', protectRoute, updateUserById);
router.delete('/:userId',protectRoute, deleteUserById)
router.get('/chats/:userId',protectRoute, getChatHistoryByUserId)

module.exports = router