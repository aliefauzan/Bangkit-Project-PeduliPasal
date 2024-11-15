const express = require('express');
const router = express.Router();
const asycnHandler = require('express-async-handler');
const { 
    createChat,
    addMessageToChat,
    getChatMessageById,
 } = require('../controllers/chatController');

router.post('/',asycnHandler(createChat));
router.post('/:chatId', asycnHandler(addMessageToChat)); 
router.get('/:chatId', asycnHandler(getChatMessageById)); 

module.exports = router