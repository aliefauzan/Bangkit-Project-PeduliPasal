const express = require('express');
const dotenv = require('dotenv').config();
const admin = require('firebase-admin');
const { errorHandler }  = require('./middleware/errorMiddleware')

const app = express();

const PORT = process.env.PORT || 8080;

admin.initializeApp({
    credential : admin.credential.cert('./serviceAccount.json'),
    databaseURL : 'https://pedulipasal.firebaseio.com'
});

app.use(express.json());
app.use('/api/users', require('./routes/userRoutes'))
app.use('/api/chats', require('./routes/chatsRoutes'));
app.use(errorHandler);

app.listen(PORT , () => {
    console.log(`Server listening on port ${PORT}`);
});