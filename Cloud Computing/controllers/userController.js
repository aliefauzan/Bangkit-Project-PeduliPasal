const admin = require('firebase-admin');
const {nanoid} = require('nanoid');
const bcrypt = require('bcrypt');
const db = admin.firestore();
const jwt = require('jsonwebtoken');
const asycnHandler = require('express-async-handler')

const registerUser = asycnHandler(async (req, res) => {
  const { name, email, password } = req.body;

  if (!name || !email || !password){
    res.status(400)
    throw new Error('Please add all fields');
  }

  const user = await db.collection('users').where('email', '==', email).get();
  if (!user.empty) {
    return res.status(400).json({ message: 'User already exist' })
  }

  const userId = nanoid(15).toUpperCase();
  const salt = await bcrypt.genSalt(10);
  const hashedPassword = await bcrypt.hash(password, salt);

  try {
    await db.collection('users').doc(userId).set({
        name: name,
        email: email,
        password : hashedPassword,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
        role: 'user'
    });
    res.status(201).json(
      { 
        message: 'User created successfully',
        userId : userId,
      });
  } catch (error) {
      console.error(error);
      res.status(500).json({ message: error.message });
  }
})

const loginUser = asycnHandler(async (req, res) => {
  const { email, password } = req.body;
  const user = await db.collection('users').where('email', '==', email).get();
  
  if (user.empty) {
    return res.status(401).json({ error: 'User not found' });
  }

  const userDoc = user.docs[0];
  const storedPassword = userDoc.data().password;
  const isMatch = await bcrypt.compare(password, storedPassword);

  if (!isMatch) {
    return res.status(401).json({ error: 'Invalid password' });
  }

  const userId = user.docs[0].id
  return res.status(200).json({
    message : "Login Successfully",
    userId : userId,
    token   : generateToken(userId)
  })
})

const getUserById = asycnHandler(async (req, res) => {
  const userId = req.params.userId;
    try {
        const user = await db.collection('users').doc(userId).get();
        if (!user.exists) {
            return res.status(404).json({ message: 'User not found' });
        }
        const { name , email, role } = user.data()
        res.json({
          name,email,role
        });
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: 'Internal server error' });
    }
})

const updateUserById = asycnHandler (async (req, res) => {
    const userId = req.params.userId;
    const updatedUserData = req.body;

    if (req.body.hasOwnProperty('password')){
      return res.status(400).json({ message : 'Password cannot be updated through this endpoint'})
    }
    try {
        const userRef = db.collection('users').doc(userId);
        const userDoc = await userRef.get();

        if (!userDoc.exists) {
            return res.status(404).json({ message: 'User not found' });
        }
        await userRef.update({
          ...updatedUserData,
          updatedAt: new Date().toISOString()
        });
        res.json({ message: 'User updated successfully' });

    } catch (error) {
        console.error(error);
        res.status(500).json({ message: 'Internal server error' });
    }
})

const deleteUserById = asycnHandler (async (req, res) => {
  const userId = req.params.userId;

    try {
        await db.collection('users').doc(userId).delete();

        res.json({ message: 'User deleted successfully' });
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: 'Internal server error' });
    }
})

const getChatHistoryByUserId = asycnHandler (async (req, res) => {
  try {
    const { userId } = req.params;

    const chatQuery = db.collection('chats')
      .where('userId', '==', userId);

    const chatDocs = await chatQuery.get();

    if (chatDocs.empty) {
      return res.status(200).json({ chats: [] }); 
    }

    const chats = chatDocs.docs.map(doc => ({
      chatId: doc.id,
      title: doc.data().title,
      createdAt: doc.data().createdAt,
      updatedAt: doc.data().updatedAt
    }));

    res.status(200).json({ chats });
  } catch (error) {
    res.status(500).json({ error: "Internal Server Error" });
  }
})

const generateToken = (id) => {
  return jwt.sign({ id }, process.env.JWT_SECRET, {
    expiresIn: '30d'
  });
};

module.exports = {
  registerUser,
  loginUser,
  getUserById,
  updateUserById,
  deleteUserById,
  getChatHistoryByUserId
}