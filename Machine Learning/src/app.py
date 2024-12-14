from flask import Flask, request, jsonify
# from lib.predict_by_model_1 import predict_pasal
from lib.predict_by_model_2 import predict_pasal
from lib.vertex_search import vertex_search

app = Flask(__name__)

@app.route("/")
def home():
    return "Hello World!"

@app.route("/predict_pasal", methods=['POST'])
def route_predict_pasal():
    data = request.get_json()
    text = data.get("text") 
    
    predicted = predict_pasal(text)
    # print(pasal)
    pasal = vertex_search(search_query=predicted)

    return jsonify({'text': text, 'pasal': pasal})

if __name__ == "__main__":
   app.run(debug=True)
