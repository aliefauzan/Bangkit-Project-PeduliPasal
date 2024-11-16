const admin = require("firebase-admin");
const db = admin.firestore();
const { nanoid } = require('nanoid');

const createChat = async (req, res) => {
  try {
    // Generate a unique chat ID if not provided
    const chatId = req.body.chatId || nanoid(16);
    const { userId, title } = req.body;

    // Validate request body
    if (!userId || !title) {
      return res.status(400).json({ error: "Data tidak valid" });
    }

    // Add chat document with chatId as document ID
    const chatRef = await admin.firestore().collection('chats').add({
      chatId,
      userId,
      title,
      messages: [],
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    });

    res.status(201).json({ chatId: chatRef.id });
  } catch (error) {
    console.error("Error creating chat:", error);
    res.status(500).json({ error: "Gagal membuat chat" });
  }
};

const addMessageToChat = async (req, res) => {
  try {
    const { chatId } = req.params;
    const { isHuman, content } = req.body;

    if (typeof isHuman !== "boolean" || !content) {
      return res.status(400).json({ error: "Invalid message data" });
    }

    const chatRef = db.collection("chats").doc(chatId);
    const chatDoc = await chatRef.get();

    if (!chatDoc.exists) {
      return res.status(404).json({ error: "Chat not found" });
    }

    const messageRef = chatRef.collection("messages").doc();
    await messageRef.set({
      isHuman,
      content,
      timestamp: new Date().toISOString(),
    });

    res.status(201).json({ message: "Message added to chat" });
  } catch (error) {
    console.error("Error adding message to chat:", error);
    res.status(500).json({ error: "Failed to add message to chat" });
  }
};

const getChatMessageById = async (req, res) => {
  try {
    const { chatId } = req.params;

    const chatRef = db.collection("chats").doc(chatId);
    const chatDoc = await chatRef.get();

    if (!chatDoc.exists) {
      return res.status(404).json({ error: "Chat not found" });
    }

    const messagesRef = chatRef.collection("messages");
    const messagesSnapshot = await messagesRef.orderBy("timestamp").get();

    const messages = messagesSnapshot.docs.map(doc => doc.data());

    res.status(200).json({ chatId, messages });
  } catch (error) {
    console.error("Error fetching chat messages:", error);
    res.status(500).json({ error: "Failed to fetch chat messages" });
  }
};

module.exports = {
  createChat,
  addMessageToChat,
  getChatMessageById,
};
