import os
import random
from datetime import datetime
from flask import Flask, request

app = Flask(__name__)

UPLOAD_FOLDER = os.path.join(os.getcwd(), "static")
ALLOWED_EXTENSIONS = {'jpg', 'png', 'jpeg'}

app.config["UPLOAD_FOLDER"] = UPLOAD_FOLDER

def allowed_file(filename):
    return '.' in filename and \
        filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

@app.route("/upload", methods=["POST"])
def upload_file():
    if 'file' not in request.files:
        app.logger.error("No file part found")
    category = request.form.get("category", None)
    if category is None:
        return {
            "status": "FAILURE",
            "message": "`category` part is mandatory"
        }, 400
    file = request.files['file']
        
    if file:
        now = str(datetime.utcnow().timestamp()).split(".")[0]
        randint = str(random.randint(1, 10000))
        filename = now + "_" + randint
        filename = category + "/" + filename + ".png"

        try:
            os.makedirs(UPLOAD_FOLDER + '/' + category)
        except FileExistsError:
            pass
        try:
            filepath = os.path.join(UPLOAD_FOLDER, filename)
            file.save(filepath)
        except FileNotFoundError:
            return {
                "status": "FAILURE",
                "message": f"category {category} does not exist"
            }, 400
        return {
            "status": "SUCCESS"
        }
    return {
        "status": "FAILURE",
        "message": "`file` part is mandatory"
    }, 400


if __name__ == "__main__":
    app.run(
        host="0.0.0.0"
    )