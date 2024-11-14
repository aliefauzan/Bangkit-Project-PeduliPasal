const admin = require('firebase-admin');
const db = admin.firestore()

const addUser = async (req, res) => {
  const { uid, name, email, } = req.body;

  if (!uid || !name || !email) {
    res.status(400);
    throw new Error("Please add all fields");
  }
  try {
    await db.collection('users').doc(uid).set({
        name: name,
        email: email,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
        role: 'user'
    });
      res.status(201).json({ message: 'User added successfully' });
  } catch (error) {
      console.error(error);
      res.status(500).json({ message: error.message });
  }
}

const getUserById = async (req, res) => {
  const userId = req.params.userId;
    try {
        const user = await db.collection('users').doc(userId).get();
        if (!user.exists) {
            return res.status(404).json({ message: 'User not found' });
        }
        res.json(user.data());
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: 'Internal server error' });
    }
}

const updateUserById = async (req, res) => {
    const userId = req.params.userId;
    const updatedUserData = req.body;

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
}

const deleteUserById = async (req, res) => {
  const userId = req.params.userId;

    try {
        await db.collection('users').doc(userId).delete();

        res.json({ message: 'User deleted successfully' });
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: 'Internal server error' });
    }
}

module.exports = {
  addUser,
  getUserById,
  updateUserById,
  deleteUserById
}
