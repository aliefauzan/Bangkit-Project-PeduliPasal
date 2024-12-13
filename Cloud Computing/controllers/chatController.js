const admin = require("firebase-admin");
const db = admin.firestore();
const { nanoid } = require('nanoid');
const { GoogleGenerativeAI } = require("@google/generative-ai");
const axios = require('axios');

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

    // Save user message
    const userMessage = {
      userMessageId,
      messageId,
      isHuman: true,
      content,
      timestamp: new Date().toISOString(),
    };
    const userMessageRef = chatRef.collection("messages").doc();
    await userMessageRef.set(userMessage);

    // Update chat timestamp
    await chatRef.update({
      updatedAt: new Date().toISOString(),
    });

    // Call Flask API for ML model output
    const flaskResponse = await axios.post(process.env.API_MODEL_RESPONSE, {
      text: content,
    });

    const { generative_result = [], text: userInput } = flaskResponse.data;

    // Prepare prompt for Gemini with Flask API output
    const genAI = new GoogleGenerativeAI(process.env.API_KEY);
    const model = genAI.getGenerativeModel({ model: "gemini-1.5-flash" });

    const prompt = `Anda adalah asisten hukum berbasis AI yang dirancang untuk membantu pengacara dan mahasiswa hukum memahami pasal-pasal hukum yang relevan di Indonesia. Berdasarkan masukan pengguna dan pasal yang diidentifikasi oleh model machine learning, lakukan hal berikut:
                    Input dari pengguna: ${userInput}
                    Pasal yang teridentifikasi oleh model ML: ${generative_result}
                    Tugas Anda:
                    Identifikasi pasal-pasal yang relevan dan berikan penjelasan singkat tentang implikasi hukumnya.
                    Jika generative_result tidak sesuai dengan userInput, langsung berikan pasal yang tepat tanpa menyebutkan kesalahan atau ketidaksesuaian.
                    Format Output:

                    Pasal X: [Deskripsi singkat], Ancaman: [Tahun Penjara], Denda: [Jumlah Denda].
                    Catatan:

                    Jangan menyebutkan kesalahan atau ketidaksesuaian ${generative_result}.
                    Jangan memberikan informasi yang tidak relevan.
                    Gunakan bahasa yang jelas, ringkas, dan langsung pada inti permasalahan.
                    Jika informasi dari pengguna tidak cukup, ajukan pertanyaan tambahan untuk memperjelas konteks.
                    Cukup berikan jawaban singkat
                    Contoh Output:

                    Pasal 362: Barang siapa mengambil barang milik orang lain dengan maksud untuk dimiliki secara melawan hukum, diancam dengan pidana penjara paling lama 5 tahun atau denda paling banyak Rp900.000.
                    Pasal 363: Pencurian yang disertai pemberatan (misalnya dilakukan pada malam hari atau oleh dua orang atau lebih), Ancaman: 7 Tahun Penjara.`;

    const result = await model.generateContent(prompt);
    let aiContent = result.response.text();

    // Remove special characters
    aiContent = aiContent.replace(/[*_*]/g, "").trim();

    // Save AI message
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
    res.status(500).json({ error: "Failed to add message to chat", details: error.message });
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