const express = require('express');
const router = express.Router();
const asycnHandler = require('express-async-handler');
const { createChat } = require('../controllers/chatController')

router.post('/',asycnHandler(createChat))

module.exports = router