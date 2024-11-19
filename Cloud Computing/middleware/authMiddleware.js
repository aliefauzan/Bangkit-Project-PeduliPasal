const jwt = require("jsonwebtoken");
const asycnHandler = require("express-async-handler");
const admin = require('firebase-admin');
const db = admin.firestore();

const protectRoute = asycnHandler(async (req, res, next) => {
  let token;

  if (
    req.headers.authorization &&
    req.headers.authorization.startsWith("Bearer")
  ) {
    try {
      token = req.headers.authorization.split(" ")[1];
      const decode = jwt.verify(token, process.env.JWT_SECRET);
      // const userDoc = await db.collection('users').doc(decode.id).get();
      next()
    } catch (err) {
      console.log(err);
      res.status(401);
      throw new Error("Not authorized");
    }
  }
  if(!token){
    res.status(401)
    throw new Error('Not authorized, no token')
  }
});

module.exports = protectRoute;