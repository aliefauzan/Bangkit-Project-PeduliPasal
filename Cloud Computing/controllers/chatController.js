const admin = require('firebase-admin');
const db = admin.firestore()

const createChat = async (req, res) => {
  try {
    const { userId, title, messages } = req.body;

    if (!userId || !title || !messages || !Array.isArray(messages)) {
      return res.status(400).json({ error: 'Data tidak valid' });
    }

    const chatRef = await admin.firestore().collection('chats').add({
      userId,
      title,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    });

    const batch = admin.firestore().batch();
    messages.forEach(message => {
      const messageRef = chatRef.collection('messages').doc();
      batch.set(messageRef, {
        ...message,
        timestamp: new Date().toISOString()
      });
    });
    await batch.commit();

    res.status(201).json({ chatId: chatRef.id });
  } catch (error) {
    console.error('Error creating chat:', error);
    res.status(500).json({ error: 'Gagal membuat chat' });
  }
};
module.exports = {
  createChat
}
