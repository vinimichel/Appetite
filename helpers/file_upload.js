const util = require("util");
const multer = require("multer");
const maxSize = 5 * 1024 * 1024;


let newFileName;
let storage = multer.diskStorage({
  destination: (req, file, cb) => {
    cb(null,"./assets/uploads/");
  },
  filename: (req, file, cb) => {
    newFileName = Date.now() + "---" + file.originalname
    cb(null, newFileName);
  },
});

let uploadFile = multer({
  storage: storage,
  limits: { fileSize: maxSize },
}).single("image");

let uploadFileMiddleware = util.promisify(uploadFile);
module.exports = uploadFileMiddleware;