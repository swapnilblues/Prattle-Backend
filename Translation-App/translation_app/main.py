import os
from google.cloud import translate_v2 as translate
from flask import Flask, request
from flask_cors import CORS

os.environ['GOOGLE_APPLICATION_CREDENTIALS'] = r"E:\NEU\CS-FSE\project\FSE-Prattle-7e68c0788ae1.json"

app = Flask(__name__)
CORS(app)
translate_client = translate.Client()

@app.route('/translate',methods=['POST'])
def translateText():
	text = request.json['text']
	target = request.json['target']	
	output = translate_client.translate(
		text,
		target_language=target)

	return output['translatedText']