// src/helpers/ImageHelper.js
import { BASE_URL } from "../service/api";

class ImageHelper {

    static getAvatarUrl(avatar) {
        if (!avatar) return null;
        return `${BASE_URL}/media/v1/local/${avatar}`;
    }

}

export default ImageHelper;
