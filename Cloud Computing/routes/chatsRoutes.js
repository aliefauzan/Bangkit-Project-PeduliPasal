const express = require('express');
const router = express.Router();
const asycnHandler = require('express-async-handler');
const { 
    createChat,
    addMessageToChat,
    getChatMessageById,
    deleteChat,
 } = require('../controllers/chatController');

const protectRoute = require('../middleware/authMiddleware')

router.post('/',asycnHandler(createChat));
router.post('/:chatId', asycnHandler(addMessageToChat)); 
router.get('/:chatId', asycnHandler(getChatMessageById)); 
router.delete('/:chatId', protectRoute, asycnHandler(deleteChat))


module.exports = router