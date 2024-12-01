from flask import Flask, request, jsonify
from lib.utils import predict_pasal

app = Flask(__name__)

@app.route("/")
def home():
    return "Hello World!"

@app.route("/predict_pasal", methods=['POST'])
def route_predict_pasal():
    data = request.get_json()
    text = data.get("text") 
    pasal = predict_pasal(text)
    
    return jsonify({'text': text, 'pasal': pasal})

if __name__ == "__main__":
   app.run(debug=True)
