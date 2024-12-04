const admin = require("firebase-admin");
const db = admin.firestore();
const { nanoid } = require('nanoid');
const { GoogleGenerativeAI } = require("@google/generative-ai");

const createChat = async (req, res) => {
  try {
    const chatId = req.body.chatId || nanoid(16);
    const { userId, title } = req.body;

    if (!userId || !title) {
      return res.status(400).json({ error: "Data tidak valid" });
    }

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
    const messageId = nanoid(16);
    const userMessageId = nanoid(16);
    const aiMessageId = nanoid(16);
    const { chatId } = req.params;
    const { content } = req.body;

    if (!content) {
      return res.status(400).json({ error: "Invalid message data" });
    }

    const chatRef = db.collection("chats").doc(chatId);
    const chatDoc = await chatRef.get();

    if (!chatDoc.exists) {
      return res.status(404).json({ error: "Chat not found" });
    }

    const userMessage = {
      userMessageId,
      messageId,
      isHuman: true,
      content,
      timestamp: new Date().toISOString(),
    };
    const userMessageRef = chatRef.collection("messages").doc();
    await userMessageRef.set(userMessage);

    await chatRef.update({
      updatedAt: new Date().toISOString(),
    });

    // Integrasi dengan Google Generative AI
    const genAI = new GoogleGenerativeAI(process.env.API_KEY);
    const model = genAI.getGenerativeModel({ model: "gemini-1.5-flash" });

    const prompt = `Anda adalah asisten hukum yang dirancang untuk membantu pengacara dan mahasiswa hukum memahami dan menavigasi pasal hukum serta peraturan yang relevan. Ketika diberikan deskripsi kasus atau masalah hukum, tugas Anda adalah:
                    1. Mengidentifikasi undang-undang, pasal, dan peraturan yang berlaku
                    2. Memberikan penjelasan singkat tentang implikasi hukum, termasuk hukuman atau konsekuensi
                    3. Gunakan bahasa yang jelas dan ringkas agar mudah dipahami oleh profesional hukum maupun mahasiswa
                    Contoh: Jika pengguna memberikan input, "pelanggaran UU ITE terkait pornografi," jawaban Anda harus mengidentifikasi pasal yang spesifik, seperti:
                    Melanggar Pasal 45 Ayat 1. Pasal 27 Ayat 1 UU RI No. 1 Tahun 2024, dengan ancaman hukuman berupa penjara hingga 12 tahun atau denda sebesar Rp 6 miliar
                    Berikan jawaban hanya dalam bentuk teks biasa tanpa simbol, format markdown, atau karakter spesial. Jika input pengguna ambigu, tanyakan pertanyaan klarifikasi untuk memberikan informasi hukum yang paling akurat
                    Input pengguna: ${content}`;
    

    const result = await model.generateContent(prompt);
    let aiContent = result.response.text();
    
    // Hapus simbol atau format dari teks
    aiContent = aiContent.replace(/[\n\r\t*_*]/g, "").trim();
                    
    // Simpan balasan AI ke subkoleksi "messages"
    const aiMessage = {
      aiMessageId,
      messageId,
      isHuman: false,
      content: aiContent,
      timestamp: new Date().toISOString(),
    };
    const aiMessageRef = chatRef.collection("messages").doc();
    await aiMessageRef.set(aiMessage);

    res.status(201).json({
      message: "Message added to chat",
      userMessage,
      aiMessage,
    });
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

    const chatData = chatDoc.data();
    
    const messagesRef = chatRef.collection("messages");
    const messagesSnapshot = await messagesRef.orderBy("timestamp").get();

    const messages = messagesSnapshot.docs.map(doc => doc.data());

    res.status(200).json({
      chatId,
      userId: chatData.userId,
      title: chatData.title,
      createdAt: chatData.createdAt,
      updatedAt: chatData.updatedAt,
      messages,
    });
  } catch (error) {
    console.error("Error fetching chat messages:", error);
    res.status(500).json({ error: "Failed to fetch chat messages" });
  }
};

const deleteChat = async (req , res) => {
  const { chatId } = req.params

  try{
   await db.collection('chats').doc(chatId).delete()
   const messagesRef = db.collection('chats').doc(chatId).collection('messages');
   const querySnapshot = await messagesRef.get();
   querySnapshot.forEach(async (doc) => {
     await doc.ref.delete();
   });
   res.status(200).json({
     message : 'Chat deleted successfully'
   })
  }catch(error){
   console.error(error);
   res.status(500).json({ message: 'Internal server error' });
  }
}

module.exports = {
  createChat,
  addMessageToChat,
  getChatMessageById,
  deleteChat
}