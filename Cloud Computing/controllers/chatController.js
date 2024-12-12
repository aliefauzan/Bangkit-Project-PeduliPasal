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

    // Call ML model endpoint first
    const mlModelResponse = await axios.post(process.ENV.API_MODEL_RESPONSE, {
      text: content
    });

    const { pasal, text } = mlModelResponse.data;

    // Prepare prompt for Gemini with ML model output
    const genAI = new GoogleGenerativeAI(process.env.API_KEY);
    const model = genAI.getGenerativeModel({ model: "gemini-1.5-flash" });

    const prompt = `Anda adalah asisten hukum berbasis AI yang dirancang untuk membantu pengacara dan mahasiswa hukum memahami serta menavigasi pasal-pasal hukum dan peraturan terkait.
                    Input dari pengguna: ${content},
                    Pasal yang teridentifikasi oleh model ML: ${pasal.join(', ')},
                    Tugas Anda:
                    1. Konfirmasi atau tambahkan pasal yang relevan: Berdasarkan masukan pengguna dan pasal yang telah diidentifikasi oleh model machine learning, pastikan pasal yang sesuai atau tambahkan pasal lainnya jika diperlukan.
                    2. Berikan penjelasan singkat: Sampaikan implikasi hukum dari pasal-pasal yang relevan menggunakan bahasa yang jelas, ringkas, dan mudah dipahami.
                    3. Tampilkan daftar pasal terkait: Berikan daftar pasal-pasal pidana Indonesia yang relevan beserta ancaman pidananya (tahun penjara dan/atau denda), dalam format berikut:
                      - Pasal X: [Deskripsi singkat], Ancaman: [Tahun Penjara], Denda: [Jumlah Denda].
                      - Pasal Y: [Deskripsi singkat], Ancaman: [Tahun Penjara], Denda: [Jumlah Denda].
                    4. Kategorisasi sub-klasifikasi: Jika sub-klasifikasi hukum tidak dapat ditentukan, tanyakan lebih lanjut kepada pengguna untuk mendapatkan rincian yang lebih spesifik.
                    5. Tangani input ambigu:
                      - Jika input ambigu atau model machine learning gagal memberikan pasal yang spesifik, jangan tampilkan output model sebelumnya.
                      - Langsung hubungkan input dengan pasal yang relevan berdasarkan pemahaman Anda, lalu ajukan pertanyaan tambahan untuk memperjelas konteks atau kebutuhan pengguna.  
                      - Hindari menyebutkan bahwa output model machine learning tidak informatif. Sebagai gantinya, beri respons berdasarkan pemahaman hukum Anda dan tanyakan informasi tambahan yang diperlukan dari pengguna.
                    Catatan:
                      - Jawaban harus dalam teks biasa tanpa simbol atau format khusus.
                      - Jangan tampilkan input pengguna dalam output.
                    Contoh output:
                      - Pasal X: [Deskripsi singkat], Ancaman: [Tahun Penjara], Denda: [Jumlah Denda].
                      - Pasal Y: [Deskripsi singkat], Ancaman: [Tahun Penjara], Denda: [Jumlah Denda].
                    Jika Anda memiliki pertanyaan tambahan, sertakan pertanyaan di bagian akhir respons.`;
                      
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